package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Jeroen Roovers
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Car extends Vehicle {

    private int horsepower;

    public Car(Dealer dealer, String brand, String color, int seatCount, int horsepower) {
        super(dealer, brand, color, seatCount);
        this.horsepower = horsepower;
    }
}
