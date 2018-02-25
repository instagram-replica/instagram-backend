package services.posts;

import org.json.JSONObject;
import org.json.JSONArray;
import persistence.nosql.ArangoInterfaceMethods;
import persistence.sql.Post;
import persistence.sql.Posts_Likes;
import persistence.sql.Tagged_Posts;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Team2 {
    public static JSONObject getPost(JSONObject paramsObject, String loggedInUserId, String methodName){
        String postId = paramsObject.getString("postId");
        JSONObject post = ArangoInterfaceMethods.getPost(postId);
        JSONObject jsonValue = new JSONObject();
        jsonValue.put("method", methodName);
        jsonValue.put("post", post);
        return jsonValue;
    }

    public static JSONObject getPosts(JSONObject paramsObject, String loggedInUserId, String methodName){
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        String ownerId = paramsObject.getString("userId");
        JSONArray posts = ArangoInterfaceMethods.getPosts(ownerId);
        JSONObject jsonValue = new JSONObject();
        jsonValue.put("method", methodName );
        jsonValue.put("posts", posts);
        return jsonValue;
    }
//    public static JSONObject getTaggedPosts(JSONObject paramsObject, String loggedInUserId){
//        int pageSize = paramsObject.getInt("pageSize");
//        int pageIndex = paramsObject.getInt("pageIndex");
//        String ownerId = paramsObject.getString("userId");
//        List<Tagged_Posts> taggedPosts = Tagged_Posts.where("user_id = ? ", ownerId);
//        List<JSONObject> posts = new ArrayList<JSONObject>();
//        for (Tagged_Posts post:taggedPosts) {
//            JSONObject postsParams = new JSONObject();
//            postsParams.put("pageSize", pageSize);
//            postsParams.put("pageIndex", pageIndex);
//            postsParams.put("postId", post.get("post_id"));
//            posts.add(getPost(postsParams, loggedInUserId));
//        }
//        JSONObject jsonValue = new JSONObject();
//        jsonValue.put("tagged_posts", posts);
//        return jsonValue;
//
//    }

    public static JSONObject createPostLike(JSONObject paramsObject, String loggedInUserId, String methodName){
        String postId = paramsObject.getString("postId");
        ArangoInterfaceMethods.likePost(postId, loggedInUserId);
        JSONObject jsonValue = new JSONObject();
        JSONObject response = new JSONObject();
        jsonValue.put("method", methodName );
        response.put("postID",  postId );
        response.put("error", "null");
        jsonValue.put("response", response);
        return jsonValue;

    }



}

