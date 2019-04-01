package messaging.serializer;

import com.owlike.genson.Genson;

/**
 * Interface for serializing things with Genson
 *
 * @author Jeroen Roovers
 * @param <REQUEST> Generic
 * @param <REPLY> Generic
 */
public interface ISerializer<REQUEST, REPLY> {

    Genson GENSON = new Genson();

    public String requestToString(REQUEST request);

    public REQUEST requestFromString(String str);

    public String replyToString(REPLY reply);

    public REPLY replyFromString(String str);
}