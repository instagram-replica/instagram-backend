package services.stories;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.Cache;
import persistence.nosql.ArangoInterfaceMethods;

import java.util.ArrayList;

public class Controller extends shared.mq_server.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject newJsonObj = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject error = new JSONObject();
        String methodName = jsonObject.getString("method");
        JSONObject paramsObject = jsonObject.getJSONObject("params");
        try {
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
        }
        catch(org.json.JSONException e){
            e.printStackTrace();
            error.put("description",utilities.Main.stringifyJSONException(e));
        }
        catch(CustomException e){
            e.printStackTrace();
            error.put("description", e.getMessage());
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getMessage());
            error.put("description","Internal Server Error");
        }
        finally {
            JSONObject response = new JSONObject();
            response.put("error",error);
            response.put("data",data);
            return response;

        }

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

    public static JSONObject getStory(JSONObject paramsObject) throws CustomException{
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

    public static JSONObject getMyStories(String userId) {
        //        @TODO: validate expiry time
        JSONObject myStory = new JSONObject();
        JSONArray stories = Cache.getMyStoriesFromCache(userId);
        if(stories==null) {
            stories = ArangoInterfaceMethods.getStoriesForUser(userId);
            Cache.insertUserStoriesIntoCache(stories,userId);
        }
        myStory.put("error","0");
        myStory.put("response",stories);
        return myStory;
    }

    public static JSONObject getStories(String userId) {
//        @TODO: validate expiry time
        JSONObject resultStories = new JSONObject();
        JSONArray allStories = Cache.getUserStoriesFromCache(userId);
        if(allStories==null){
           allStories = ArangoInterfaceMethods.getFriendsStories(userId);
           Cache.insertUserStoriesIntoCache(allStories,userId);
        }
        resultStories.put("error","0");
        resultStories.put("response",allStories);
        return resultStories;
    }

    public static void getDiscoverStories() {

    }

}
