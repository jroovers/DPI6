/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.answer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Jeroen Roovers <jroovers>
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DealerQueryRequest {

    private int seats;
    private String brand;
    private int period;
    private int price;

    public DealerQueryRequest(int seats, String brand, int period, int price) {
        this.seats = seats;
        this.brand = brand;
        this.period = period;
        this.price = price;
    }
}
