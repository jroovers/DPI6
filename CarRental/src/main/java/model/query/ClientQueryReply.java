package model.query;

import java.util.ArrayList;
import java.util.List;
import model.Car;

/**
 *
 * @author Jeroen Roovers
 */
public class ClientQueryReply {

    public ClientQueryReply() {
        this.cars = new ArrayList<>();
    }

    private List<Car> cars;

    @Override
    public String toString() {
        StringBuilder bd = new StringBuilder();
        bd.append(cars.size()).append(" cars: ");
        bd.append("/n");
        for (Car c : cars) {
            bd.append(c.toString());
            bd.append("/n");
        }
        return bd.toString();
    }

}
