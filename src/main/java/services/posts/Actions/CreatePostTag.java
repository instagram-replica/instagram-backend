package services.posts.Actions;

import exceptions.CustomException;
import org.json.JSONObject;
import persistence.nosql.PostMethods;
import static persistence.nosql.GraphMethods.tagUserInPost;
import static shared.Helpers.isAuthorizedToView;

public class CreatePostTag implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) throws Exception {
        String user = jsonObject.getString("userId");
        String postId = jsonObject.getString("postId");
        JSONObject post = PostMethods.getPost(postId);
        String postOwner = post.getString("user_id");
        if (isAuthorizedToView(userId, "posts", userId, user) && userId.equals(postOwner)) {
            tagUserInPost("" + user, "" + postId);
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            response.put("taggedId", user);
            return response;

        }
        throw new CustomException("Not authorized to tag");
    }
}
