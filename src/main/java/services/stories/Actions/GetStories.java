package services.stories.Actions;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.StoriesCache;
import persistence.nosql.StoriesMethods;

public class GetStories implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId){
        //TODO: validate expiry time
        JSONObject resultStories = new JSONObject();
        JSONArray allStories = StoriesCache.getUserStoriesFromCache(userId);
        if(allStories==null){
            allStories = StoriesMethods.getFriendsStories(userId);
            StoriesCache.insertUserStoriesIntoCache(allStories,userId);
        }
        resultStories.put("response",allStories);
        return resultStories;
    }
}
