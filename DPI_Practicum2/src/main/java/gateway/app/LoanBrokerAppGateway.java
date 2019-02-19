package gateway.app;

import gateway.jms.MessageReceiverGateway;
import gateway.jms.MessageSenderGateway;
import gateway.utility.LoanSerializer;
import java.util.Map;
import java.util.TreeMap;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import model.loan.LoanReply;
import model.loan.LoanRequest;

/**
 *
 * @author Jeroen Roovers
 */
abstract public class LoanBrokerAppGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private LoanSerializer serializer;

    private static final String LOANREPLY_QUEUE_DEFAULT = "LoanReplyQueue";
    private static final String LOANREQUEST_QUEUE_DEFAULT = "LoanRequestQueue";
    private static final String BANKINTERESTREQUEST_QUEUE_DEFAULT = "LoanReplyQueue";
    private static final String BANKINTERESTREPLY_QUEUE_DEFAULT = "LoanRequestQueue";

    // Helper map to keep track of messages we have sent.
    private Map<String, LoanRequest> tempStorage = new TreeMap<>();

    public LoanBrokerAppGateway() {
        try {
            sender = new MessageSenderGateway(LOANREQUEST_QUEUE_DEFAULT);
            receiver = new MessageReceiverGateway(LOANREPLY_QUEUE_DEFAULT);
            receiver.setListener((Message message) -> {
                try {
                    String corrID = message.getJMSCorrelationID();
                    String body = ((TextMessage) message).getText();
                    LoanReply reply = serializer.replyFromString(body);
                    LoanRequest originalRequest = tempStorage.get(corrID);
                    onLoanReplyArrived(originalRequest, reply);
                } catch (JMSException ex) {
                    System.out.println("Error while receiving loanreply");
                }
            });
        } catch (JMSException ex) {
            System.out.println("Error while setting up message-gateways. Is ActiveMQ running?");
        }
    }

    public void applyForLoan(LoanRequest request) {
        String body = serializer.requestToString(request);
        Message msg = sender.createTextMessage(body);
        try {
            tempStorage.put(msg.getJMSMessageID(), request);
        } catch (JMSException ex) {
            System.out.println("Couldn't read messageID from message to send");
        }
        sender.Send(msg);
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
