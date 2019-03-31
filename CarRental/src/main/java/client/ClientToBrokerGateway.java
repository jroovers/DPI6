package client;

import java.util.HashMap;
import java.util.Map;
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
abstract class ClientToBrokerGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private QuerySerializer serializer;

    private static final String CLIENTQUERY_QUEUE_DEFAULT = "ClientQueryRequestQueue";
    private Map<String, ClientQueryRequest> tempStorage;

    public ClientToBrokerGateway() {
        serializer = new QuerySerializer();
        tempStorage = new HashMap<>();

        try {
            sender = new MessageSenderGateway(CLIENTQUERY_QUEUE_DEFAULT);
            receiver = new MessageReceiverGateway();
            // Set listener
            receiver.setListener((Message message) -> {
                System.out.println("Client received message from broker");
                try {
                    // Receive reply
                    String body = ((TextMessage) message).getText();
                    ClientQueryReply reply = serializer.replyFromString(body);

                    // Do the JMS correlation thing
                    String corrId = message.getJMSCorrelationID();
                    ClientQueryRequest request = tempStorage.get(corrId);
                    tempStorage.remove(corrId);
                    onClientReplyArrived(request, reply);
                } catch (JMSException ex) {
                    System.out.println("Error while receiving ClientQueryReply");
                }
            });
        } catch (JMSException ex) {
            System.out.println("Error while setting up message-gateways. Is ActiveMQ running?");
        }
    }

    public void sendQuery(ClientQueryRequest request) {
        try {
            String body = serializer.requestToString(request);
            Message msg = sender.createTextMessage(body);
            msg.setJMSReplyTo(receiver.getDestination());

            // send and keep track of original message
            sender.Send(msg);
            tempStorage.put(msg.getJMSMessageID(), request);
        } catch (JMSException ex) {
            System.out.println("Could not read messageID from message to send");
        }
    }

    /**
     * This method is called when a message is received. The corresponding
     * request is fetched by the app gateway.
     *
     * @param request contains the original request
     * @param reply contains the reply
     */
    abstract public void onClientReplyArrived(ClientQueryRequest request, ClientQueryReply reply);
}
