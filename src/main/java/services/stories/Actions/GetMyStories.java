package services.stories.Actions;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.StoriesCache;
import persistence.nosql.StoriesMethods;

public class GetMyStories implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId) {
        //TODO: validate expiry time
        JSONObject myStory = new JSONObject();
        JSONArray stories = StoriesCache.getMyStoriesFromCache(userId);
        if(stories==null) {
            stories = StoriesMethods.getStoriesForUser(userId);
            StoriesCache.insertUserStoriesIntoCache(stories,userId);
        }
        myStory.put("response",stories);
        return myStory;
    }
}