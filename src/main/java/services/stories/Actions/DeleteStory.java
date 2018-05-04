package services.stories.Actions;

import org.json.JSONObject;
import persistence.nosql.StoriesMethods;

public class DeleteStory implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject deleteStory = new JSONObject();
        StoriesMethods.deleteStory(jsonObject.getString("id"));
        deleteStory.put("message","Story Deleted");
        return deleteStory;
    }
}
