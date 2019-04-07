package model.query;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import model.Car;

/**
 *
 * @author Jeroen Roovers
 */
@Getter
@Setter
public class ClientQueryReply {

    public ClientQueryReply() {
        this.cars = new ArrayList<>();
    }

    public ClientQueryReply(List<Car> cars) {
        this.cars = cars;
    }

    private List<Car> cars;

//    @Override
//    public String toString() {
//        StringBuilder bd = new StringBuilder();
//        bd.append(cars.size()).append(" cars: ");
//        bd.append("/n");
//        for (Car c : cars) {
//            bd.append(c.toString());
//            bd.append("/n");
//        }
//        return bd.toString();
//    }
    @Override
    public String toString() {
        return "Dealer reply: " + this.cars.size() + " autos";
    }
}
