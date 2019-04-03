package messaging.serializer;

import com.owlike.genson.Genson;
import messaging.dynrouter.ControlMessage;

/**
 *
 * @author Jeroen Roovers
 */
public class ControlSerializer {

    Genson GENSON = new Genson();

    public String controlToString(ControlMessage msg) {
        return GENSON.serialize(msg);
    }

    public ControlMessage stringToControl(String body) {
        return GENSON.deserialize(body, ControlMessage.class);
    }

}
