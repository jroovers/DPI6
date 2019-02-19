package gateway.jms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

/**
 *
 * @author Jeroen Roovers
 */
public class MessageReceiverGateway extends MessageAbstractGateway {

    private final MessageConsumer consumer;

    public MessageReceiverGateway(String queueName) throws JMSException {
        super(queueName);
        consumer = getSession().createConsumer(getDestination());
    }

    public void setListener(MessageListener ml) {
        try {
            consumer.setMessageListener(ml);
        } catch (JMSException ex) {
            Logger.getLogger(MessageReceiverGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
