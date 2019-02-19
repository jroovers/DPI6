package bank;

import java.util.HashMap;
import java.util.Map;
import messaging.gateway.MessageReceiverGateway;
import messaging.gateway.MessageSenderGateway;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import messaging.utility.BankSerializer;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;

/**
 *
 * @author Jeroen Roovers
 */
abstract class LoanBrokerAppGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private BankSerializer serializer;

    private static final String BANKREQUEST_QUEUE_DEFAULT = "BankRequestQueue";
    private static final String BANKREPLY_QUEUE_DEFAULT = "BankReplyQueue";

    // Helper map to keep track of messages we have received.
    private Map<BankInterestRequest, String> tempStorage;

    public LoanBrokerAppGateway() {
        serializer = new BankSerializer();
        tempStorage = new HashMap<>();
        try {
            sender = new MessageSenderGateway(BANKREPLY_QUEUE_DEFAULT);
            receiver = new MessageReceiverGateway(BANKREQUEST_QUEUE_DEFAULT);
            receiver.setListener((Message message) -> {
                System.out.println("Bank received message from broker");
                try {
                    String corrID = message.getJMSMessageID();
                    String body = ((TextMessage) message).getText();
                    BankInterestRequest request = serializer.requestFromString(body);
                    tempStorage.put(request, message.getJMSMessageID());
                    onBankRequestArrived(request);
                } catch (JMSException ex) {
                    System.out.println("Error while receiving loanreply");
                }
            });
        } catch (JMSException ex) {
            System.out.println("Error while setting up message-gateways. Is ActiveMQ running?");
        }
    }

    public void sendBankReply(BankInterestRequest request, BankInterestReply reply) {
        try {
            String body = serializer.replyToString(reply);
            Message msg = sender.createTextMessage(body);
            // fetch original id
            String jmsid = tempStorage.get(request);
            if (jmsid == null) {
                throw new NullPointerException("jmsid was not found in map in method sendBankReply");
            }
            msg.setJMSCorrelationID(jmsid);
            sender.Send(msg);
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
    abstract public void onBankRequestArrived(BankInterestRequest request);

}
