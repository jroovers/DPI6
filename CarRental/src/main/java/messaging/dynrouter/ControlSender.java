package messaging.dynrouter;

import javax.jms.JMSException;
import javax.jms.Message;
import messaging.gateway.MessageSenderGateway;
import messaging.serializer.ControlSerializer;
import model.Dealer;

/**
 *
 * @author Jeroen Roovers
 */
public class ControlSender {

    private MessageSenderGateway sender;
    private ControlSerializer serializer;
    private Dealer dealer;
    private String filter;
    private String queueName;

    public ControlSender() {
        try {
            this.serializer = new ControlSerializer();
            this.sender = new MessageSenderGateway("brokerControlQueue");
        } catch (JMSException ex) {
            System.out.println("could not create control sender");
        }
    }

    public ControlSender(Dealer dealer, String filter, String queueName) {
        this();
        this.dealer = dealer;
        this.filter = filter;
        this.queueName = queueName;
    }

    public void sendControlMessage(Dealer dealer, String queuename, String filter) {
        ControlMessage ctrl = new ControlMessage(dealer, filter, queuename, ControlType.CREATE);
        String body = serializer.controlToString(ctrl);
        Message msg = sender.createTextMessage(body);

        StringBuilder sb = new StringBuilder("Dealer sending control message: ");
        sb.append("Dealer=").append(dealer.getName()).append(", ");
        sb.append("Queue=").append(queuename).append(", ");
        sb.append("Filter=").append(filter);
        System.out.println(sb.toString());
        sender.Send(msg);;
    }

    public void sendControlMessage() {
        if (this.dealer != null && this.queueName != null && this.filter != null) {
            sendControlMessage(this.dealer, this.queueName, this.filter);
        } else {
            System.out.println("Tried to use no-argument sendControlMessage but fields in CotnrolSender were not properly set.");
        }
    }

    public Dealer getDealer() {
        return dealer;
    }

    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
