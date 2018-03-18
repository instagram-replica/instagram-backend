package services.activities;

import org.json.JSONObject;

public class Controller extends shared.mq_server.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject newJsonObj = new JSONObject();
        newJsonObj.put("application", "activities");
        System.out.println(jsonObject);
        return newJsonObj;
    }

}
