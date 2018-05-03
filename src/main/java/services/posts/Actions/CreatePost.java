package services.posts.Actions;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.PostMethods;

public class CreatePost implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) throws CustomException {

        //TODO: Parse tags in media and @ACTIVITIES_TEAM create activities for their users
        String postId = PostMethods.insertPost(jsonObject.getJSONObject("post"), userId);
        JSONObject response = new JSONObject();
        JSONObject postCreated = PostMethods.getPost(postId);

        /// Replacing likes array with no of likes instead
        JSONArray likes = postCreated.getJSONArray("likes");
        int noOfLikes = likes.length();
        postCreated.put("likes", noOfLikes);

        JSONObject postResponse = new JSONObject();
        postResponse.put("post", postCreated);

        response.put("method", methodName);
        response.put("postId", postId);
        response.put("response", postResponse);
        return response;
    }
}