package services.posts.Actions;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.GraphMethods;
import persistence.nosql.PostMethods;

import java.util.ArrayList;

public class GetHashtagPosts implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) {

        ArrayList<String> postIds = GraphMethods.getAllPostsTaggedInHashtag("" + jsonObject.getString("name"));
        JSONArray posts = PostMethods.getPosts(userId);

        JSONObject response = new JSONObject();
        response.put("method", methodName);
        response.put("posts", posts);
        return response;
    }
}
