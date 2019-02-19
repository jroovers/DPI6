package gateway.utility;

import model.loan.LoanReply;
import model.loan.LoanRequest;

/**
 * Implementation of ISerializer for Loanrequest and Loanreply
 *
 * @author Jeroen Roovers
 */
public class LoanSerializer implements ISerializer<LoanRequest, LoanReply> {

    @Override
    public String requestToString(LoanRequest request) {
        return GENSON.serialize(request);
    }

    @Override
    public LoanRequest requestFromString(String str) {
        return GENSON.deserialize(str, LoanRequest.class);
    }

    @Override
    public String replyToString(LoanReply reply) {
        return GENSON.serialize(reply);
    }

    @Override
    public LoanReply replyFromString(String str) {
        return GENSON.deserialize(str, LoanReply.class);
    }

}
