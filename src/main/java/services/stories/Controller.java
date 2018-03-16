package services.stories;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.Cache;
import persistence.nosql.ArangoInterfaceMethods;

public class Controller extends shared.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject newJsonObj = new JSONObject();

        String methodName = jsonObject.getString("method");
        JSONObject paramsObject = jsonObject.getJSONObject("params");

        switch(methodName){
            case "createStory":createStory(paramsObject);break;
            case "deleteStory":deleteStory(paramsObject);break;
            case "getMyStory":getMyStories(userId);break;
            case "getMyStories":getStories();break;
            case "getStory" :getStory(paramsObject);break;
            case "getDiscoverStories":getDiscoverStories();break;
        }

        newJsonObj.put("application", methodName);
        return newJsonObj;
    }


    public static void createStory(JSONObject paramsObject){
        JSONObject createStory = new JSONObject();
        if(!ArangoInterfaceMethods.insertStory(paramsObject).equals(null)){
            createStory.put("success","true");
            createStory.put("error","0");
        }else{
            createStory.put("success","false");
            createStory.put("error","Story not created");
        }
    }


    public static JSONObject deleteStory(JSONObject paramsObject){

        JSONObject delteStory = new JSONObject();
        if(ArangoInterfaceMethods.deleteStory(paramsObject.getString("id"))){
            delteStory.put("success","true");
            delteStory.put("error", "0");
        }else{
            delteStory.put("success","false");
            delteStory.put("error", "Story not deleted");
        }
        return delteStory;
    }

    public static JSONObject getStory(JSONObject paramsObject){
        JSONObject story = new JSONObject();
        String storyID = paramsObject.getString("id");
        JSONObject storyResponse = Cache.getStoryFromCache(storyID);
        if(storyResponse==null) {
            storyResponse = ArangoInterfaceMethods.getStory(storyID);
            Cache.insertStoryIntoCache(storyResponse,storyID);
        }
        story.put("error","0");
        story.put("response",storyResponse);
        return story;
    }
    public static JSONObject getMyStories(String userId){
        JSONObject myStory = new JSONObject();
        JSONArray stories = Cache.getUserStoriesFromCache(userId);
        if(stories==null) {
            stories = ArangoInterfaceMethods.getStories(userId);
            Cache.insertUserStoriesIntoCache(stories,userId);
        }
        myStory.put("error","0");
        myStory.put("response",stories);
        return myStory;
    }

    public static void getStories(){

    }

    public static void getDiscoverStories(){

    }

}
