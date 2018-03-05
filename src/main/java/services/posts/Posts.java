package services.posts;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.ArangoInterfaceMethods;

public class Posts {

    //Tested
    public static JSONObject getPost(JSONObject paramsObject, String loggedInUserId, String methodName){
        String postId = paramsObject.getString("postId");
        JSONObject post = ArangoInterfaceMethods.getPost(postId);
        JSONObject response = new JSONObject();
        response.put("method", methodName);
        response.put("post", post);
        return response;
    }

    public static JSONObject getPosts(JSONObject paramsObject, String loggedInUserId, String methodName){
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        String ownerId = paramsObject.getString("userId");
        JSONArray posts = ArangoInterfaceMethods.getPosts(ownerId);
        JSONObject response = new JSONObject();
        response.put("method", methodName );
        response.put("posts", posts);
        return response;
    }

    public static JSONObject deletePost (JSONObject paramsObject, String loggedInUserId, String methodName){
        String postId = paramsObject.getString("postId");
        ArangoInterfaceMethods.deletePost(postId);
        JSONObject post = new JSONObject();
        JSONObject response = new JSONObject();
        response.put("method", methodName);
        post.put("id",postId );
        response.put("post", post);
        response.put("error", "0");
        return response;

    }

    public static JSONObject createPost(JSONObject paramsObject, String loggedInUserId, String methodName){
        String postId = ArangoInterfaceMethods.insertPost(paramsObject);
        JSONObject response = new JSONObject();
        JSONObject res = new JSONObject();
        res.put("postId", postId);
        res.put("error", "0");
        response.put("method", methodName);
        response.put("response", res);
        return response;
    }

    public static JSONObject createPostLike(JSONObject paramsObject, String loggedInUserId, String methodName){
        String postId = paramsObject.getString("postId");
        ArangoInterfaceMethods.likePost(postId, loggedInUserId);
        JSONObject res = new JSONObject();
        JSONObject response = new JSONObject();
        response.put("method", methodName );
        res.put("postID",  postId );
        res.put("error", "null");
        response.put("response", res);
        return response;

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
}
