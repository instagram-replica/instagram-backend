package services.posts;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.ArangoInterfaceMethods;

import java.io.IOException;

import static shared.Helpers.createJSONError;
import static shared.Helpers.isAuthorizedToView;

public class Posts {

    //TODO: Updates a post, take care of permissions
    //Done: create JSON req and res for this method in submission1 folder
    public static JSONObject updatePost(JSONObject paramsObject, String loggedInUserId, String methodName) {
        String postId = paramsObject.getString("postId");
        JSONObject post=null;
        JSONObject updatedPost=null;
        System.out.println(paramsObject);
        try {
            post = ArangoInterfaceMethods.getPost(postId);
            String ownerId = post.getString("user_id");
            if (loggedInUserId.equals(ownerId)) {
                ArangoInterfaceMethods.updatePost(postId,paramsObject);
                updatedPost =  ArangoInterfaceMethods.getPost(postId);
                JSONObject response = new JSONObject();

                //Replacing likes array with no of likes instead
//                JSONArray likes = updatedPost.getJSONArray("likes");
//                int noOfLikes= likes.length();
//                updatedPost.put("likes",noOfLikes);

                JSONObject postResponse = new JSONObject();
                postResponse.put("post", updatedPost);
                postResponse.put("error", "null");

                response.put("method", methodName);
                response.put("postId",postId);
                response.put("response", postResponse);

                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //TODO: Returns list of users (actual users not ids) who liked a post
    public static JSONObject getPostLikers(JSONObject paramsObject, String loggedInUserId, String methodName) {
        String postId = paramsObject.getString("postId");
        JSONObject post = null;
        try {
            post = ArangoInterfaceMethods.getPost(postId);
            String ownerId = post.getString("user_id");
            if (isAuthorizedToView("posts", loggedInUserId, ownerId)) {
                //TODO: @USERS_TEAM `getUsers`
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static JSONObject getPost(JSONObject paramsObject, String loggedInUserId, String methodName) {
        //DONE LOGICALLY: Get the post if the logged in user has permission to view it, otherwise return error
        //DONE: Calculate number of likes and return it, instead of the likes array
        String postId = paramsObject.getString("postId");
        JSONObject post = null;

        try {
                post = ArangoInterfaceMethods.getPost(postId);
                JSONArray likes = post.getJSONArray("likes");
                int noOfLikes= likes.length();
                JSONObject response = new JSONObject();
                post.put("likes",noOfLikes);

                response.put("method", methodName);
                response.put("post", post);

                String ownerId= post.getString("user_id");
                System.out.println(loggedInUserId+" :LOGGEDIN");
                System.out.println(ownerId+" :OWNER");
                if (isAuthorizedToView("posts", loggedInUserId, ownerId) || loggedInUserId.equals(ownerId)) {
                    return response;
               }
            return createJSONError("Not authorized to view");
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }
    }


    public static JSONObject getPosts(JSONObject paramsObject, String loggedInUserId, String methodName) {

        //DONE: Calculate number of likes and return it, instead of the likes array
        //TODO: Make use of the pagination params (do not spend much time on this)
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        String ownerId = paramsObject.getString("userId");
        try {
            if (isAuthorizedToView("posts", loggedInUserId, ownerId) || loggedInUserId.equals(ownerId)) {
                //@TODO: Check if the user exists
                JSONArray posts = ArangoInterfaceMethods.getPosts(ownerId);

                /// replacing likes array with no of likes instead
                for(int i=0; i<posts.length();i++){
                    JSONObject post= posts.getJSONObject(i);
                    System.out.println(post);
                    JSONArray likes = post.getJSONArray("likes");
                    int noOfLikes= likes.length();
                    post.put("likes",noOfLikes);
                }

                JSONObject response = new JSONObject();
                response.put("method", methodName);
                response.put("posts", posts);

                return response;
            }
            return createJSONError("Not authorized to view");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject deletePost(JSONObject paramsObject, String loggedInUserId, String methodName) {
        //Done: Delete the post iff the creator of the post is the logged in user
        String postId = paramsObject.getString("postId");
        try {
            JSONObject postToDelete = ArangoInterfaceMethods.getPost(postId);
            String ownerId= postToDelete.getString("user_id");
            if(loggedInUserId.equals(ownerId)) {
                ArangoInterfaceMethods.deletePost(postId);
                JSONObject post = new JSONObject();
                JSONObject response = new JSONObject();
                response.put("method", methodName);
                post.put("id", postId);
                response.put("post", post);
                response.put("error", "null");
                return response;
            }
            return createJSONError("Not authorized to delete");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject createPost(JSONObject paramsObject, String loggedInUserId, String methodName) {
        String postId = null;
        try {
            //TODO: Parse tags in media and @ACTIVITIES_TEAM create activities for their users
            //DONE: Return the newly created post instead of the ID only

        //    if(paramsObject.getJSONObject("post").get("user_id").toString().equals(loggedInUserId))
            postId = ArangoInterfaceMethods.insertPost(paramsObject.getJSONObject("post"),loggedInUserId);
            JSONObject response = new JSONObject();
            JSONObject postCreated = ArangoInterfaceMethods.getPost(postId);

            /// Replacing likes array with no of likes instead
            JSONArray likes = postCreated.getJSONArray("likes");
            int noOfLikes= likes.length();
            postCreated.put("likes",noOfLikes);

            JSONObject postResponse = new JSONObject();
            postResponse.put("post", postCreated);
            postResponse.put("error", "null");

            response.put("method", methodName);
            response.put("postId",postId);
            response.put("response", postResponse);

            return response;
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }
    }

    public static JSONObject createPostLike(JSONObject paramsObject, String loggedInUserId, String methodName) {
        //DONE: User cannot like a post more than once
        //TODO: Add unlike method and create JSON req and res
        //TODO: Create activity for the post owner @ACTIVITIES_TEAM, except if he is a retard who likes his own image
        String postId = paramsObject.getString("postId");
        try {
            JSONObject post = ArangoInterfaceMethods.getPost(postId);
            JSONArray likes = post.getJSONArray("likes");
            for(int i=0; i<likes.length();i++){
                if(likes.get(i).equals(loggedInUserId))
                    return createJSONError("You already liked this post");
            }
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

//    public static JSONObject deletePostLike(JSONObject paramsObject, String loggedInUserId, String methodName){
//        String postId = paramsObject.getString("postId");
//        try {
//            JSONObject post = ArangoInterfaceMethods.getPost(postId);
//            JSONArray likes = post.getJSONArray("likes");
//            for (int i = 0; i < likes.length(); i++) {
//                if (likes.get(i).equals(loggedInUserId)) {
//                ArangoInterfaceMethods.unlikePost(postId, loggedInUserId);
//                JSONObject res = new JSONObject();
//                JSONObject response = new JSONObject();
//                response.put("method", methodName);
//                res.put("postID", postId);
//                res.put("error", "null");
//                response.put("response", res);
//                    return response;
//            }
//        }
//            return createJSONError("You have not liked this post");
//        } catch (Exception e) {
//            return createJSONError(e.getMessage());
//        }
//    }s

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
