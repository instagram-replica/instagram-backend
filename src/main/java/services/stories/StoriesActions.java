package services.stories;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.StoriesCache;
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
        JSONObject storyResponse = StoriesCache.getStoryFromCache(storyID);
        if(storyResponse==null) {
            storyResponse = ArangoInterfaceMethods.getStory(storyID);
            StoriesCache.insertStoryIntoCache(storyResponse,storyID);
        }
        story.put("response",storyResponse);
        return story;
    }

    private static JSONObject getMyStories(JSONObject paramsObject, String userId) {
        //        @TODO: validate expiry time
        JSONObject myStory = new JSONObject();
        JSONArray stories = StoriesCache.getMyStoriesFromCache(userId);
        if(stories==null) {
            stories = ArangoInterfaceMethods.getStoriesForUser(userId);
            StoriesCache.insertUserStoriesIntoCache(stories,userId);
        }
        myStory.put("response",stories);
        return myStory;
    }

    private static JSONObject getStories(JSONObject paramsObject, String userId) {
        JSONObject resultStories = new JSONObject();
        JSONArray allStories = StoriesCache.getUserStoriesFromCache(userId);
        if(allStories==null){
            allStories = ArangoInterfaceMethods.getFriendsStories(userId);
            StoriesCache.insertUserStoriesIntoCache(allStories,userId);
        }
        resultStories.put("response",allStories);
        return resultStories;
    }

    private static JSONObject getDiscoverStories(JSONObject paramsObject, String userId) {
        JSONObject discoverStories = new JSONObject();
        JSONArray allStories = StoriesCache.getDiscoverStoriesFromCache(userId);
        if(allStories==null){
            allStories = ArangoInterfaceMethods.getDiscoverStories(userId);
            StoriesCache.insertDiscoverStoriesIntoCache(allStories,userId);
        }
        discoverStories.put("response",allStories);
        return discoverStories;
    }
}
