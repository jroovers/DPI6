package loanbroker;

import java.util.HashMap;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import messaging.gateway.MessageReceiverGateway;
import messaging.gateway.MessageSenderGateway;
import messaging.utility.BankSerializer;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;

/**
 *
 * @author Jeroen Roovers <jroovers>
 */
abstract class BankAppGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private BankSerializer serializer;

    private static final String BANKREQUEST_QUEUE_DEFAULT = "BankRequestQueue";
    private static final String BANKREPLY_QUEUE_DEFAULT = "BankReplyQueue";

    // Helper map to keep track of messages we have received.
    private Map<String, BankInterestRequest> tempStorage;

    public BankAppGateway() {
        serializer = new BankSerializer();
        tempStorage = new HashMap<>();
        try {
            sender = new MessageSenderGateway(BANKREQUEST_QUEUE_DEFAULT);
            receiver = new MessageReceiverGateway(BANKREPLY_QUEUE_DEFAULT);
            receiver.setListener((Message message) -> {
                System.out.println("Broker received message from bank");
                try {
                    String body = ((TextMessage) message).getText();
                    BankInterestReply reply = serializer.replyFromString(body);

                    BankInterestRequest request = tempStorage.get(message.getJMSCorrelationID());
                    tempStorage.remove(message.getJMSCorrelationID());
                    onBankReplyArrived(request, reply);
                } catch (JMSException ex) {
                    System.out.println("Error while receiving BankInterestReply");
                }
            });
        } catch (JMSException ex) {
            System.out.println("Error while setting up message-gateways. Is ActiveMQ running?");
        }
    }

    public void sendBankRequest(BankInterestRequest request) {
        try {
            String body = serializer.requestToString(request);
            Message msg = sender.createTextMessage(body);

            // send and keep track of original message id.
            sender.Send(msg);
            tempStorage.put(msg.getJMSMessageID(), request);
        } catch (JMSException ex) {
            System.out.println("Failed to get JMSMessageID in sendBankRequest");
        }
    }

    /**
     * This method is called when a message is received. The corresponding
     * request is fetched by the app gateway.
     *
     * @param request contains the original request
     * @param reply contains the reply
     */
    abstract public void onBankReplyArrived(BankInterestRequest request, BankInterestReply reply);

}
