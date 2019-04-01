/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dealer;

/**
 *
 * @author Jeroen Roovers <jroovers>
 */
import java.util.HashMap;
import java.util.Map;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import messaging.gateway.MessageReceiverGateway;
import messaging.gateway.MessageSenderGateway;
import model.answer.DealerQueryRequest;
import messaging.serializer.DealerSerializer;
import model.answer.DealerQueryReply;

abstract class DealerToBrokerGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private DealerSerializer serializer;

    // Helper map to keep track of messages we have received.
    private Map<DealerQueryRequest, Message> tempStorage;

    public DealerToBrokerGateway(String queueToListenOn) {
        serializer = new DealerSerializer();
        tempStorage = new HashMap<>();
        try {
            sender = new MessageSenderGateway();
            receiver = new MessageReceiverGateway(queueToListenOn);
            receiver.setListener((Message message) -> {
                System.out.println("Dealer received message from broker");
                try {
                    String body = ((TextMessage) message).getText();
                    DealerQueryRequest request = serializer.requestFromString(body);
                    tempStorage.put(request, message);
                    onDealerRequestArrived(request);
                } catch (JMSException ex) {
                    System.out.println("Error while receiving request");
                }
            });
        } catch (JMSException ex) {
            System.out.println("Error while setting up message-gateways. Is ActiveMQ running?");
        }
    }

    public void sendDealerReply(DealerQueryRequest request, DealerQueryReply reply) {
        try {
            String body = serializer.replyToString(reply);
            Message replyMessage = sender.createTextMessage(body);
            Message requestMessage = tempStorage.get(request);

            // fetch original id and set return address
            String jmsid = requestMessage.getJMSMessageID();
            Integer aggID = requestMessage.getIntProperty("aggregationID");
            Destination replyAddress = requestMessage.getJMSReplyTo();
            if (requestMessage.getJMSMessageID() == null) {
                throw new NullPointerException("jmsid was not found in map in method sendDealerReply");
            }
            replyMessage.setJMSCorrelationID(jmsid);
            replyMessage.setIntProperty("aggregationID", aggID);
            sender.Send(replyAddress, replyMessage);
            tempStorage.remove(request);
        } catch (JMSException ex) {
            System.out.println("Failed to set correlation ID in sendbankreply");
        }
    }

    /**
     * This method is called when a message is received. The corresponding
     * request is fetched by the app gateway.
     *
     * @param request contains the original request
     */
    abstract public void onDealerRequestArrived(DealerQueryRequest request);

}
