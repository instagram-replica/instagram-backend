package services.stories.Actions;

import exceptions.CustomException;
import org.json.JSONObject;
import persistence.cache.StoriesCache;
import persistence.nosql.StoriesMethods;

public class GetStory implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId) throws CustomException {
        JSONObject story = new JSONObject();
        String storyID = jsonObject.getString("id");
        JSONObject storyResponse = StoriesCache.getStoryFromCache(storyID);
        if(storyResponse==null) {
            storyResponse = StoriesMethods.getStory(storyID);
            StoriesCache.insertStoryIntoCache(storyResponse,storyID);
        }
        story.put("response",storyResponse);
        return story;
    }
}