package services.posts.Actions;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.PostMethods;

import java.io.IOException;

import static shared.Helpers.getUsersByIds;
import static shared.Helpers.isAuthorizedToView;

public class GetPostLikers implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) throws CustomException, IOException, InterruptedException {
        String postId = jsonObject.getString("postId");
        JSONObject post = null;
        post = PostMethods.getPost(postId);

        String ownerId = post.getString("user_id");
        JSONArray userIds = post.getJSONArray("likes");

        if (isAuthorizedToView(userId, "posts", userId, ownerId) && userIds.length() != 0) {
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            response.put("users", getUsersByIds("posts", userIds, userId));
            return response;
        }
        throw new CustomException("Not authorized to view");
    }
}
