package services.stories.Actions;

import org.json.JSONObject;
import persistence.nosql.StoriesMethods;

public class CreateStory implements Action{

    public static JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject createStory = new JSONObject();
        System.out.println("USERIDCREATE:  "+userId);
        createStory.put("story_id", StoriesMethods.insertStory(jsonObject, userId));
        return createStory;
    }
}
