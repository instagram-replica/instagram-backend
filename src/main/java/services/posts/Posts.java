package services.posts;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.ArangoInterfaceMethods;

import java.io.IOException;

import static shared.Helpers.createJSONError;
import static shared.Helpers.getUsersByIds;
import static shared.Helpers.isAuthorizedToView;

public class Posts {

    //TODO: Updates a post, take care of permissions
    //TODO: create JSON req and res for this method in submission1 folder
    public static JSONObject updatePost(JSONObject paramsObject, String loggedInUserId, String methodName) {
        return null;
    }

    //TODO: Returns list of users (actual users not ids) who liked a post
    public static JSONObject getPostLikers(JSONObject paramsObject, String loggedInUserId, String methodName) {
        String postId = paramsObject.getString("postId");
        JSONObject post = null;
        try {
            post = ArangoInterfaceMethods.getPost(postId);
            String ownerId = post.getString("user_id");
            JSONArray userIds = post.getJSONArray("likes");
            if (isAuthorizedToView("posts", loggedInUserId, ownerId)) {
                JSONObject response = new JSONObject();
                response.put("method", methodName);
                response.put("users", getUsersByIds("posts", userIds));
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static JSONObject getPost(JSONObject paramsObject, String loggedInUserId, String methodName) {
        //TODO: Get the post if the logged in user has permission to view it, otherwise return error
        //TODO: Calculate number of likes and return it, instead of the likes array
        String postId = paramsObject.getString("postId");
        JSONObject post = null;
        try {
            post = ArangoInterfaceMethods.getPost(postId);
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            response.put("post", post);
            return response;
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }
    }


    public static JSONObject getPosts(JSONObject paramsObject, String loggedInUserId, String methodName) {

        //TODO: Calculate number of likes and return it, instead of the likes array
        //TODO: Make use of the pagination params (do not spend much time on this)
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        String ownerId = paramsObject.getString("userId");
        try {
            if (isAuthorizedToView("posts", loggedInUserId, ownerId)) {
                //@TODO: Check if the user exists
                JSONArray posts = ArangoInterfaceMethods.getPosts(ownerId);
                JSONObject response = new JSONObject();
                response.put("method", methodName);
                response.put("posts", posts);
                System.out.println(response);
                return response;
            }
            return createJSONError("Not authorized to view");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject deletePost(JSONObject paramsObject, String loggedInUserId, String methodName) {
        //TODO: Delete the post iff the creator of the post is the logged in user
        String postId = paramsObject.getString("postId");
        ArangoInterfaceMethods.deletePost(postId);
        JSONObject post = new JSONObject();
        JSONObject response = new JSONObject();
        response.put("method", methodName);
        post.put("id", postId);
        response.put("post", post);
        response.put("error", "null");
        return response;

    }

    public static JSONObject createPost(JSONObject paramsObject, String loggedInUserId, String methodName) {
        String postId = null;
        try {
            //TODO: Parse tags in media and @ACTIVITIES_TEAM create activities for their users
            //TODO: Return the newly created post instead of the ID only
            postId = ArangoInterfaceMethods.insertPost(paramsObject.getJSONObject("post"), loggedInUserId);
            JSONObject response = new JSONObject();
            JSONObject res = new JSONObject();
            res.put("postId", postId);
            res.put("error", "null");
            response.put("method", methodName);
            response.put("response", res);
            return response;
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }
    }

    public static JSONObject createPostLike(JSONObject paramsObject, String loggedInUserId, String methodName) {
        //TODO: User cannot like a post more than once
        //TODO: Add unlike method and create JSON req and res
        //TODO: Create activity for the post owner @ACTIVITIES_TEAM, except if he is a retard who likes his own image
        String postId = paramsObject.getString("postId");
        try {
            ArangoInterfaceMethods.likePost(postId, loggedInUserId);
            JSONObject res = new JSONObject();
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            res.put("postID", postId);
            res.put("error", "null");
            response.put("response", res);
            return response;
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }
    }

    //TODO:
    public static JSONObject getTaggedPosts(JSONObject paramsObject, String loggedInUserId) {
        //@TODO: Check if the user has permission to view the other user's profile
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        String ownerId = paramsObject.getString("userId");
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
        return new JSONObject();

    }

    //TODO:
    public static JSONObject getDiscoverFeed(JSONObject paramsObject, String loggedInUserId) {
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        return new JSONObject();
    }

    //TODO:
    public static JSONObject getHashtagPosts(JSONObject paramsObject, String loggedInUserId) {
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        return new JSONObject();
    }


}
