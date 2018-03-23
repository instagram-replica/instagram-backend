package services.stories;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.Cache;
import persistence.nosql.ArangoInterfaceMethods;

public class StoriesActions {

    private static JSONObject createStory(JSONObject paramsObject, String userId) {
        JSONObject createStory = new JSONObject();
        createStory.put("story_id", ArangoInterfaceMethods.insertStory(paramsObject));
        return createStory;
    }

    private static JSONObject deleteStory(JSONObject paramsObject, String userId) {
        JSONObject deleteStory = new JSONObject();
        ArangoInterfaceMethods.deleteStory(paramsObject.getString("id"));
        deleteStory.put("message","Story Deleted");
        return deleteStory;
    }

    private static JSONObject getStory(JSONObject paramsObject, String userId) throws CustomException {
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

    private static JSONObject getMyStories(JSONObject paramsObject, String userId) {
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

    private static JSONObject getStories(JSONObject paramsObject, String userId) {
        JSONObject resultStories = new JSONObject();
        JSONArray allStories = Cache.getUserStoriesFromCache(userId);
        if(allStories==null){
            allStories = ArangoInterfaceMethods.getFriendsStories(userId);
            Cache.insertUserStoriesIntoCache(allStories,userId);
        }
        resultStories.put("response",allStories);
        return resultStories;
    }

    private static JSONObject getDiscoverStories(JSONObject paramsObject, String userId) {
        JSONObject discoverStories = new JSONObject();
        JSONArray allStories = Cache.getDiscoverStoriesFromCache(userId);
        if(allStories==null){
            allStories = ArangoInterfaceMethods.getDiscoverStories(userId);
            Cache.insertDiscoverStoriesIntoCache(allStories,userId);
        }
        discoverStories.put("response",allStories);
        return discoverStories;
    }
}
