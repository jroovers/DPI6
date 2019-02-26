package app;

import bank.JMSBankFrame;
import java.awt.EventQueue;
import loanbroker.LoanBrokerFrame;
import loanclient.LoanClientFrame;

/**
 *
 * @author Jeroen Roovers // 2186859
 */
public class main {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JMSBankFrame bank = new JMSBankFrame("ABN AMRO", "abnRequestQueue");
                    bank.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JMSBankFrame bank = new JMSBankFrame("Rabobank", "raboRequestQueue");
                    bank.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JMSBankFrame bank = new JMSBankFrame("ING", "ingRequestQueue");
                    bank.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoanBrokerFrame broker = new LoanBrokerFrame();
                    broker.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoanClientFrame client = new LoanClientFrame();
                    client.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
