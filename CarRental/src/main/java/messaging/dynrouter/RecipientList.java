package messaging.dynrouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.Dealer;
import model.answer.DealerQueryRequest;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

/**
 *
 * @author Jeroen Roovers
 */
public class RecipientList {

    private HashMap<String, DealerExtended> dealerMap;
    private Evaluator evaluator;

    public RecipientList() {
        this.evaluator = new Evaluator();
        this.dealerMap = new HashMap<>();
    }

    /**
     * Check what banks are accepting a particular request.
     *
     * @param request
     * @return list of banks (0 banks if non eligable)
     * @throws EvaluationException
     */
    public List<DealerExtended> getEligableDealers(DealerQueryRequest request) throws EvaluationException {
        List<DealerExtended> eligableDealers = new ArrayList<>();
        dealerMap.entrySet().forEach((t) -> {
            try {
                if (evaluate(t.getValue().getFilter(), request.getSeats(), request.getPeriod(), request.getPrice())) {
                    eligableDealers.add(t.getValue());
                    System.out.println("Filter: " + t.getValue().getName() + " is eligible for this dealerRequet");
                }
            } catch (EvaluationException ex) {
                System.out.println("Evaluation exception for request!");
                ex.printStackTrace();
            }
        });
        return eligableDealers;
    }

    public void AddNewDealer(Dealer dealer, String channel, String filter) {
        DealerExtended de = new DealerExtended(channel, filter, dealer.getName());
        this.dealerMap.put(channel, de);
    }

    /**
     * Evaluator a bank expression with inputs
     *
     * @param expression expresion to evaluate in string format
     * @param amount amount to loan (use 0 or lower if not applicable)
     * @param time time to payment (use 0 or lower if not applicable)
     * @return true or false
     */
    private boolean evaluate(String expression, int seats, int period, int price) throws EvaluationException {
        if (seats > 0) {
            this.evaluator.putVariable("seats", Integer.toString(seats));
        }
        if (period > 0) {
            this.evaluator.putVariable("period", Integer.toString(period));
        }
        if (price > 0) {
            this.evaluator.putVariable("price", Integer.toString(price));
        }
        boolean output = this.evaluator.getBooleanResult(expression);
        this.evaluator.clearVariables();
        return output;
    }

    public List<DealerExtended> getDealerList() {
        List<DealerExtended> dealers = new ArrayList<>(dealerMap.values());
        return dealers;
    }
}
