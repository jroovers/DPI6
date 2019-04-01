package messaging.serializer;

import model.query.ClientQueryReply;
import model.query.ClientQueryRequest;

/**
 *
 * @author Jeroen Roovers
 */
public class ClientSerializer implements ISerializer<ClientQueryRequest, ClientQueryReply> {

    @Override
    public String requestToString(ClientQueryRequest request) {
        return GENSON.serialize(request);
    }

    @Override
    public ClientQueryRequest requestFromString(String str) {
        return GENSON.deserialize(str, ClientQueryRequest.class);
    }

    @Override
    public String replyToString(ClientQueryReply reply) {
        return GENSON.serialize(reply);
    }

    @Override
    public ClientQueryReply replyFromString(String str) {
        return GENSON.deserialize(str, ClientQueryReply.class);
    }
}
