/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.answer;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import model.Car;

/**
 *
 * @author Jeroen Roovers <jroovers>
 */
@Getter
@Setter
public class DealerQueryReply {

    public DealerQueryReply() {
        this.cars = new ArrayList<>();
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
