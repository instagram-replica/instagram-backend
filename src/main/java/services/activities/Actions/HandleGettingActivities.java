package services.activities.Actions;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.ActivityMethods;
import persistence.nosql.GraphMethods;

import java.util.ArrayList;

public class HandleGettingActivities implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId) {
        int size = jsonObject.getInt("pageSize");
        int start = jsonObject.getInt("pageIndex") * size;
        ArrayList<String> followings = GraphMethods.getAllfollowingIDs(userId);
        JSONArray activities= ActivityMethods.getActivities(followings, start, size);
        JSONObject result = new JSONObject();
        result.put("activities", activities);
        return result;
    }
}

