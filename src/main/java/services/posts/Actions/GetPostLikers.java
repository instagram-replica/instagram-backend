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
        System.out.println("hellooo");
        System.out.println(userIds);
        System.out.println("userid"+userId);
        System.out.println("ownerid"+ownerId);
        if (isAuthorizedToView(userId, "posts", userId, ownerId) && userIds.length() != 0) {
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            System.out.println( "hello; "+getUsersByIds("posts", userIds, userId));
            response.put("users", getUsersByIds("posts", userIds, userId));
            System.out.println(response);
            return response;
        }
        throw new CustomException("Not authorized to view");
    }
}
