package services.posts.Actions;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.GraphMethods;
import persistence.nosql.PostMethods;

import java.io.IOException;
import java.util.ArrayList;

import static shared.Helpers.isAuthorizedToView;

public class GetTaggedPosts implements Action {
    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) throws IOException, InterruptedException, CustomException {
        int pageSize = jsonObject.getInt("pageSize");
        int pageIndex = jsonObject.getInt("pageIndex");
        String user = jsonObject.getString("userId");

        if (isAuthorizedToView(userId, "posts", userId, user)) {
            ArrayList<String> postIds = GraphMethods.getAllTaggedPosts(user);
            JSONArray posts = PostMethods.getPosts(postIds);
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            response.put("posts", posts);
            return response;
        } else
            throw new CustomException("Not authorized to view");
    }
}
