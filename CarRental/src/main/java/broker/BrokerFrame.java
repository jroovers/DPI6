package broker;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import model.answer.DealerQueryReply;
import model.answer.DealerQueryRequest;
import model.query.ClientQueryRequest;

public class BrokerFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private DefaultListModel<JListLine> listModel = new DefaultListModel<>();
    private JList<JListLine> list;

    private BrokerToClientGateway clientGateway;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    BrokerFrame frame = new BrokerFrame();
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
    public BrokerFrame() {
        this.clientGateway = new BrokerToClientGateway() {
            @Override
            public void onQueryRequestArrived(ClientQueryRequest request) {
                System.out.println("received request");
                add(request);
            }
        };

        setTitle("Car Broker");
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

    private JListLine getRequestReply(ClientQueryRequest request) {

        for (int i = 0; i < listModel.getSize(); i++) {
            JListLine rr = listModel.get(i);
            if (rr.getClientRequest() == request) {
                return rr;
            }
        }
        return null;
    }

    private JListLine getRequestReply(DealerQueryRequest request) {

        for (int i = 0; i < listModel.getSize(); i++) {
            JListLine rr = listModel.get(i);
            if (rr.getDealerRequest() == request) {
                return rr;
            }
        }
        return null;
    }

    public void add(ClientQueryRequest clientRequest) {
        listModel.addElement(new JListLine(clientRequest));
    }

    public void add(ClientQueryRequest clientRequest, DealerQueryRequest dealerRequest) {
        JListLine rr = getRequestReply(clientRequest);
        if (rr != null && dealerRequest != null) {
            rr.setDealerRequest(dealerRequest);
            list.repaint();
        }
    }

    public void add(DealerQueryRequest dealerRequest, DealerQueryReply dealerReply) {
        JListLine rr = getRequestReply(dealerRequest);
        if (rr != null && dealerReply != null) {
            rr.setDealerReply(dealerReply);;
            list.repaint();
        }
    }

    public void add(ClientQueryRequest clientRequest, DealerQueryReply dealerReply) {
        JListLine rr = getRequestReply(clientRequest);
        if (rr != null && dealerReply != null) {
            rr.setDealerReply(dealerReply);;
            list.repaint();
        }
    }
}
