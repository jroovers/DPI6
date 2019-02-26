package bank;

import java.util.HashMap;
import java.util.Map;
import javax.jms.Destination;
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

    // Helper map to keep track of messages we have received.
    private Map<BankInterestRequest, Message> tempStorage;

    public LoanBrokerAppGateway(String queueToListenOn) {
        serializer = new BankSerializer();
        tempStorage = new HashMap<>();
        try {
            sender = new MessageSenderGateway();
            receiver = new MessageReceiverGateway(queueToListenOn);
            receiver.setListener((Message message) -> {
                System.out.println("Bank received message from broker");
                try {
                    String body = ((TextMessage) message).getText();
                    BankInterestRequest request = serializer.requestFromString(body);
                    tempStorage.put(request, message);
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
            Message replyMessage = sender.createTextMessage(body);
            Message requestMessage = tempStorage.get(request);

            // fetch original id and set return address
            String jmsid = requestMessage.getJMSMessageID();
            Integer aggID = requestMessage.getIntProperty("aggregationID");
            Destination replyAddress = requestMessage.getJMSReplyTo();
            if (requestMessage.getJMSMessageID() == null) {
                throw new NullPointerException("jmsid was not found in map in method sendBankReply");
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
    abstract public void onBankRequestArrived(BankInterestRequest request);

}
