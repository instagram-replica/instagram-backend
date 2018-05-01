package services.posts.Actions;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.PostsCache;
import persistence.nosql.PostMethods;
import java.io.IOException;
import static shared.Helpers.isAuthorizedToView;

public class GetPost implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) throws CustomException, IOException, InterruptedException {
        String postId = jsonObject.getString("postId");
        JSONObject post = null;
        post = PostsCache.getPostFromCache(postId);
        if (post == null) {
            post = PostMethods.getPost(postId);
            PostsCache.insertPostIntoCache(post, postId);
        }
        JSONArray likes = post.getJSONArray("likes");
        int noOfLikes = likes.length();
        JSONObject response = new JSONObject();
        post.put("likes", noOfLikes);

        response.put("method", methodName);
        response.put("post", post);

        String ownerId = post.getString("user_id");
        System.out.println(userId + " :LOGGEDIN");
        System.out.println(ownerId + " :OWNER");
        if (isAuthorizedToView(userId, "posts", userId, ownerId)) {
            return response;
        }
        throw new CustomException("Not authorized to view");
    }
}
