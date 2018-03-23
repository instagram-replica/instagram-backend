package services.posts;

import com.arangodb.ArangoDBException;
import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.PostsCache;
import persistence.nosql.ArangoInterfaceMethods;
import shared.mq_server.Controller;
import java.io.IOException;
import java.util.ArrayList;

import static persistence.nosql.ArangoInterfaceMethods.*;
import static shared.Helpers.createJSONError;
import static shared.Helpers.getUsersByIds;
import static shared.Helpers.isAuthorizedToView;

public class Posts {
        //TODO: Updates a post, take care of permissions
        //Done: create JSON req and res for this method in submission1 folder
        public static JSONObject updatePost(JSONObject paramsObject, String loggedInUserId) throws CustomException{

        String postId = paramsObject.getString("postId");

        JSONObject post = null;
        JSONObject updatedPost = null;

            post = ArangoInterfaceMethods.getPost(postId);
            String ownerId = post.getString("user_id");
            if (loggedInUserId.equals(ownerId)) {
                ArangoInterfaceMethods.updatePost(postId, paramsObject);
                updatedPost = ArangoInterfaceMethods.getPost(postId);
                JSONObject response = new JSONObject();

            //Replacing likes array with no of likes instead
//                JSONArray likes = updatedPost.getJSONArray("likes");
//                int noOfLikes= likes.length();
//                updatedPost.put("likes",noOfLikes);

            JSONObject postResponse = new JSONObject();
            postResponse.put("post", updatedPost);

                response.put("postId", postId);
                response.put("response", postResponse);


            return response;
        }
        throw new CustomException("Not authorized to update post");

    }

    //TODO: Returns list of users (actual users not ids) who liked a post
    public static JSONObject getPostLikers(JSONObject paramsObject, String loggedInUserId) throws CustomException, IOException, InterruptedException{
        String postId = paramsObject.getString("postId");
        JSONObject post = null;
            post = ArangoInterfaceMethods.getPost(postId);

            String ownerId = post.getString("user_id");
            JSONArray userIds = post.getJSONArray("likes");
            if (isAuthorizedToView(loggedInUserId,"posts",loggedInUserId,ownerId) && userIds.length()!=0) {
                JSONObject response = new JSONObject();
                response.put("users", getUsersByIds("posts", userIds, loggedInUserId));
                return response;
            }
            throw new CustomException("Not authorized to view");
    }

    public static JSONObject getPost(JSONObject paramsObject, String loggedInUserId) throws Exception {
        //DONE LOGICALLY: Get the post if the logged in user has permission to view it, otherwise return error
        //DONE: Calculate number of likes and return it, instead of the likes array
        String postId = paramsObject.getString("postId");
        JSONObject post = null;
        post = PostsCache.getPostFromCache(postId);
        if(post==null){
            post = ArangoInterfaceMethods.getPost(postId);
            PostsCache.insertPostIntoCache(post,postId);
        }
        JSONArray likes = post.getJSONArray("likes");
        int noOfLikes= likes.length();
        JSONObject response = new JSONObject();
        post.put("likes",noOfLikes);

            response.put("post", post);

                String ownerId= post.getString("user_id");
                System.out.println(loggedInUserId+" :LOGGEDIN");
                System.out.println(ownerId+" :OWNER");
                if (isAuthorizedToView(loggedInUserId,"posts",loggedInUserId,ownerId)) {
                    return response;
               }
            throw new CustomException("Not authorized to view");

    }

    public static JSONObject getPosts(JSONObject paramsObject, String loggedInUserId) throws ArangoDBException, IOException, InterruptedException, CustomException {

        //TODO: Make use of the pagination params (do not spend much time on this)
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        String ownerId = paramsObject.getString("userId");

            if (isAuthorizedToView(loggedInUserId,"posts",loggedInUserId,ownerId)) {
                //@TODO: Check if the user exists
                JSONArray posts = PostsCache.getPostsFromCache(ownerId, pageIndex, pageSize);
                if (posts == null) {
                    posts = ArangoInterfaceMethods.getPosts(ownerId);
                    PostsCache.insertPostsIntoCache(posts, ownerId, pageIndex, pageSize);
                }
                /// replacing likes array with no of likes instead
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.getJSONObject(i);
                    System.out.println(post);
                    JSONArray likes = post.getJSONArray("likes");
                    int noOfLikes = likes.length();
                    post.put("likes", noOfLikes);
                }

                JSONObject response = new JSONObject();
                response.put("posts", posts);
                return response;
            }

        throw new CustomException("Not authorized to view");
        }

    public static JSONObject deletePost(JSONObject paramsObject, String loggedInUserId) throws Exception {
        String postId = paramsObject.getString("postId");
            JSONObject postToDelete = ArangoInterfaceMethods.getPost(postId);
            String ownerId = postToDelete.getString("user_id");
            if (loggedInUserId.equals(ownerId)) {
                ArangoInterfaceMethods.deletePost(postId);
                JSONObject post = new JSONObject();
                JSONObject response = new JSONObject();
                post.put("id", postId);
                response.put("post", post);
                return response;
            }
            throw new CustomException("Not authorized to delete");
    }

    public static JSONObject createPost(JSONObject paramsObject, String loggedInUserId) throws Exception{
        String postId = null;

        //TODO: Parse tags in media and @ACTIVITIES_TEAM create activities for their users

        //    if(paramsObject.getJSONObject("post").get("user_id").toString().equals(loggedInUserId))
        postId = ArangoInterfaceMethods.insertPost(paramsObject.getJSONObject("post"), loggedInUserId);
        JSONObject response = new JSONObject();
        JSONObject postCreated = ArangoInterfaceMethods.getPost(postId);

        /// Replacing likes array with no of likes instead
        JSONArray likes = postCreated.getJSONArray("likes");
        int noOfLikes = likes.length();
        postCreated.put("likes", noOfLikes);

        JSONObject postResponse = new JSONObject();
        postResponse.put("post", postCreated);

        response.put("postId",postId);
        response.put("response", postResponse);
        return response;


    }

    public static JSONObject createPostLike(JSONObject paramsObject, String loggedInUserId) throws Exception{
        //DONE: User cannot like a post more than once
        //TODO: Add unlike method and create JSON req and res
        String postId = paramsObject.getString("postId");

        JSONObject params = new JSONObject();
        JSONObject activities = new JSONObject();
        JSONObject post = ArangoInterfaceMethods.getPost(postId);
        String receiverId = post.getString("user_id");
        JSONArray likes = post.getJSONArray("likes");
        for(int i=0; i<likes.length();i++){
            if(likes.get(i).equals(loggedInUserId))
                throw new CustomException("You already liked this post");
        }
        ArangoInterfaceMethods.likePost(postId, loggedInUserId);
        JSONObject res = new JSONObject();
        JSONObject response = new JSONObject();
        res.put("postID", postId);
        response.put("response", res);
        params.put("postID",postId);
        params.put("receiverId",receiverId);
        activities.put("method","createPostLike");
        activities.put("params",params);
       Controller.send("posts","activities",activities,loggedInUserId);
        return response;
    }

    public static JSONObject deletePostLike(JSONObject paramsObject, String loggedInUserId, String methodName){
        String postId = paramsObject.getString("postId");
        try {
            JSONObject post = ArangoInterfaceMethods.getPost(postId);
            JSONArray likes = post.getJSONArray("likes");
            for (int i = 0; i < likes.length(); i++) {
                if (likes.get(i).equals(loggedInUserId)) {
                ArangoInterfaceMethods.unlikePost(postId, loggedInUserId);
                JSONObject res = new JSONObject();
                JSONObject response = new JSONObject();
                response.put("method", methodName);
                res.put("postID", postId);
                response.put("response", res);
                    return response;
            }
        }
            return createJSONError("You have not liked this post");
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }
    }

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

    public static JSONObject createPostTag(JSONObject paramsObject, String loggedInUserId, String methodName) throws Exception {
        String userId = paramsObject.getString("userId");
        String postId = paramsObject.getString("postId");
        JSONObject post = ArangoInterfaceMethods.getPost(postId);
        String postOwner = post.getString("user_id");
        if (isAuthorizedToView(loggedInUserId,"posts",loggedInUserId,userId) && loggedInUserId.equals(postOwner)) {
            tagUserInPost(""+ userId, ""+postId);
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            response.put("taggedId",userId);
            return response;
        }
        throw new CustomException("Not authorized to tag");
    }

    public static JSONObject getTaggedPosts(JSONObject paramsObject, String loggedInUserId, String methodName) throws Exception {
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        String userId = paramsObject.getString("userId");

        if(isAuthorizedToView(loggedInUserId,"posts",loggedInUserId,userId)) {
            ArrayList<String> postIds = ArangoInterfaceMethods.getAllTaggedPosts(userId);
            JSONArray posts = ArangoInterfaceMethods.getPosts(postIds);
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            response.put("posts", posts);
            return response;
        } else
            throw new CustomException("Not authorized to view");
    }

    //TODO:
    public static JSONObject getDiscoverFeed(JSONObject paramsObject, String loggedInUserId, String methodName) {
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");

        ArrayList<JSONObject> feed= ArangoInterfaceMethods.getDiscoveryFeed(""+loggedInUserId,pageSize,pageIndex);
        JSONObject response= new JSONObject();
        response.put("method", methodName);
        response.put("posts", feed);

        return response;
    }

    public static JSONObject createPostHashtags(JSONObject paramsObject, String loggedInUserId, String methodName) throws Exception {
        String postId = paramsObject.getString("postId");
        //String hashtag = paramsObject.getString("name");
        JSONArray hashtags =paramsObject.getJSONArray("name");
        JSONObject post = ArangoInterfaceMethods.getPost(postId);
        String ownerId = post.getString("user_id");

        if (loggedInUserId.equals(ownerId)) {
            for(int i=0; i<hashtags.length();i++) {
                if(!isHashtagNode(""+hashtags.get(i))) {
                    makeHashtagNode("" + hashtags.get(i));
                }
                ArangoInterfaceMethods.tagPostInHashtag("" + postId, "" + hashtags.get(i));
            }
            JSONObject response = new JSONObject();
            response.put("method",methodName);
            response.put("post",post);
            response.put("name",hashtags);
            return response;
        }
        throw new CustomException("You are not the owner of this post");
    }

    public static JSONObject getHashtagPosts(JSONObject paramsObject, String loggedInUserId, String methodName) {
        ArrayList<String> postIds= ArangoInterfaceMethods.getAllPostsTaggedInHashtag(""+paramsObject.getString("name"));
        JSONArray posts= ArangoInterfaceMethods.getPosts(postIds);

        JSONObject response = new JSONObject();
        response.put("method",methodName);
        response.put("posts",posts);
        return response;
    }
}
