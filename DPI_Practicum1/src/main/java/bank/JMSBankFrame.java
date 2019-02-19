package bank;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import model.bank.*;
import messaging.requestreply.RequestReply;
import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSBankFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField tfReply;
    private DefaultListModel<RequestReply<BankInterestRequest, BankInterestReply>> listModel = new DefaultListModel<RequestReply<BankInterestRequest, BankInterestReply>>();

    private Connection connection; // to connect to the JMS
    private Session session; // session for creating consumers
    private Destination receiverDestination; // reference to a queue/topic destination
    private Destination sendDestination; // reference to a queue/topic destination
    private MessageConsumer consumer; // for receiving messages
    private MessageProducer producer; // for sending messages

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JMSBankFrame frame = new JMSBankFrame();
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
    public JMSBankFrame() {

        // load shared variables
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        connectionFactory.setTrustAllPackages(true);
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // start sender
            sendDestination = session.createQueue("BankSendDestination");
            producer = session.createProducer(sendDestination);

            // start receiver
            receiverDestination = session.createQueue("BankReceiverDestination");
            consumer = session.createConsumer(receiverDestination);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    System.out.println("Bank received message from broker");
                    if (message instanceof ObjectMessage) {
                        try {
                            System.out.println("Msgid: " + message.getJMSMessageID());
                            System.out.println("Corr: " + message.getJMSCorrelationID());
                            ObjectMessage o = (ObjectMessage) message;
                            BankInterestRequest bir = (BankInterestRequest) o.getObject();
                            listModel.addElement(new RequestReply<>(bir, null));
                        } catch (JMSException ex) {
                            System.out.println("JMS exception in onMessage method for broker consumer.");
                        }
                    }
                }
            });

            // Connect
            connection.start();
        } catch (JMSException ex) {
            // No point in continueing, kill the app.
            System.out.println("JMS exception in JMSBankFrame in constructor method");
            System.out.println("Is ActiveMQ server running?");

            System.out.println("Shutting down");
            System.exit(1);
        }

        setTitle("JMS Bank - ABN AMRO");
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
        gbc_scrollPane.gridwidth = 5;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        contentPane.add(scrollPane, gbc_scrollPane);

        JList<RequestReply<BankInterestRequest, BankInterestReply>> list = new JList<RequestReply<BankInterestRequest, BankInterestReply>>(listModel);
        scrollPane.setViewportView(list);

        JLabel lblNewLabel = new JLabel("type reply");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 1;
        contentPane.add(lblNewLabel, gbc_lblNewLabel);

        tfReply = new JTextField();
        GridBagConstraints gbc_tfReply = new GridBagConstraints();
        gbc_tfReply.gridwidth = 2;
        gbc_tfReply.insets = new Insets(0, 0, 0, 5);
        gbc_tfReply.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfReply.gridx = 1;
        gbc_tfReply.gridy = 1;
        contentPane.add(tfReply, gbc_tfReply);
        tfReply.setColumns(10);

        JButton btnSendReply = new JButton("send reply");
        btnSendReply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RequestReply<BankInterestRequest, BankInterestReply> rr = list.getSelectedValue();
                double interest = Double.parseDouble((tfReply.getText()));
                BankInterestReply reply = new BankInterestReply(interest, "ABN AMRO");
                if (rr != null && reply != null) {
                    rr.setReply(reply);
                    list.repaint();
                    try {
                        // todo: sent JMS message with the reply to Loan Broker
                        System.out.println("Bank sending BankInterestReply to Broker: " + String.valueOf(interest));
                        producer.send(session.createObjectMessage(reply));
                    } catch (JMSException ex) {
                        System.out.println("JMSexception while sending reply.");
                    }
                }
            }
        });
        GridBagConstraints gbc_btnSendReply = new GridBagConstraints();
        gbc_btnSendReply.anchor = GridBagConstraints.NORTHWEST;
        gbc_btnSendReply.gridx = 4;
        gbc_btnSendReply.gridy = 1;
        contentPane.add(btnSendReply, gbc_btnSendReply);
    }

}
