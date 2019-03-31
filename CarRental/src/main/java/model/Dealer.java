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
@Setter
@Getter
@ToString
public class Dealer {

    private String name;

    public Dealer(String name) {
        this.name = name;
    }
}
