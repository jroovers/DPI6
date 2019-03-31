package model.query;

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
public class ClientQueryRequest {

    private int seats;
    private String brand;
    private int period;
    private int price;

    public ClientQueryRequest(int seats, String brand, int period, int price) {
        this.seats = seats;
        this.brand = brand;
        this.period = period;
        this.price = price;
    }
}
