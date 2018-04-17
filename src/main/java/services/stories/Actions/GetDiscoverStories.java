package services.stories.Actions;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.StoriesCache;
import persistence.nosql.StoriesMethods;

public class GetDiscoverStories {

    public static JSONObject execute(JSONObject jsonObject, String userId){
        JSONObject discoverStories = new JSONObject();
        JSONArray allStories = StoriesCache.getDiscoverStoriesFromCache(userId);
        if(allStories==null){
            allStories = StoriesMethods.getDiscoverStories(userId);
            StoriesCache.insertDiscoverStoriesIntoCache(allStories,userId);
        }
        discoverStories.put("response",allStories);
        return discoverStories;
    }
}
