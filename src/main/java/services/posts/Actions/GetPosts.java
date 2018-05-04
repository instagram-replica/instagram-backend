package services.posts.Actions;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.PostsCache;
import persistence.nosql.PostMethods;

import java.io.IOException;

import static shared.Helpers.isAuthorizedToView;

public class GetPosts implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) throws CustomException, IOException, InterruptedException {
        //TODO: Make use of the pagination params (do not spend much time on this)
        int pageSize = jsonObject.getInt("pageSize");
        int pageIndex = jsonObject.getInt("pageIndex");
        String ownerId = jsonObject.getString("userId");

        if (isAuthorizedToView(userId, "posts", userId, ownerId)) {
            //@TODO: Check if the user exists
            JSONArray posts = PostsCache.getPostsFromCache(ownerId, pageIndex, pageSize);
            if (posts == null) {
                posts = PostMethods.getPosts(ownerId);
                PostsCache.insertPostsIntoCache(posts, ownerId, pageIndex, pageSize);
            }

            // replacing likes array with no of likes instead
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);
                System.out.println(post);
                JSONArray likes = post.getJSONArray("likes");
                int noOfLikes = likes.length();
                post.put("likes", noOfLikes);
            }

            JSONObject response = new JSONObject();
            response.put("method", methodName);
            response.put("posts", posts);
            return response;
        }
        throw new CustomException("Not authorized to view");
    }
}
