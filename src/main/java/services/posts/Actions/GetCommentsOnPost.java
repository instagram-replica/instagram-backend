package services.posts.Actions;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.PostsCache;
import persistence.nosql.PostMethods;
import shared.Settings;
import java.io.IOException;
import static shared.Helpers.createJSONError;
import static shared.Helpers.isAuthorizedToView;

public class GetCommentsOnPost implements Action {
    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) throws IOException, InterruptedException, CustomException {
        String postId = jsonObject.getString("postId");
        JSONObject post = PostsCache.getPostFromCache(postId);
        if (post == null) {
            post = PostMethods.getPost(postId);
            PostsCache.insertPostIntoCache(post, postId);
        }
        if (isAuthorizedToView(Settings.getInstance().getInstanceId(), userId, post.getString("user_id"), userId)) {
            JSONArray comments = PostsCache.getCommentsFromCache(postId);
            if (comments == null) {
                comments = PostMethods.getPosts(postId);
                PostsCache.insertCommentsIntoCache(comments, postId);
            }
            JSONObject jsonValue = new JSONObject();
            jsonValue.put("method", methodName);
            jsonValue.put("count", comments.length());
            jsonValue.put("comments", comments);
            jsonValue.put("error", "null");
            return jsonValue;
        }
        return createJSONError("Not authorized to view");

    }
}
