package client;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import messaging.requestreply.RequestReply;
import model.query.ClientQueryReply;
import model.query.ClientQueryRequest;

/**
 *
 * @author Jeroen Roovers
 */
public class ClientFrame extends JFrame {

    private JPanel contentPane;
    private JLabel lblSeats;
    private JTextField tfSeats;
    private JTextField tfMaxPrice;
    private JLabel lblMaxPrice;
    private JLabel lblTime;
    private JTextField tfTime;
    private JLabel lblBrand;
    private JTextField tfBrand;
    private DefaultListModel<RequestReply<ClientQueryRequest, ClientQueryReply>> listModel = new DefaultListModel<>();
    private JList<RequestReply<ClientQueryRequest, ClientQueryReply>> requestReplyList;

    private ClientToBrokerGateway gateway;

    public ClientFrame() {
        setTitle("Client");
        gateway = new ClientToBrokerGateway() {
            @Override
            public void onClientReplyArrived(ClientQueryRequest request, ClientQueryReply reply) {
                RequestReply<ClientQueryRequest, ClientQueryReply> rr = getRequestReply(request);
                rr.setReply(reply);
                requestReplyList.repaint();
            }
        };

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 684, 619);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0, 0, 30, 30, 30, 30, 0};
        gbl_contentPane.rowHeights = new int[]{30, 30, 30, 30, 30};
        gbl_contentPane.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);

        lblSeats = new JLabel("Number of seats:");
        GridBagConstraints gbc_lblSeats = new GridBagConstraints();
        gbc_lblSeats.anchor = GridBagConstraints.WEST;
        gbc_lblSeats.insets = new Insets(0, 0, 5, 5);
        gbc_lblSeats.gridx = 0;
        gbc_lblSeats.gridy = 0;
        contentPane.add(lblSeats, gbc_lblSeats);

        tfSeats = new JTextField();
        GridBagConstraints gbc_tfSeats = new GridBagConstraints();
        gbc_tfSeats.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfSeats.insets = new Insets(0, 0, 5, 5);
        gbc_tfSeats.gridx = 1;
        gbc_tfSeats.gridy = 0;
        contentPane.add(tfSeats, gbc_tfSeats);
        tfSeats.setColumns(10);

        lblMaxPrice = new JLabel("Max price:");
        GridBagConstraints gbc_lblMaxPrice = new GridBagConstraints();
        gbc_lblMaxPrice.insets = new Insets(0, 0, 5, 5);
        gbc_lblMaxPrice.anchor = GridBagConstraints.WEST;
        gbc_lblMaxPrice.gridx = 0;
        gbc_lblMaxPrice.gridy = 1;
        contentPane.add(lblMaxPrice, gbc_lblMaxPrice);

        tfMaxPrice = new JTextField();
        GridBagConstraints gbc_tfAmount = new GridBagConstraints();
        gbc_tfAmount.anchor = GridBagConstraints.NORTH;
        gbc_tfAmount.insets = new Insets(0, 0, 5, 5);
        gbc_tfAmount.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfAmount.gridx = 1;
        gbc_tfAmount.gridy = 1;
        contentPane.add(tfMaxPrice, gbc_tfAmount);
        tfMaxPrice.setColumns(10);

        lblTime = new JLabel("Desired time period:");
        GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
        gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_1.gridx = 0;
        gbc_lblNewLabel_1.gridy = 2;
        contentPane.add(lblTime, gbc_lblNewLabel_1);

        tfTime = new JTextField();
        GridBagConstraints gbc_tfTime = new GridBagConstraints();
        gbc_tfTime.insets = new Insets(0, 0, 5, 5);
        gbc_tfTime.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfTime.gridx = 1;
        gbc_tfTime.gridy = 2;
        contentPane.add(tfTime, gbc_tfTime);
        tfTime.setColumns(10);

        lblBrand = new JLabel("(Optional) Brand:");
        GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
        gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_2.gridx = 0;
        gbc_lblNewLabel_2.gridy = 3;
        contentPane.add(lblBrand, gbc_lblNewLabel_2);

        tfBrand = new JTextField();
        GridBagConstraints gbc_tfBrand = new GridBagConstraints();
        gbc_tfBrand.insets = new Insets(0, 0, 5, 5);
        gbc_tfBrand.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfBrand.gridx = 1;
        gbc_tfBrand.gridy = 3;
        contentPane.add(tfBrand, gbc_tfBrand);
        tfBrand.setColumns(10);

        JButton btnQueue = new JButton("Send Query");
        btnQueue.addActionListener((ActionEvent e) -> {
            int seats = Integer.parseInt(tfSeats.getText());
            int time = Integer.parseInt(tfTime.getText());
            int maxprice = Integer.parseInt(tfMaxPrice.getText());
            String brand = tfBrand.getText();
            ClientQueryRequest request = new ClientQueryRequest(seats, brand, time, maxprice);
            listModel.addElement(new RequestReply<>(request, null));

            System.out.println("Sending request: " + request.toString());
            gateway.sendQuery(request);
        });
        GridBagConstraints gbc_btnQueue = new GridBagConstraints();
        gbc_btnQueue.insets = new Insets(0, 0, 5, 5);
        gbc_btnQueue.gridx = 2;
        gbc_btnQueue.gridy = 2;
        contentPane.add(btnQueue, gbc_btnQueue);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridheight = 7;
        gbc_scrollPane.gridwidth = 6;
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 4;

        contentPane.add(scrollPane, gbc_scrollPane);
        requestReplyList = new JList<>(listModel);
        requestReplyList.addListSelectionListener((e) -> {
            if (requestReplyList.getSelectedValue() != null) {
                if (requestReplyList.getSelectedValue().getReply() != null) {
                    System.out.println(requestReplyList.getSelectedValue().getReply().getCars());
                }
            }
        });
        scrollPane.setViewportView(requestReplyList);
    }

    /**
     * This method returns the RequestReply line that belongs to the request
     * from requestReplyList (JList). You can call this method when an reply
     * arrives in order to add this reply to the right request in
     * requestReplyList.
     *
     * @param request
     * @return
     */
    private RequestReply<ClientQueryRequest, ClientQueryReply> getRequestReply(ClientQueryRequest request) {

        for (int i = 0; i < listModel.getSize(); i++) {
            RequestReply<ClientQueryRequest, ClientQueryReply> rr = listModel.get(i);
            if (rr.getRequest() == request) {
                return rr;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ClientFrame frame = new ClientFrame();

                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
