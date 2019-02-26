package loanbroker.scattergather;

/**
 *
 * @author Jeroen Roovers
 *
 * Simple representation of bank including its name, queue listening location
 * address and filter.
 */
public class Bank {

    private String bankName;
    private String queuename;
    private String filter;

    public Bank() {

    }

    public Bank(String name, String queue, String filter) {
        this.bankName = name;
        this.queuename = queue;
        this.filter = filter;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getQueuename() {
        return queuename;
    }

    public void setQueuename(String queuename) {
        this.queuename = queuename;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

}
