package messaging.dynrouter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.Dealer;

/**
 *
 * @author Jeroen Roovers
 */
@NoArgsConstructor
@Getter
@Setter
public class ControlMessage {

    private Dealer dealer;
    private String filter;
    private String queueName;
    private ControlType type;

    public ControlMessage(Dealer dealer, String filter, String queueName, ControlType type) {
        this.dealer = dealer;
        this.filter = filter;
        this.queueName = queueName;
        this.type = type;
    }
}
