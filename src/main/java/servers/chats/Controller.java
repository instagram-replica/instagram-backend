package servers.chats;

import org.json.JSONObject;

public class Controller extends shared.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject) {
        JSONObject newJsonObj = new JSONObject();
        newJsonObj.put("application", "chats");
        return newJsonObj;
    }
}
