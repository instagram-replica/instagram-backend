package services.posts.Actions;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.GraphMethods;
import persistence.nosql.PostMethods;

import static persistence.nosql.GraphMethods.isHashtagNode;
import static persistence.nosql.GraphMethods.makeHashtagNode;

public class CreatePostHashtags implements Action {
    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) throws CustomException {
        String postId = jsonObject.getString("postId");
        //String hashtag = paramsObject.getString("name");
        JSONArray hashtags = jsonObject.getJSONArray("name");
        JSONObject post = PostMethods.getPost(postId);
        String ownerId = post.getString("user_id");

        if (userId.equals(ownerId)) {
            for (int i = 0; i < hashtags.length(); i++) {
                if (!isHashtagNode("" + hashtags.get(i))) {
                    makeHashtagNode("" + hashtags.get(i));
                }
                GraphMethods.tagPostInHashtag("" + postId, "" + hashtags.get(i));
            }
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            response.put("post", post);
            response.put("name", hashtags);
            return response;
        }
        throw new CustomException("You are not the owner of this post");
    }
}
