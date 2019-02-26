package loanbroker.scattergather;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import model.bank.BankInterestRequest;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

/**
 *
 * @author Jeroen Roovers
 */
public class RecipientList {

    private List<Bank> bankList;
    private Evaluator evaluator;

    public RecipientList() {
        this.evaluator = new Evaluator();
        getBanksFromPropertyFile();
    }

    /**
     * Check what banks are accepting a particular request.
     *
     * @param request
     * @return list of banks (0 banks if non eligable)
     * @throws EvaluationException
     */
    public List<Bank> getEligableBanks(BankInterestRequest request) throws EvaluationException {
        List<Bank> eligableBanks = new ArrayList<>();
        for (Bank bank : bankList) {
            if (evaluate(bank.getFilter(), request.getAmount(), request.getTime())) {
                eligableBanks.add(bank);
                System.out.println("Filter: " + bank.getBankName() + " is eligible for this loanrequest.");
            }
        }
        return eligableBanks;
    }

    /**
     * Get all banks from property file. Overwrites old bank details.
     */
    private void getBanksFromPropertyFile() {
        this.bankList = new ArrayList<Bank>();
        Properties props = PropertyService.getBankProperties();
        String banksAsCsv = PropertyService.readValue(props, "BANKS");
        String[] bankNames = banksAsCsv.split(",");
        for (String bankname : bankNames) {
            bankList.add(new Bank(
                    bankname,
                    PropertyService.readValue(props, "BANK." + bankname + ".QUEUE"),
                    PropertyService.readValue(props, "BANK." + bankname + ".RULES")));
        }
        System.out.println("Read all banks, found: " + bankList.size());
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

    public List<Bank> getBankList() {
        return bankList;
    }
}
