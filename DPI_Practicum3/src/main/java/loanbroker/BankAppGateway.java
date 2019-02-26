package loanbroker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
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
    private Map<String, MessageSenderGateway> senders;
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private BankSerializer serializer;
    private Integer aggcounter;

    private static final String BANKREQUEST_QUEUE_DEFAULT = "abnRequestQueue";

    // Helper map to keep track of messages we have sent.
    private Map<Integer, BankInterestRequest> requestStorage;
    private Map<Integer, LinkedList<BankInterestReply>> replyStorage;
    private Map<Integer, Integer> aggregationIdCount;

    public BankAppGateway() {
        recipientlist = new RecipientList();
        senders = new HashMap<>();
        serializer = new BankSerializer();
        requestStorage = new HashMap<>();
        replyStorage = new HashMap<>();
        aggregationIdCount = new HashMap<>();
        aggcounter = 1;
        try {
            for (Bank b : recipientlist.getBankList()) {
                senders.put(b.getBankName(), new MessageSenderGateway(b.getQueuename()));
            }
            sender = new MessageSenderGateway(BANKREQUEST_QUEUE_DEFAULT);
            receiver = new MessageReceiverGateway();
            // Set listener
            receiver.setListener((Message message) -> {
                System.out.println("Broker received message from bank");
                try {
                    // Receive reply
                    String body = ((TextMessage) message).getText();
                    BankInterestReply interestReply = serializer.replyFromString(body);

                    // Do the JMS correlation thing?
                    // TO DO: 'save' incoming messages until all are received.
                    Integer aggId = message.getIntProperty("aggregationID");
                    if (!replyStorage.containsKey(aggId)) {
                        replyStorage.put(aggId, new LinkedList<>());
                    }
                    replyStorage.get(aggId).add(interestReply);

                    // Check if we received all expected replies and if so reply to client.
                    Integer requiredCount = aggregationIdCount.get(aggId);
                    if (requiredCount == replyStorage.get(aggId).size()) {
                        System.out.println("Broker received all replies for aggregation id " + aggId.toString());
                        BankInterestReply bestReply = null;
                        for (BankInterestReply reply : replyStorage.get(aggId)) {
                            if (bestReply == null) {
                                bestReply = reply;
                            } else {
                                if (reply.getInterest() < bestReply.getInterest()) {
                                    bestReply = reply;
                                }
                            }
                        }
                        BankInterestRequest originalrequest = requestStorage.get(aggId);
                        onBankReplyArrived(originalrequest, bestReply);
                        // Cleanup
                        requestStorage.remove(aggId);
                        replyStorage.remove(aggId);
                        aggregationIdCount.remove(aggId);
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
            // recipient implementation
            for (Bank bank : recipientlist.getEligableBanks(request)) {
                MessageSenderGateway banksender = senders.get(bank.getBankName());
                Message msg = banksender.createTextMessage(body);
                msg.setJMSReplyTo(receiver.getDestination());
                msg.setIntProperty("aggregationID", aggcounter);
                // start counting if not doing so yet. otherwise raise aggregation counter
                if (!aggregationIdCount.containsKey(aggcounter)) {
                    aggregationIdCount.put(aggcounter, 1);
                } else {
                    int current = aggregationIdCount.get(aggcounter);
                    aggregationIdCount.put(aggcounter, current + 1);
                }
                banksender.Send(msg);
            }
            if (aggregationIdCount.get(aggcounter) == null) {
                System.out.println("Broker: No eligable banks for this request.");
                onBankReplyArrived(request, null);
            } else {
                System.out.println("Broker Sent to " + aggregationIdCount.get(aggcounter).toString() + " banks with aggregation id " + aggcounter.toString());
                requestStorage.put(aggcounter, request);
                aggcounter++;
            }

//            Message msg = sender.createTextMessage(body);
//            msg.setJMSReplyTo(receiver.getDestination());
//
//            // send and keep track of original message.
//            sender.Send(msg);
//            tempStorage.put(msg.getJMSMessageID(), request);
        } catch (JMSException | EvaluationException ex) {
            System.out.println("Failed to get JMSMessageID in sendBankRequest");
            System.out.println("OR failed to evaluate bank filter");
        }
    }

    /**
     * This method is called when a message is received. The corresponding
     * request is fetched by the app gateway.
     *
     * @param request contains the original request
     * @param reply contains the reply
     */
    abstract public void onBankReplyArrived(BankInterestRequest request, BankInterestReply reply
    );

}
