package messaging.dynrouter;

import java.util.ArrayList;
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

    private List<DealerExtended> dealerList;

    private Evaluator evaluator;

    public RecipientList() {
        this.evaluator = new Evaluator();
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
        for (DealerExtended dealer : dealerList) {
            // evaluate
            if (true) {
                eligableDealers.add(dealer);
                System.out.println("Filter: " + dealer.getName() + " is eligible for this loanrequest.");
            }
        }
        return eligableDealers;
    }

    public void AddNewDealer(Dealer dealer, String channel, String filter) {
        DealerExtended de = new DealerExtended(channel, filter, dealer.getName());
        this.dealerList.add(de);
    }

    /**
     * Evaluator a bank expression with inputs
     *
     * @param expression expresion to evaluate in string format
     * @param amount amount to loan (use 0 or lower if not applicable)
     * @param time time to payment (use 0 or lower if not applicable)
     * @return true or false
     */
    private boolean evaluate(String expression, int amount, int time) throws EvaluationException {
        if (amount > 0) {
            this.evaluator.putVariable("amount", Integer.toString(amount));
        }
        if (time > 0) {
            this.evaluator.putVariable("time", Integer.toString(time));
        }
        boolean output = this.evaluator.getBooleanResult(expression);
        return output;
    }

    public List<DealerExtended> getDealerList() {
        return dealerList;
    }
}
