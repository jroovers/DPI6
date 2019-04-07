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
public abstract class ControlListener {

    private MessageReceiverGateway controlReceiver;
    private ControlSerializer serializer;

    public ControlListener() {
        try {
            this.controlReceiver = new MessageReceiverGateway("brokerControlQueue");
            this.controlReceiver.setListener((message) -> {
                try {
                    String body = ((TextMessage) message).getText();
                    ControlMessage ctrl = serializer.stringToControl(body);
                    switch (ctrl.getType()) {
                        case CREATE:
                            onNewDealerFound(ctrl.getDealer(), ctrl.getQueueName(), ctrl.getFilter());
                            break;
                        case UPDATE:
                            // NOT IMPLEMENTED
                            break;
                        case DELETE:
                            // NOT IMPLEMENTED
                            break;
                        default:
                            break;
                    }
                    // If does not exist, register this dealer.
                } catch (JMSException ex) {
                    Logger.getLogger(ControlListener.class.getName()).log(Level.SEVERE, null, ex);
                }

            });
        } catch (JMSException ex) {
            Logger.getLogger(ControlListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    abstract public void onNewDealerFound(Dealer dealer, String queue, String filter);
}
