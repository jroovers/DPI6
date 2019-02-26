package loanclient;

import java.util.HashMap;
import messaging.gateway.MessageReceiverGateway;
import messaging.gateway.MessageSenderGateway;
import messaging.utility.LoanSerializer;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import model.loan.LoanReply;
import model.loan.LoanRequest;

/**
 *
 * @author Jeroen Roovers
 */
abstract class LoanBrokerAppGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private LoanSerializer serializer;

    private static final String LOANREQUEST_QUEUE_DEFAULT = "LoanRequestQueue";
    private static final String LOANREPLY_QUEUE_DEFAULT = "LoanReplyQueue";

    // Helper map to keep track of messages we have sent.
    private Map<String, LoanRequest> tempStorage;

    public LoanBrokerAppGateway() {
        serializer = new LoanSerializer();
        tempStorage = new HashMap<>();
        try {
            sender = new MessageSenderGateway(LOANREQUEST_QUEUE_DEFAULT);
            receiver = new MessageReceiverGateway();
            // Set listener
            receiver.setListener((Message message) -> {
                System.out.println("Client received message from broker");
                try {
                    // Receive reply
                    String body = ((TextMessage) message).getText();
                    LoanReply reply = serializer.replyFromString(body);
                    // Do the JMS correlation thing
                    String corrId = message.getJMSCorrelationID();
                    LoanRequest request = tempStorage.get(corrId);
                    tempStorage.remove(corrId);
                    onLoanReplyArrived(request, reply);
                } catch (JMSException ex) {
                    System.out.println("Error while receiving loanReply");
                }
            });
        } catch (JMSException ex) {
            System.out.println("Error while setting up message-gateways. Is ActiveMQ running?");
        }
    }

    public void applyForLoan(LoanRequest request) {
        try {
            String body = serializer.requestToString(request);
            Message msg = sender.createTextMessage(body);
            msg.setJMSReplyTo(receiver.getDestination());

            // send and keep track of original message
            sender.Send(msg);
            tempStorage.put(msg.getJMSMessageID(), request);
        } catch (JMSException ex) {
            System.out.println("Couldn't read messageID from message to send");
        }
    }

    /**
     * This method is called when a message is received. The corresponding
     * request is fetched by the app gateway.
     *
     * @param request contains the original request
     * @param reply contains the reply
     */
    abstract public void onLoanReplyArrived(LoanRequest request, LoanReply reply);

}
