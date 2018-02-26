package services.posts;

import org.json.JSONObject;
import persistence.sql.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Team1 {
    public static JSONObject getPosts(JSONObject paramsObject, String loggedInUserId) {
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        String ownerId = paramsObject.getString("userId");

        List<Post> posts = Post.where("user_id = ? ", ownerId);
        List<JSONObject> jsonObjects =
                posts
                    .stream()
                    .map(post -> mapPostToJSONObj(post))
                    .collect(Collectors.toList());

        JSONObject jsonValue = new JSONObject();
        jsonValue.put("posts", jsonObjects);
        jsonValue.put("error", "0");

        return jsonValue;
    }

    public static JSONObject mapPostToJSONObj(Post post) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", post.get("id"));
        jsonObject.put("created_at", post.get("created_at"));
        jsonObject.put("updated_at", post.get("updated_at"));
        jsonObject.put("caption", post.get("caption"));
        return jsonObject;
    }

}
