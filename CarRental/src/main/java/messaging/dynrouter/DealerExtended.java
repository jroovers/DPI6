/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.dynrouter;

import model.Dealer;

/**
 *
 * @author Jeroen Roovers <jroovers>
 */
public class DealerExtended extends Dealer {
    
    private String queuename;
    private String filter;

    public DealerExtended(String queuename, String filter, String name) {
        super(name);
        this.queuename = queuename;
        this.filter = filter;
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
