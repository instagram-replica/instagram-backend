package services.stories;

import org.json.JSONObject;

public class Controller extends shared.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject) {
        JSONObject newJsonObj = new JSONObject();
        newJsonObj.put("application", "feed/stories");
        return newJsonObj;
    }
}
