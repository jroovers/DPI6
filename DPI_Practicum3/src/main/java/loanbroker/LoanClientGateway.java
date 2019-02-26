package loanbroker;

import java.util.HashMap;
import java.util.Map;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import messaging.gateway.MessageReceiverGateway;
import messaging.gateway.MessageSenderGateway;
import messaging.utility.LoanSerializer;
import model.loan.LoanReply;
import model.loan.LoanRequest;

/**
 *
 * @author Jeroen Roovers
 */
abstract class LoanClientGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private LoanSerializer serializer;

    private static final String LOANREQUEST_QUEUE_DEFAULT = "LoanRequestQueue";
    private static final String LOANREPLY_QUEUE_DEFAULT = "LoanReplyQueue";

    // Helper map to keep track of messages we have received.
    private Map<LoanRequest, Message> tempStorage;

    public LoanClientGateway() {
        serializer = new LoanSerializer();
        tempStorage = new HashMap<>();
        try {
            sender = new MessageSenderGateway();
            receiver = new MessageReceiverGateway(LOANREQUEST_QUEUE_DEFAULT);
            receiver.setListener((Message message) -> {
                System.out.println("Broker received message from client");
                try {
                    String corrID = message.getJMSCorrelationID();
                    String body = ((TextMessage) message).getText();
                    LoanRequest request = serializer.requestFromString(body);
                    tempStorage.put(request, message);
                    onLoanRequestArrived(request);
                } catch (JMSException ex) {
                    System.out.println("Error while receiving loanreply");
                }
            });
        } catch (JMSException ex) {
            System.out.println("Error while setting up message-gateways. Is ActiveMQ running?");
        }
    }

    public void sendLoanReply(LoanRequest request, LoanReply reply) {
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
     */
    abstract void onLoanRequestArrived(LoanRequest request);
}
