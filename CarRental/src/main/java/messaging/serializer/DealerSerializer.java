/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.serializer;

import model.answer.DealerQueryReply;
import model.answer.DealerQueryRequest;

/**
 *
 * @author Jeroen Roovers <jroovers>
 */
public class DealerSerializer implements ISerializer<DealerQueryRequest, DealerQueryReply> {

    @Override
    public String requestToString(DealerQueryRequest request) {
        return GENSON.serialize(request);
    }

    @Override
    public DealerQueryRequest requestFromString(String str) {
        return GENSON.deserialize(str, DealerQueryRequest.class);
    }

    @Override
    public String replyToString(DealerQueryReply reply) {
        return GENSON.serialize(reply);
    }

    @Override
    public DealerQueryReply replyFromString(String str) {
        return GENSON.deserialize(str, DealerQueryReply.class);
    }

}
