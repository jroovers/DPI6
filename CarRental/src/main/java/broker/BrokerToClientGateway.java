package broker;

import java.util.HashMap;
import java.util.Map;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import messaging.gateway.MessageReceiverGateway;
import messaging.gateway.MessageSenderGateway;
import model.query.ClientQueryReply;
import model.query.ClientQueryRequest;
import model.serializer.QuerySerializer;

/**
 *
 * @author Jeroen Roovers
 */
abstract class BrokerToClientGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private QuerySerializer serializer;

    private static final String CLIENTQUERY_QUEUE_DEFAULT = "ClientQueryRequestQueue";
    private Map<ClientQueryRequest, Message> tempStorage;

    public BrokerToClientGateway() {
        serializer = new QuerySerializer();
        tempStorage = new HashMap<>();
        try {
            sender = new MessageSenderGateway();
            receiver = new MessageReceiverGateway(CLIENTQUERY_QUEUE_DEFAULT);
            // Set listener
            receiver.setListener((Message message) -> {
                System.out.println("Broker received message from client");
                try {
                    String corrID = message.getJMSCorrelationID();
                    String body = ((TextMessage) message).getText();
                    ClientQueryRequest request = serializer.requestFromString(body);
                    tempStorage.put(request, message);
                    onQueryRequestArrived(request);
                } catch (JMSException ex) {
                    System.out.println("Error while receiving ClientQueryRequest");
                }
            });
        } catch (JMSException ex) {
            System.out.println("Error while setting up message-gateways. Is ActiveMQ running?");
        }
    }

    public void sendQueryReply(ClientQueryRequest request, ClientQueryReply reply) {
        try {
            String body = serializer.replyToString(reply);
            Message replyMessage = sender.createTextMessage(body);
            Message requestMessage = tempStorage.get(request);

            // fetch original id and set return address
            String jmsid = requestMessage.getJMSMessageID();
            Destination replyAddress = requestMessage.getJMSReplyTo();
            if (requestMessage.getJMSMessageID() == null) {
                throw new NullPointerException("jmsid was not found in map in method sendBankReply");
            }
            replyMessage.setJMSCorrelationID(jmsid);
            sender.Send(replyAddress, replyMessage);
            tempStorage.remove(request);
        } catch (JMSException ex) {
            System.out.println("Failed to set correlation ID in sendLoanReply");
        }
    }

    /**
     * This method is called when a message is received. The corresponding
     * request is fetched by the app gateway.
     *
     * @param request contains the original request
     * @param reply contains the reply
     */
    abstract public void onQueryRequestArrived(ClientQueryRequest request);
}
