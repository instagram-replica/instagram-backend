package services.users;

import org.json.JSONObject;
import shared.MQServer.Queue;

import java.io.IOException;

public class Controller extends shared.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) throws IOException {
        JSONObject newJsonObj = new JSONObject();
        newJsonObj.put("application", "users");

        JSONObject activitiesRes = this.send(new Queue("activities"), new JSONObject());

        newJsonObj.put("activities", activitiesRes);
        return newJsonObj;
    }
}
