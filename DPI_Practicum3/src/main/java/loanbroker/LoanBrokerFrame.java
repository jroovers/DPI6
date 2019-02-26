package loanbroker;

import loanbroker.scattergather.PropertyService;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import model.bank.*;
import model.loan.LoanReply;
import model.loan.LoanRequest;

public class LoanBrokerFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private DefaultListModel<JListLine> listModel = new DefaultListModel<JListLine>();
    private JList<JListLine> list;

    private BankAppGateway bankGateway;
    private LoanClientGateway clientGateway;

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
        bankGateway = new BankAppGateway() {
            @Override
            public void onBankReplyArrived(BankInterestRequest request, BankInterestReply reply) {
                if (reply != null) {
                    add(request, reply);
                    System.out.println("Broker sending LoanReply to client: " + reply.toString());
                    clientGateway.sendLoanReply(getRequestReply(request).getLoanRequest(), new LoanReply(reply.getInterest(), reply.getQuoteId()));
                } else {
                    BankInterestReply ineligibleReply = new BankInterestReply(-1.0, "NOT ELIGIBLE");
                    add(request, ineligibleReply);
                    clientGateway.sendLoanReply(getRequestReply(request).getLoanRequest(), new LoanReply(ineligibleReply.getInterest(), ineligibleReply.getQuoteId()));
                }
            }
        };

        clientGateway = new LoanClientGateway() {
            @Override
            void onLoanRequestArrived(LoanRequest loanreq) {
                add(loanreq);
                BankInterestRequest bankreq = new BankInterestRequest(loanreq.getAmount(), loanreq.getTime());
                System.out.println("Broker sending BankInterestRequest to bank: " + bankreq);
                add(loanreq, bankreq);
                bankGateway.sendBankRequest(bankreq);
            }
        };

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

    private JListLine getRequestReply(BankInterestRequest request) {

        for (int i = 0; i < listModel.getSize(); i++) {
            JListLine rr = listModel.get(i);
            if (rr.getBankRequest() == request) {
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

    public void add(BankInterestRequest request, BankInterestReply reply) {
        JListLine rr = getRequestReply(request);
        if (rr != null && reply != null) {
            rr.setBankReply(reply);;
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
