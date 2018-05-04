package services.posts.Actions;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.PostMethods;
import static persistence.nosql.PostMethods.unlikePost;
import static shared.Helpers.createJSONError;

public class DeletePostLike implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) throws Exception {
        String postId = jsonObject.getString("postId");
        JSONObject post = PostMethods.getPost(postId);
        JSONArray likes = post.getJSONArray("likes");
        for (int i = 0; i < likes.length(); i++) {
            if (likes.get(i).equals(userId)) {
                unlikePost(postId, userId);
                JSONObject res = new JSONObject();
                JSONObject response = new JSONObject();
                response.put("method", methodName);
                res.put("postID", postId);
                response.put("response", res);
                return response;
            }
        }
        return createJSONError("You have not liked this post");
    }
}
