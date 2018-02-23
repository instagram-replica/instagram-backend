package shared;

import org.json.JSONObject;

public abstract class Controller {
    public Controller(){

    }

    public abstract JSONObject execute(JSONObject jsonObject, String userId);

}
