package loanbroker;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import model.bank.*;
import model.loan.LoanReply;
import model.loan.LoanRequest;
import org.apache.activemq.ActiveMQConnectionFactory;

public class LoanBrokerFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private DefaultListModel<JListLine> listModel = new DefaultListModel<JListLine>();
    private JList<JListLine> list;

    private Connection connection; // to connect to the JMS
    private Session session; // session for creating consumers
    private Destination clientReceiveDestination; // reference to a queue/topic destination
    private Destination clientSendDestination; // reference to a queue/topic destination
    private Destination bankReceiverDestination; // reference to a queue/topic destination
    private Destination bankSendDestination; // reference to a queue/topic destination
    private MessageConsumer clientConsumer; // for receiving messages
    private MessageProducer clientProducer; // for sending messages
    private MessageConsumer bankConsumer; // for receiving messages
    private MessageProducer bankProducer; // for sending messages

    private LoanRequest temporaryLRequest;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoanBrokerFrame frame = new LoanBrokerFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public LoanBrokerFrame() {

        // load shared variables
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        connectionFactory.setTrustAllPackages(true);
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // start sender (client)
            clientReceiveDestination = session.createQueue("ClientReceiverDestination");
            clientProducer = session.createProducer(clientReceiveDestination);

            // start sender (bank)
            bankReceiverDestination = session.createQueue("BankReceiverDestination");
            bankProducer = session.createProducer(bankReceiverDestination);

            // start receiver (client)
            clientSendDestination = session.createQueue("ClientSendDestination");
            clientConsumer = session.createConsumer(clientSendDestination);
            clientConsumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    System.out.println("Broker received message from client");
                    if (message instanceof ObjectMessage) {
                        try {
                            System.out.println("Msgid: " + message.getJMSMessageID());
                            System.out.println("Corr: " + message.getJMSCorrelationID());
                            ObjectMessage objectMessage = (ObjectMessage) message;
                            LoanRequest lr = (LoanRequest) objectMessage.getObject();
                            add(lr);
                            temporaryLRequest = lr;
                            BankInterestRequest bir = new BankInterestRequest(lr.getAmount(), lr.getTime());
                            System.out.println("Broker sending BankInterestRequest to bank: " + bir);
                            add(lr, bir);
                            Message msg = session.createObjectMessage(bir);
                            bankProducer.send(msg);
                            System.out.println("Msgid: " + msg.getJMSMessageID());
                            System.out.println("Corr: " + msg.getJMSCorrelationID());
                        } catch (JMSException ex) {
                            System.out.println("JMS exception in onMessage method for client consumer.");
                        }
                    }
                }
            });

            // start receiver (bank)
            bankSendDestination = session.createQueue("BankSendDestination");
            bankConsumer = session.createConsumer(bankSendDestination);
            bankConsumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    System.out.println("Broker received message from bank");
                    if (message instanceof ObjectMessage) {
                        try {
                            ObjectMessage objectMessage = (ObjectMessage) message;
                            BankInterestReply bir = (BankInterestReply) objectMessage.getObject();
                            add(temporaryLRequest, bir);
                            temporaryLRequest = null;
                            System.out.println("Broker sending LoanReply to client: " + bir.toString());
                            clientProducer.send(session.createObjectMessage(new LoanReply(bir.getInterest(), bir.getQuoteId())));
                        } catch (JMSException ex) {
                            System.out.println("JMS exception in onMessage method for bank consumer.");
                        }
                    }
                }
            });

            // Connect
            connection.start();
        } catch (JMSException ex) {
            // No point in continueing, kill the app.
            System.out.println("JMS exception in LoanBrokerFrame in constructor method");
            System.out.println("Is ActiveMQ server running?");

            System.out.println("Shutting down");
            System.exit(1);
        }

        setTitle("Loan Broker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
        gbl_contentPane.rowHeights = new int[]{233, 23, 0};
        gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridwidth = 7;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        contentPane.add(scrollPane, gbc_scrollPane);

        list = new JList<JListLine>(listModel);
        scrollPane.setViewportView(list);
    }

    private JListLine getRequestReply(LoanRequest request) {

        for (int i = 0; i < listModel.getSize(); i++) {
            JListLine rr = listModel.get(i);
            if (rr.getLoanRequest() == request) {
                return rr;
            }
        }
        return null;
    }

    public void add(LoanRequest loanRequest) {
        listModel.addElement(new JListLine(loanRequest));
    }

    public void add(LoanRequest loanRequest, BankInterestRequest bankRequest) {
        JListLine rr = getRequestReply(loanRequest);
        if (rr != null && bankRequest != null) {
            rr.setBankRequest(bankRequest);
            list.repaint();
        }
    }

    public void add(LoanRequest loanRequest, BankInterestReply bankReply) {
        JListLine rr = getRequestReply(loanRequest);
        if (rr != null && bankReply != null) {
            rr.setBankReply(bankReply);;
            list.repaint();
        }
    }
}
