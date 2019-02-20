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

    // should be temporary queue
    private static final String LOANREPLY_QUEUE_DEFAULT = "LoanReplyQueue";
    // should be general queue
    private static final String LOANREQUEST_QUEUE_DEFAULT = "LoanRequestQueue";

    // Helper map to keep track of messages we have sent.
    private Map<String, LoanRequest> tempStorage;

    public LoanBrokerAppGateway() {
        tempStorage = new HashMap<>();
        serializer = new LoanSerializer();
        try {
            sender = new MessageSenderGateway(LOANREQUEST_QUEUE_DEFAULT);
            receiver = new MessageReceiverGateway();
            receiver.setListener((Message message) -> {
                System.out.println("Client received message from broker");
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
        try {
            String body = serializer.requestToString(request);
            Message msg = sender.createTextMessage(body);
            msg.setJMSReplyTo(receiver.getDestination());
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
