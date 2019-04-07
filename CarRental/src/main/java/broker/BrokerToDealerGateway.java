package broker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import messaging.dynrouter.Aggregator;
import messaging.dynrouter.RecipientList;
import messaging.dynrouter.ControlListener;
import messaging.dynrouter.DealerExtended;
import messaging.gateway.MessageReceiverGateway;
import messaging.gateway.MessageSenderGateway;
import messaging.serializer.DealerSerializer;
import model.Car;
import model.Dealer;
import model.answer.DealerQueryReply;
import model.answer.DealerQueryRequest;
import model.query.ClientQueryReply;
import net.sourceforge.jeval.EvaluationException;

/**
 *
 * @author Jeroen Roovers <jroovers>
 */
abstract class BrokerToDealerGateway {

    private RecipientList recipientlist;
    private Aggregator<DealerQueryReply> aggregator;
    private Map<String, MessageSenderGateway> senders;
    private MessageReceiverGateway receiver;
    private MessageReceiverGateway controlreceiver;
    private DealerSerializer serializer;

    private ControlListener drouter;

    // Helper map to keep track of messages we have sent.
    private Map<Integer, DealerQueryRequest> requestStorage;

    public BrokerToDealerGateway() {
        recipientlist = new RecipientList();
        aggregator = new Aggregator<>();
        senders = new HashMap<>();
        serializer = new DealerSerializer();
        requestStorage = new HashMap<>();
        try {
            // TO DO register all eligable dealers (control channel/dynamic router)
            drouter = new ControlListener() {
                @Override
                public void onNewDealerFound(Dealer dealer, String queue, String filter) {
                    try {
                        System.out.println("Broker received dealer control message.");
                        recipientlist.AddNewDealer(dealer, queue, filter);
                        // Senders are unique on a QUEUE basis
                        senders.put(queue, new MessageSenderGateway(queue));
                        newDealerRegistered(dealer, queue, filter);
                    } catch (JMSException ex) {
                        System.out.println("Could not start new receiver for dealer: " + dealer.getName());
                    }
                }
            };
            // receive on temporary channel
            receiver = new MessageReceiverGateway();
            // Set listener
            receiver.setListener((Message message) -> {
                System.out.println("Broker received message from dealer");
                try {
                    // Receive reply
                    String body = ((TextMessage) message).getText();
                    DealerQueryReply dealerReply = serializer.replyFromString(body);
                    // AGGREGATOR LOGIC HERE
                    Integer aggId = message.getIntProperty("aggregationID");
                    boolean receivedAll = aggregator.storeObject(aggId, dealerReply);
                    tryProcessReplies(receivedAll, aggId);
                } catch (JMSException ex) {
                    System.out.println("Error while receiving DealerQueryReply");
                }
            });
        } catch (JMSException ex) {
            System.out.println("Error while setting up message-gateways. Is ActiveMQ running?");
        }
    }

    private void tryProcessReplies(boolean sendreply, Integer aggId) {
        if (sendreply || aggId == null) {
            // Timer event or all replies received, force send reply to client
            System.out.println("Broker received all replies for aggregation id " + aggId.toString());
            // TO DO: Get all replies and merge into answer
            List<Car> compositeList = new LinkedList<Car>();
            for (DealerQueryReply reply : aggregator.getObjectsAndClearByAggregationId(aggId)) {
                compositeList.addAll(reply.getCars());
            }
            DealerQueryRequest originalrequest = requestStorage.get(aggId);
            DealerQueryReply compositeReply = new DealerQueryReply();
            compositeReply.setCars(compositeList);
            // TO DO send merge answer
            if (compositeReply.getCars().size() == 0) {
                onDealerReplyArrived(originalrequest, null);
            } else {
                onDealerReplyArrived(originalrequest, compositeReply);
            }
            // Cleanup
            requestStorage.remove(aggId);
        }
    }

    public void sendDealerRequest(DealerQueryRequest request) {
        try {
            String body = serializer.requestToString(request);
            // Get a new aggregation id
            int aggid = aggregator.getNewAggregationCounter();
            // For each eligable bank, register to aggregator and send message.
            for (DealerExtended dealer : recipientlist.getEligableDealers(request)) {
                MessageSenderGateway dealerSender = senders.get(dealer.getQueuename());
                Message msg = dealerSender.createTextMessage(body);
                msg.setJMSReplyTo(receiver.getDestination());
                msg.setIntProperty("aggregationID", aggid);
                aggregator.countObject(aggid);
                dealerSender.Send(msg);
            }
            // In the event no banks were eligable deal with this
            if (aggregator.getObjectCount(aggid) == null) {
                System.out.println("Broker: No eligable dealers for this request.");
                onDealerReplyArrived(request, null);
            } else {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // After 5 seconds force to send a reply to client.
                        // Even if not all dealers sent a reply.
                        // Don't do anything if reply was already sent.
                        System.out.println("Dealer: 5 seconds since request, forcing process...");
                        tryProcessReplies(true, aggid);
                    }
                }, 5000);
                System.out.println("Broker Sent to " + aggregator.getObjectCount(aggid).toString() + " dealers with aggregation id " + aggid);
                requestStorage.put(aggid, request);
            }
        } catch (JMSException | EvaluationException ex) {
            System.out.println("Failed to get JMSMessageID in sendDealerRequest");
            System.out.println("OR failed to evaluate dealer filter");
        }
    }

    /**
     * This method is called when a message is received. The GUI should
     * implement this abstract method and do put the GUI updates in it.
     *
     * @param request contains the original request
     * @param reply contains the reply
     */
    abstract public void onDealerReplyArrived(DealerQueryRequest request, DealerQueryReply reply);

    abstract public void newDealerRegistered(Dealer dealer, String queue, String filter);

}
