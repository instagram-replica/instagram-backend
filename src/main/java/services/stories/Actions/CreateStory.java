package services.stories.Actions;

import org.json.JSONObject;
import persistence.nosql.StoriesMethods;

public class CreateStory implements Action{

    public static JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject createStory = new JSONObject();
        createStory.put("story_id", StoriesMethods.insertStory(jsonObject));
        return createStory;
    }
}
