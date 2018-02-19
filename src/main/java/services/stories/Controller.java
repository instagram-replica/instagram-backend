package services.stories;

import org.json.JSONObject;

public class Controller extends shared.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject newJsonObj = new JSONObject();

        String methodName = jsonObject.getString("method");
//        JSONObject paramsObject = jsonObject.getJSONObject("params");

        System.out.println(methodName);

        switch(methodName){
            case "createStory":createStory();break;
            case "deleteStory":deleteStory();break;
            case "getMyStory":getMyStory();break;
            case "getMyStories":getStories();break;
            case "getDiscoverStories":getDiscoverStories();break;
        }

        newJsonObj.put("application", methodName);
        return newJsonObj;
    }


    public static void createStory(){
    }

    public static void deleteStory(){
    }

    public static void getMyStory(){
    }

    public static void getStories(){
    }

    public static void getDiscoverStories(){
    }

}
