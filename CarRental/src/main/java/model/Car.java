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
public class Car extends Vehicle {

    private int horsepower;

    public Car(Dealer dealer, String brand, String color, int seatCount, int horsepower) {
        super(dealer, brand, color, seatCount);
        this.horsepower = horsepower;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("Car{");
        string.append("brand=").append(this.getBrand()).append(",");
        string.append("seats=").append(this.getSeatCount()).append(",");
        string.append("color=").append(this.getColor()).append(",");
        string.append("horsepower=").append(this.getHorsepower());
        string.append("}");
        return string.toString();
    }
}
