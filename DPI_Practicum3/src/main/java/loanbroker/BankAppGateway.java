package loanbroker;

import java.util.HashMap;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import loanbroker.scattergather.Aggregator;
import loanbroker.scattergather.Bank;
import loanbroker.scattergather.RecipientList;
import messaging.gateway.MessageReceiverGateway;
import messaging.gateway.MessageSenderGateway;
import messaging.utility.BankSerializer;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;
import net.sourceforge.jeval.EvaluationException;

/**
 *
 * @author Jeroen Roovers <jroovers>
 */
abstract class BankAppGateway {

    private RecipientList recipientlist;
    private Aggregator<BankInterestReply> aggregator;
    private Map<String, MessageSenderGateway> senders;
    private MessageReceiverGateway receiver;
    private BankSerializer serializer;

    // Helper map to keep track of messages we have sent.
    private Map<Integer, BankInterestRequest> requestStorage;

    public BankAppGateway() {
        recipientlist = new RecipientList();
        aggregator = new Aggregator<>();
        senders = new HashMap<>();
        serializer = new BankSerializer();
        requestStorage = new HashMap<>();
        try {
            for (Bank b : recipientlist.getBankList()) {
                senders.put(b.getBankName(), new MessageSenderGateway(b.getQueuename()));
            }
            receiver = new MessageReceiverGateway();
            // Set listener
            receiver.setListener((Message message) -> {
                System.out.println("Broker received message from bank");
                try {
                    // Receive reply
                    String body = ((TextMessage) message).getText();
                    BankInterestReply interestReply = serializer.replyFromString(body);

                    Integer aggId = message.getIntProperty("aggregationID");
                    boolean receivedAll = aggregator.storeObject(aggId, interestReply);
                    // Check if we received all expected replies and if so reply to client.
                    if (receivedAll) {
                        System.out.println("Broker received all replies for aggregation id " + aggId.toString());

                        // Get replies and determine best one
                        BankInterestReply bestReply = null;
                        for (BankInterestReply reply : aggregator.getObjectsAndClearByAggregationId(aggId)) {
                            if (bestReply == null) {
                                bestReply = reply;
                            }
                            if (reply.getInterest() < bestReply.getInterest()) {
                                bestReply = reply;
                            }
                        }
                        BankInterestRequest originalrequest = requestStorage.get(aggId);
                        onBankReplyArrived(originalrequest, bestReply);
                        // Cleanup
                        requestStorage.remove(aggId);
                    }
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

            // Get a new aggregation id
            int aggid = aggregator.getNewAggregationCounter();

            // For each eligable bank, register to aggregator and send message.
            for (Bank bank : recipientlist.getEligableBanks(request)) {
                MessageSenderGateway banksender = senders.get(bank.getBankName());
                Message msg = banksender.createTextMessage(body);
                msg.setJMSReplyTo(receiver.getDestination());
                msg.setIntProperty("aggregationID", aggid);
                aggregator.countObject(aggid);
                banksender.Send(msg);
            }

            // In the event no banks were eligable deal with this
            if (aggregator.getObjectCount(aggid) == null) {
                System.out.println("Broker: No eligable banks for this request.");
                onBankReplyArrived(request, null);
            } else {
                System.out.println("Broker Sent to " + aggregator.getObjectCount(aggid).toString() + " banks with aggregation id " + aggid);
                requestStorage.put(aggid, request);
            }
        } catch (JMSException | EvaluationException ex) {
            System.out.println("Failed to get JMSMessageID in sendBankRequest");
            System.out.println("OR failed to evaluate bank filter");
        }
    }

    /**
     * This method is called when a message is received. The GUI should
     * implement this abstract method and do put the GUI updates in it.
     *
     * @param request contains the original request
     * @param reply contains the reply
     */
    abstract public void onBankReplyArrived(BankInterestRequest request, BankInterestReply reply
    );

}
