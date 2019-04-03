package broker;

import java.util.HashMap;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import messaging.gateway.MessageReceiverGateway;
import messaging.gateway.MessageSenderGateway;
import messaging.serializer.DealerSerializer;
import model.answer.DealerQueryReply;
import model.answer.DealerQueryRequest;
import net.sourceforge.jeval.EvaluationException;

/**
 *
 * @author Jeroen Roovers <jroovers>
 */
abstract class BrokerToDealerGateway {

    // private RecipientList recipientlist;
    // TO DO replace aggregator with dynamic router
    // private Aggregator<BankInterestReply> aggregator;
    private Map<String, MessageSenderGateway> senders;
    private MessageReceiverGateway receiver;
    private MessageReceiverGateway controlreceiver;
    private DealerSerializer serializer;

    // Helper map to keep track of messages we have sent.
    private Map<Integer, DealerQueryRequest> requestStorage;

    public BrokerToDealerGateway() {
//        recipientlist = new RecipientList();
//        aggregator = new Aggregator<>();
        senders = new HashMap<>();
        serializer = new DealerSerializer();
        requestStorage = new HashMap<>();
        try {
            // TO DO register all eligable dealers (control channel/dynamic router)
//            for (Bank b : recipientlist.getBankList()) {
//                senders.put(b.getBankName(), new MessageSenderGateway(b.getQueuename()));
//            }
            // receive on temporay channel
            receiver = new MessageReceiverGateway();
            // Set listener
            receiver.setListener((Message message) -> {
                System.out.println("Broker received message from dealer");
                try {
                    // Receive reply
                    String body = ((TextMessage) message).getText();
                    DealerQueryReply dealerReply = serializer.replyFromString(body);

//                    Integer aggId = message.getIntProperty("aggregationID");
//                    boolean receivedAll = aggregator.storeObject(aggId, dealerReply);
//                    // Check if we received all expected replies and if so reply to client.
//                    if (receivedAll) {
//                        System.out.println("Broker received all replies for aggregation id " + aggId.toString());
//
//                        // TO DO: Get all replies and merge into answer
//                        for (DealerQueryReply reply : aggregator.getObjectsAndClearByAggregationId(aggId)) {
//                            
//
//                        }
//                        DealerQueryRequest originalrequest = requestStorage.get(aggId);
//                        // TO DO send merge answer
//                        //onBankReplyArrived(originalrequest, compositeReply??);
//                        // Cleanup
//                        requestStorage.remove(aggId);
//                    }
                } catch (JMSException ex) {
                    System.out.println("Error while receiving DealerQueryReply");
                }
            });
        } catch (JMSException ex) {
            System.out.println("Error while setting up message-gateways. Is ActiveMQ running?");
        }
    }

    public void sendDealerRequest(DealerQueryRequest request) {
//        try {
//            String body = serializer.requestToString(request);
//
//            // Get a new aggregation id
//            int aggid = aggregator.getNewAggregationCounter();
//
//            // For each eligable bank, register to aggregator and send message.
//            for (Bank bank : recipientlist.getEligableBanks(request)) {
//                MessageSenderGateway banksender = senders.get(bank.getBankName());
//                Message msg = banksender.createTextMessage(body);
//                msg.setJMSReplyTo(receiver.getDestination());
//                msg.setIntProperty("aggregationID", aggid);
//                aggregator.countObject(aggid);
//                banksender.Send(msg);
//            }
//
//            // In the event no banks were eligable deal with this
//            if (aggregator.getObjectCount(aggid) == null) {
//                System.out.println("Broker: No eligable banks for this request.");
//                onBankReplyArrived(request, null);
//            } else {
//                System.out.println("Broker Sent to " + aggregator.getObjectCount(aggid).toString() + " banks with aggregation id " + aggid);
//                requestStorage.put(aggid, request);
//            }
//        } catch (JMSException | EvaluationException ex) {
//            System.out.println("Failed to get JMSMessageID in sendBankRequest");
//            System.out.println("OR failed to evaluate bank filter");
//        }
    }

    /**
     * This method is called when a message is received. The GUI should
     * implement this abstract method and do put the GUI updates in it.
     *
     * @param request contains the original request
     * @param reply contains the reply
     */
    abstract public void onDealerReplyArrived(DealerQueryRequest request, DealerQueryReply reply);

}
