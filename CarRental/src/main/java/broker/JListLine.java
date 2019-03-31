package broker;

import model.answer.*;
import model.query.*;

/**
 * @author 884294
 * @author Jeroen Roovers
 *
 */
class JListLine {

    private ClientQueryRequest clientRequest;
    private DealerQueryRequest dealerRequest;
    private DealerQueryReply dealerReply;

    public JListLine(ClientQueryRequest request) {
        this.clientRequest = request;
    }

    public ClientQueryRequest getClientRequest() {
        return clientRequest;
    }

    public void setClientRequest(ClientQueryRequest clientRequest) {
        this.clientRequest = clientRequest;
    }

    public DealerQueryRequest getDealerRequest() {
        return dealerRequest;
    }

    public void setDealerRequest(DealerQueryRequest dealerRequest) {
        this.dealerRequest = dealerRequest;
    }

    public DealerQueryReply getDealerReply() {
        return dealerReply;
    }

    public void setDealerReply(DealerQueryReply dealerReply) {
        this.dealerReply = dealerReply;
    }

    @Override
    public String toString() {
        return clientRequest.toString() + " || " + ((dealerReply != null) ? dealerReply.toString() : "waiting for reply...");
    }

}
