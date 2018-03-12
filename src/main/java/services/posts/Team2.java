package services.posts;

import org.json.JSONObject;
import org.json.JSONArray;
import persistence.cache.Cache;
import persistence.nosql.ArangoInterfaceMethods;
import persistence.sql.Post;
import persistence.sql.Posts_Likes;
import persistence.sql.Tagged_Posts;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Team2 {
    //Tested
    public static JSONObject getPost(JSONObject paramsObject, String loggedInUserId, String methodName){
        String postId = paramsObject.getString("postId");
        JSONObject post = Cache.getPostFromCache(postId);
        if(post==null){
            post = ArangoInterfaceMethods.getPost(postId);
            Cache.insertPostIntoCache(post,postId);
        }
        JSONObject response = new JSONObject();
        response.put("method", methodName);
        response.put("post", post);
        return response;
    }

    public static JSONObject getPosts(JSONObject paramsObject, String loggedInUserId, String methodName){
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        String ownerId = paramsObject.getString("userId");
        JSONArray posts = Cache.getPostsFromCache(ownerId, pageIndex, pageSize);
        if(posts==null) {
             posts = ArangoInterfaceMethods.getPosts(ownerId);
             Cache.insertPostsIntoCache(posts,ownerId,pageIndex,pageSize);
        }
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
        JSONObject res = new JSONObject();
        JSONObject response = new JSONObject();
        response.put("method", methodName );
        res.put("postID",  postId );
        res.put("error", "null");
        response.put("response", res);
        return response;

    }
}

