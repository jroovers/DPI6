package app;

import broker.BrokerFrame;
import client.ClientFrame;
import dealer.DealerFrame;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import model.Car;
import model.Dealer;

/**
 *
 * @author Jeroen Roovers
 */
public class Main {

    public static void main(String[] args) {
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
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Dealer borent = new Dealer("Bo-rent");
                    List<Car> borentCars = new ArrayList<>();
                    Car car1 = new Car(borent, "BMW", "Blauw", 5, 130);
                    Car car2 = new Car(borent, "BMW", "Blauw", 5, 130);
                    Car car3 = new Car(borent, "Volkswagen", "Grijs", 5, 90);
                    Car car4 = new Car(borent, "Volkswagen", "Grijs", 5, 90);
                    Car car5 = new Car(borent, "Mercedes", "Zwart", 5, 250);
                    Car car6 = new Car(borent, "Toyota", "Rood", 4, 70);
                    Car car7 = new Car(borent, "Toyota", "Rood", 4, 70);
                    Car car8 = new Car(borent, "Toyota", "Rood", 4, 70);
                    Car car9 = new Car(borent, "Toyota", "Rood", 4, 70);
                    Car car10 = new Car(borent, "Toyota", "Rood", 4, 70);
                    borentCars.add(car1);
                    borentCars.add(car2);
                    borentCars.add(car3);
                    borentCars.add(car4);
                    borentCars.add(car5);
                    borentCars.add(car6);
                    borentCars.add(car7);
                    borentCars.add(car8);
                    borentCars.add(car9);
                    borentCars.add(car10);
                    DealerFrame bank = new DealerFrame("Bo-rent", "dealerAQueue", "#{seats} >= 4 && #{period} >= 7 && #{price} >= 100", borentCars);
                    bank.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Dealer sixt = new Dealer("Sixt");
                    List<Car> sixtCars = new ArrayList<>();
                    Car car1 = new Car(sixt, "BMW", "Blauw", 5, 130);
                    Car car2 = new Car(sixt, "BMW", "Blauw", 5, 130);
                    Car car3 = new Car(sixt, "Volkswagen", "Grijs", 5, 90);
                    Car car4 = new Car(sixt, "Volkswagen", "Grijs", 5, 90);
                    Car car5 = new Car(sixt, "Mercedes", "Zwart", 5, 250);
                    Car car6 = new Car(sixt, "Hyundai", "Oranje", 4, 70);
                    Car car7 = new Car(sixt, "Hyundai", "Oranje", 4, 70);
                    Car car8 = new Car(sixt, "Hyundai", "Oranje", 4, 70);
                    Car car9 = new Car(sixt, "Hyundai", "Oranje", 4, 70);
                    Car car10 = new Car(sixt, "Hyundai", "Oranje", 4, 70);
                    sixtCars.add(car1);
                    sixtCars.add(car2);
                    sixtCars.add(car3);
                    sixtCars.add(car4);
                    sixtCars.add(car5);
                    sixtCars.add(car6);
                    sixtCars.add(car7);
                    sixtCars.add(car8);
                    sixtCars.add(car9);
                    sixtCars.add(car10);
                    DealerFrame bank = new DealerFrame("Sixt", "dealerBQueue", "#{seats} >= 4 && #{period} >= 1 && #{price} >= 149", sixtCars);
                    bank.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Dealer europ = new Dealer("Europcar");
                    List<Car> europCars = new ArrayList<>();
                    Car car1 = new Car(europ, "BMW", "Blauw", 5, 130);
                    Car car2 = new Car(europ, "BMW", "Blauw", 5, 130);
                    Car car3 = new Car(europ, "Volkswagen", "Grijs", 5, 90);
                    Car car4 = new Car(europ, "Volkswagen", "Grijs", 5, 90);
                    Car car5 = new Car(europ, "Mercedes", "Zwart", 5, 250);
                    Car car6 = new Car(europ, "Opel", "Grijs", 4, 70);
                    Car car7 = new Car(europ, "Opel", "Grijs", 4, 70);
                    Car car8 = new Car(europ, "Opel", "Grijs", 4, 70);
                    Car car9 = new Car(europ, "Opel", "Grijs", 4, 70);
                    Car car10 = new Car(europ, "Opel", "Grijs", 4, 70);
                    europCars.add(car1);
                    europCars.add(car2);
                    europCars.add(car3);
                    europCars.add(car4);
                    europCars.add(car5);
                    europCars.add(car6);
                    europCars.add(car7);
                    europCars.add(car8);
                    europCars.add(car9);
                    europCars.add(car10);
                    DealerFrame bank = new DealerFrame("Europcar", "dealerCQueue", "#{seats} >= 2 && #{period} >= 1 && #{price} >= 350", europCars);
                    bank.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
