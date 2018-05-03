package services.posts.Actions;

import exceptions.CustomException;
import org.json.JSONObject;
import persistence.nosql.PostMethods;

public class UpdatePost implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) throws CustomException {
        String postId = jsonObject.getString("postId");
        JSONObject post = null;
        JSONObject updatedPost = null;
        post = PostMethods.getPost(postId);
        String ownerId = post.getString("user_id");
        if (userId.equals(ownerId)) {
            PostMethods.updatePost(postId, jsonObject);
            updatedPost = PostMethods.getPost(postId);
            JSONObject response = new JSONObject();

            JSONObject postResponse = new JSONObject();
            postResponse.put("post", updatedPost);
            response.put("method", methodName);
            response.put("postId", postId);
            response.put("response", postResponse);
            return response;
        }
        throw new CustomException("Not authorized to update post");
    }
}
