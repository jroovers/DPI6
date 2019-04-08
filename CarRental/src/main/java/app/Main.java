package app;

import broker.BrokerFrame;
import client.ClientFrame;
import dealer.DealerFrame;
import java.awt.EventQueue;

/**
 *
 * @author Jeroen Roovers
 */
public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    DealerFrame bank = new DealerFrame("Dealer A", "dealerAQueue", "#{seats} >= 4 && #{period} >= 7 && #{price} >= 100");
                    bank.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    DealerFrame bank = new DealerFrame("Dealer B", "dealerBQueue", "#{seats} >= 4 && #{period} >= 1 && #{price} >= 149");
                    bank.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    DealerFrame bank = new DealerFrame("Dealer C", "dealerCQueue", "#{seats} >= 2 && #{period} >= 1 && #{price} >= 350");
                    bank.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    BrokerFrame broker = new BrokerFrame();
                    broker.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ClientFrame client = new ClientFrame();
                    client.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
