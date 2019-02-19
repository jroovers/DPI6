package messaging.gateway;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

/**
 *
 * @author Jeroen Roovers
 */
public class MessageSenderGateway extends MessageAbstractGateway {

    private final MessageProducer producer;

    public MessageSenderGateway(String queueName) throws JMSException {
        super(queueName);
        producer = getSession().createProducer(getDestination());
    }

    public Message createTextMessage(String body) {
        try {
            return this.getSession().createTextMessage(body);
        } catch (JMSException ex) {
            Logger.getLogger(MessageSenderGateway.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void Send(Message msg) {
        try {
            producer.send(msg);
        } catch (JMSException ex) {
            Logger.getLogger(MessageSenderGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
