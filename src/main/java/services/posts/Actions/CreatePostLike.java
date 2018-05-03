package services.posts.Actions;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.PostMethods;

import java.io.IOException;

public class CreatePostLike implements Action {
    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) throws Exception {

        //TODO: Create activity for the post owner @ACTIVITIES_TEAM, except if he is a retard who likes his own image
        String postId = jsonObject.getString("postId");

        JSONObject post = PostMethods.getPost(postId);
        JSONArray likes = post.getJSONArray("likes");
        for (int i = 0; i < likes.length(); i++) {
            if (likes.get(i).equals(userId))
                throw new CustomException("You already liked this post");
        }

        PostMethods.likePost(postId, userId);
        JSONObject res = new JSONObject();
        JSONObject response = new JSONObject();
        response.put("method", methodName);
        res.put("postID", postId);
        response.put("response", res);
        return response;
    }
}
