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
                    data = createStory(paramsObject);
                    break;
                case "deleteStory":
                    data = deleteStory(paramsObject);
                    break;
                case "getMyStory":
                    data = getMyStories(userId);
                    break;
                case "getMyStories":
                    data = getStories(userId);
                    break;
                case "getStory":
                    data = getStory(paramsObject);
                    break;
                case "getDiscoverStories":
                    data = getDiscoverStories();
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


    public static JSONObject createStory(JSONObject paramsObject) {
        JSONObject createStory = new JSONObject();
        createStory.put("story_id",ArangoInterfaceMethods.insertStory(paramsObject));
        return createStory;
    }


    public static JSONObject deleteStory(JSONObject paramsObject) {
        JSONObject deleteStory = new JSONObject();
        ArangoInterfaceMethods.deleteStory(paramsObject.getString("id"));
        deleteStory.put("message","Story Deleted");
        return deleteStory;
    }

    public static JSONObject getStory(JSONObject paramsObject) throws CustomException{
        JSONObject story = new JSONObject();
        String storyID = paramsObject.getString("id");
        JSONObject storyResponse = Cache.getStoryFromCache(storyID);
        if(storyResponse==null) {
            storyResponse = ArangoInterfaceMethods.getStory(storyID);
            Cache.insertStoryIntoCache(storyResponse,storyID);
        }
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
        resultStories.put("response",allStories);
        return resultStories;
    }

    public static JSONObject getDiscoverStories() {
        return null;
    }

}
