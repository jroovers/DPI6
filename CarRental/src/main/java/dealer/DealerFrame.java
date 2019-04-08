package dealer;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import messaging.requestreply.RequestReply;
import model.Car;
import model.Dealer;
import model.answer.DealerQueryReply;
import model.answer.DealerQueryRequest;

public class DealerFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField tfReply;
    private DefaultListModel<RequestReply<DealerQueryRequest, DealerQueryReply>> listModel = new DefaultListModel<>();
    private DealerToBrokerGateway gateway;
    private String dealername;
    private List<Car> stock;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Dealer vanRent = new Dealer("VANZZ");
                    List<Car> vanCars = new ArrayList<>();
                    Car car1 = new Car(vanRent, "VDL", "Wit", 5, 130);
                    Car car2 = new Car(vanRent, "VDL", "Wit", 5, 130);
                    Car car3 = new Car(vanRent, "Volkswagen", "Zwart", 7, 90);
                    Car car4 = new Car(vanRent, "Volkswagen", "Zwart", 7, 90);
                    Car car5 = new Car(vanRent, "Mercedes", "Zwart", 7, 250);
                    Car car6 = new Car(vanRent, "Volkswagen", "Rood", 7, 70);
                    Car car7 = new Car(vanRent, "Volkswagen", "Rood", 7, 70);
                    Car car8 = new Car(vanRent, "Volkswagen", "Rood", 7, 70);
                    Car car9 = new Car(vanRent, "Volkswagen", "Rood", 7, 70);
                    Car car10 = new Car(vanRent, "Volkswagen", "Rood", 7, 70);
                    vanCars.add(car1);
                    vanCars.add(car2);
                    vanCars.add(car3);
                    vanCars.add(car4);
                    vanCars.add(car5);
                    vanCars.add(car6);
                    vanCars.add(car7);
                    vanCars.add(car8);
                    vanCars.add(car9);
                    vanCars.add(car10);
                    DealerFrame frameeA = new DealerFrame(vanRent.getName(), "dealerVANZZQueue", "1 < 2", vanCars);
                    frameeA.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public DealerFrame(String dealerName, String receiverQueue, String filter, List<Car> stock) {
        this.dealername = dealerName;
        gateway = new DealerToBrokerGateway(receiverQueue) {
            @Override
            public void onDealerRequestArrived(DealerQueryRequest request) {
                listModel.addElement(new RequestReply<>(request, null));
                DealerQueryReply reply = new DealerQueryReply();
                for (Car c : stock) {
                    if (c.getSeatCount() >= request.getSeats()) {
                        if (request.getBrand().isEmpty()) {
                            reply.getCars().add(c);
                        } else {
                            if (request.getBrand().equalsIgnoreCase(c.getBrand())) {
                                reply.getCars().add(c);
                            }
                        }
                    }
                }
                getRequestReply(request).setReply(reply);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        gateway.sendDealerReply(request, reply);
                        System.out.println("Cardealer - " + dealerName + "; sending " + reply.getCars().size() + " cars.");
                    }
                }, 500);

            }
        };
        gateway.sendControlMessage(new Dealer(this.dealername), receiverQueue, filter);
        setTitle("Cardealer - " + dealerName);
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

        JList<RequestReply<DealerQueryRequest, DealerQueryReply>> list = new JList<>(listModel);
        scrollPane.setViewportView(list);

//        JLabel lblNewLabel = new JLabel("type reply");
//        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
//        gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
//        gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
//        gbc_lblNewLabel.gridx = 0;
//        gbc_lblNewLabel.gridy = 1;
//        contentPane.add(lblNewLabel, gbc_lblNewLabel);
//        tfReply = new JTextField();
//        GridBagConstraints gbc_tfReply = new GridBagConstraints();
//        gbc_tfReply.gridwidth = 2;
//        gbc_tfReply.insets = new Insets(0, 0, 0, 5);
//        gbc_tfReply.fill = GridBagConstraints.HORIZONTAL;
//        gbc_tfReply.gridx = 1;
//        gbc_tfReply.gridy = 1;
//        contentPane.add(tfReply, gbc_tfReply);
//        tfReply.setColumns(10);
//
//        JButton btnSendReply = new JButton("send reply");
//        btnSendReply.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
////                RequestReply<BankInterestRequest, BankInterestReply> rr = list.getSelectedValue();
////                double interest = Double.parseDouble((tfReply.getText()));
////                BankInterestReply reply = new BankInterestReply(interest, bankName);
////                if (rr != null && reply != null) {
////                    rr.setReply(reply);
////                    list.repaint();
////                    // todo: sent JMS message with the reply to Loan Broker
////                    System.out.println("Bank sending BankInterestReply to Broker: " + String.valueOf(interest));
////                    brokerGateway.sendBankReply(rr.getRequest(), rr.getReply());
////                }
//            }
//        });
//        GridBagConstraints gbc_btnSendReply = new GridBagConstraints();
//        gbc_btnSendReply.anchor = GridBagConstraints.NORTHWEST;
//        gbc_btnSendReply.gridx = 4;
//        gbc_btnSendReply.gridy = 1;
//        contentPane.add(btnSendReply, gbc_btnSendReply);
    }

    private RequestReply<DealerQueryRequest, DealerQueryReply> getRequestReply(DealerQueryRequest request) {
        for (int i = 0; i < listModel.getSize(); i++) {
            RequestReply rr = listModel.get(i);
            if (rr.getRequest() == request) {
                return rr;
            }
        }
        return null;
    }

}
