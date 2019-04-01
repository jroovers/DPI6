package messaging.gateway;

import messaging.serializer.ISerializer;

/**
 *
 * @author Jeroen Roovers
 */
public abstract class AbstractAppGateway<Request, Reply> {

    private MessageReceiverGateway receiver;
    private MessageSenderGateway sender;
    private ISerializer serializer;
    private String senderQueueName;

    public AbstractAppGateway() {
    }

    public void sendRequest(Request request) {
        String body = serializer.requestToString(request);

    }

    public void sendReply(Request request, Reply reply) {
        String body = serializer.replyToString(reply);

    }

    /**
     * This method is called when a message is received. The corresponding
     * request is fetched by the app gateway.
     *
     * @param request contains the original request
     */
    abstract void onRequestArrived(Request request);

    /**
     * This method is called when a message is received. The corresponding reply
     * is fetched by the app gateway.
     *
     * @param reply contains the reply
     */
    abstract void onReplyArrived(Reply request);
}
