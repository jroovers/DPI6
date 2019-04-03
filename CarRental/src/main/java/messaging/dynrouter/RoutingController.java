package messaging.dynrouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import messaging.gateway.MessageReceiverGateway;
import messaging.serializer.ControlSerializer;
import model.Dealer;

/**
 *
 * @author Jeroen Roovers
 */
abstract class RoutingController {

    private MessageReceiverGateway controlReceiver;
    private ControlSerializer serializer;
    HashMap<String, String> queueAndFilters;
    List<String> knownDealerQueues;

    public RoutingController() {
        try {
            this.queueAndFilters = new HashMap<>();
            this.knownDealerQueues = new ArrayList<>();
            this.controlReceiver = new MessageReceiverGateway("brokerControlQueue");
            this.controlReceiver.setListener((message) -> {
                try {
                    String body = ((TextMessage) message).getText();
                    ControlMessage ctrl = serializer.stringToControl(body);
                    // If does exist, register this dealer.
                    if (queueAndFilters.containsKey(ctrl.getQueueName())) {
                        switch (ctrl.getType()) {
                            case CREATE:

                                break;
                            case UPDATE:
                                
                                break;
                            case DELETE:

                                break;
                            default:

                                break;
                        }
                    } // If does not exist, register this dealer.
                    else {
                        

                    }

                } catch (JMSException ex) {
                    Logger.getLogger(RoutingController.class.getName()).log(Level.SEVERE, null, ex);
                }

            });
        } catch (JMSException ex) {
            Logger.getLogger(RoutingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    abstract public String onNewDealerFound();
}
