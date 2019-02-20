package messaging.gateway;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Abstract class for JMS related gateways. Contains all shared variables.
 *
 * @author Jeroen Roovers
 */
public abstract class MessageAbstractGateway {

    private String brokerUrl;
    private String queueName;

    private Connection connection;
    private Session session;
    private Destination destination;

    public MessageAbstractGateway(String brokerUrl, String queueName) throws JMSException {
        this.brokerUrl = brokerUrl;
        this.queueName = queueName;
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        if (this.queueName.equals("temporary")) {
            this.queueName = null;
            this.destination = null;
        } else {

            destination = session.createQueue(this.queueName);
        }
    }

    public MessageAbstractGateway(String queueName) throws JMSException {
        this(ActiveMQConnection.DEFAULT_BROKER_URL, queueName);
    }

    protected void createTempQueueForReceiver() throws JMSException {
        TemporaryQueue tempq = session.createTemporaryQueue();
        this.queueName = tempq.getQueueName();
        this.destination = tempq;
    }

    protected void closeConnections(MessageConsumer consumer) throws JMSException {
        consumer.close();
        session.close();
        connection.close();
    }

    protected void closeConnections(MessageProducer producer) throws JMSException {
        producer.close();
        session.close();
        connection.close();
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public final void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public final String getQueueName() {
        return queueName;
    }

    public final void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public final Session getSession() {
        return session;
    }

    public final void setSession(Session session) {
        this.session = session;
    }

    public final Destination getDestination() {
        return destination;
    }

    public final void setDestination(Destination destination) {
        this.destination = destination;
    }
}
