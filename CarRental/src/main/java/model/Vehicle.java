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
public abstract class Vehicle {

    private Dealer dealer;
    private String brand;
    private String color;
    private int seatCount;

    public Vehicle(Dealer dealer, String brand, String color, int seatCount) {
        this.dealer = dealer;
        this.brand = brand;
        this.color = color;
        this.seatCount = seatCount;
    }
}
