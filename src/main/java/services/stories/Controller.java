package services.stories;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.StoriesCache;
import persistence.nosql.ArangoInterfaceMethods;
import persistence.nosql.StoriesMethods;

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
                    data = getDiscoverStories(userId);
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
        createStory.put("story_id", StoriesMethods.insertStory(paramsObject));
        return createStory;
    }


    public static JSONObject deleteStory(JSONObject paramsObject) {
        JSONObject deleteStory = new JSONObject();
        StoriesMethods.deleteStory(paramsObject.getString("id"));
        deleteStory.put("message","Story Deleted");
        return deleteStory;
    }

    public static JSONObject getStory(JSONObject paramsObject) throws CustomException{
        JSONObject story = new JSONObject();
        String storyID = paramsObject.getString("id");
        JSONObject storyResponse = StoriesCache.getStoryFromCache(storyID);
        if(storyResponse==null) {
            storyResponse = StoriesMethods.getStory(storyID);
            Cache.insertStoryIntoCache(storyResponse,storyID);
        }
        story.put("response",storyResponse);
        return story;
    }

    public static JSONObject getMyStories(String userId) {
        //        @TODO: validate expiry time
        JSONObject myStory = new JSONObject();
        JSONArray stories = StoriesCache.getMyStoriesFromCache(userId);
        if(stories==null) {
            stories = StoriesMethods.getStoriesForUser(userId);
            Cache.insertUserStoriesIntoCache(stories,userId);
        }
        myStory.put("response",stories);
        return myStory;
    }

    public static JSONObject getStories(String userId) {
//        @TODO: validate expiry time
        JSONObject resultStories = new JSONObject();
        JSONArray allStories = StoriesCache.getUserStoriesFromCache(userId);
        if(allStories==null){
           allStories = StoriesMethods.getFriendsStories(userId);
           Cache.insertUserStoriesIntoCache(allStories,userId);
        }
        resultStories.put("response",allStories);
        return resultStories;
    }

    public static JSONObject getDiscoverStories(String userId) {
        JSONObject discoverStories = new JSONObject();
        JSONArray allStories = StoriesCache.getDiscoverStoriesFromCache(userId);
        if(allStories==null){
            allStories = StoriesMethods.getDiscoverStories(userId);
            Cache.insertDiscoverStoriesIntoCache(allStories,userId);
        }
        discoverStories.put("response",allStories);
        return discoverStories;
    }

}
