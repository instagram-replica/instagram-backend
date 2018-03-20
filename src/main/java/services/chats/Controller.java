package services.chats;

import org.json.JSONObject;

public class Controller extends shared.mq_server.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject newJsonObj = new JSONObject();
        newJsonObj.put("application", "activities");
        String methodName = jsonObject.getString("method");
        JSONObject paramsObject = jsonObject.getJSONObject("params");

        //interface insert method, change params of json object to match different activity
        //types
        switch (methodName) {
            case "createMessage":
                newJsonObj = Messenger.createMessage(paramsObject, userId);
                break;
            case "createThread":
                newJsonObj = Messenger.createThread(paramsObject, userId);
                break;
            case "getMessages":
                newJsonObj = Messenger.getMessages(paramsObject, userId);
            case "getThreads":
                newJsonObj = Messenger.getThreads(paramsObject, userId);
                break;
            default:
                break;
        }
        return newJsonObj;
    }
}
