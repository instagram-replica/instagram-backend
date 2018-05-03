package services.posts.Actions;

import org.json.JSONObject;
import java.util.ArrayList;

import static persistence.nosql.FeedMethods.getDiscoveryFeed;

public class GetDiscoverFeed implements Action {
    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) {
        int pageSize = jsonObject.getInt("pageSize");
        int pageIndex = jsonObject.getInt("pageIndex");

        ArrayList<JSONObject> feed = getDiscoveryFeed("" + userId, pageSize, pageIndex);
        JSONObject response = new JSONObject();
        response.put("method", methodName);
        response.put("posts", feed);

        return response;
    }
}
