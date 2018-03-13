package services.stories;

import org.json.JSONObject;
import persistence.nosql.ArangoInterfaceMethods;

public class Controller extends shared.MQServer.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject newJsonObj = new JSONObject();

        String methodName = jsonObject.getString("method");
        JSONObject paramsObject = jsonObject.getJSONObject("params");

        switch (methodName) {
            case "createStory":
                createStory(paramsObject);
                break;
            case "deleteStory":
                deleteStory(paramsObject);
                break;
            case "getMyStory":
                getMyStories(userId);
                break;
            case "getMyStories":
                getStories();
                break;
            case "getStory":
                getStory(paramsObject);
                break;
            case "getDiscoverStories":
                getDiscoverStories();
                break;
        }

        newJsonObj.put("application", methodName);
        return newJsonObj;
    }


    public static void createStory(JSONObject paramsObject) {
        JSONObject createStory = new JSONObject();
        if (!ArangoInterfaceMethods.insertStory(paramsObject).equals(null)) {
            createStory.put("success", "true");
            createStory.put("error", "0");

        } else {
            createStory.put("success", "false");
            createStory.put("error", "Story not created");
        }
    }


    public static JSONObject deleteStory(JSONObject paramsObject) {

        JSONObject delteStory = new JSONObject();
        if (ArangoInterfaceMethods.deleteStory(paramsObject.getString("id"))) {
            delteStory.put("success", "true");
            delteStory.put("error", "0");
        } else {
            delteStory.put("success", "false");
            delteStory.put("error", "Story not deleted");
        }
        return delteStory;
    }

    public static JSONObject getStory(JSONObject paramsObject) {
        JSONObject story = new JSONObject();
        story.put("error", "0");
        story.put("response", ArangoInterfaceMethods.getStory(paramsObject.getString("id")));
        return story;
    }

    public static JSONObject getMyStories(String userId) {
        //        @TODO: validate expiry time
        JSONObject myStory = new JSONObject();
        myStory.put("error", "0");
        myStory.put("response", ArangoInterfaceMethods.getStories(userId));
        return myStory;
    }

    public static void getStories() {
//        @TODO: validate expiry time
    }

    public static void getDiscoverStories() {

    }

}
