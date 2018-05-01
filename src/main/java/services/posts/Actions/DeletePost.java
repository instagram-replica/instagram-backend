package services.posts.Actions;

import exceptions.CustomException;
import org.json.JSONObject;
import persistence.nosql.PostMethods;
import java.io.IOException;

public class DeletePost implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) throws CustomException, IOException, InterruptedException {
        String postId = jsonObject.getString("postId");
        JSONObject postToDelete = PostMethods.getPost(postId);
        String ownerId = postToDelete.getString("user_id");
        if (userId.equals(ownerId)) {
            PostMethods.deletePost(postId);
            JSONObject post = new JSONObject();
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            post.put("id", postId);
            response.put("post", post);
            return response;
        }
        throw new CustomException("Not authorized to delete");
    }
}
