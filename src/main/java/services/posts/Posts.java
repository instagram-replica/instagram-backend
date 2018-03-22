package services.posts;

import com.arangodb.ArangoDBException;
import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.Cache;
import persistence.nosql.ArangoInterfaceMethods;
import shared.Settings;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import static persistence.nosql.ArangoInterfaceMethods.*;
import static persistence.nosql.ArangoInterfaceMethods.followUser;
import static shared.Helpers.createJSONError;
import static shared.Helpers.isAuthorizedToView;

public class Posts {

    public static JSONObject updatePost(JSONObject paramsObject, String loggedInUserId, String methodName) throws CustomException{
        String postId = paramsObject.getString("postId");
        JSONObject post=null;
        JSONObject updatedPost=null;

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

            response.put("method", methodName);
            response.put("postId",postId);
            response.put("response", postResponse);

            return response;
        }
        throw new CustomException("Not authorized to update post");

    }

    //TODO: Returns list of users (actual users not ids) who liked a post
    public static JSONObject getPostLikers(JSONObject paramsObject, String loggedInUserId, String methodName) throws CustomException, IOException, InterruptedException{
        String postId = paramsObject.getString("postId");
        JSONObject post = null;
        post = ArangoInterfaceMethods.getPost(postId);
        String ownerId = post.getString("user_id");
        if (isAuthorizedToView(Settings.getInstance().getInstanceId(), loggedInUserId, ownerId)) {
            //TODO: @USERS_TEAM `getUsers`
        }
        throw new CustomException("Not authorized to view");
    }

    public static JSONObject getPost(JSONObject paramsObject, String loggedInUserId, String methodName) throws Exception {
        String postId = paramsObject.getString("postId");
        JSONObject post = null;
        post = Cache.getPostFromCache(postId);
        if(post==null){
            post = ArangoInterfaceMethods.getPost(postId);
            Cache.insertPostIntoCache(post,postId);
        }
        JSONArray likes = post.getJSONArray("likes");
        int noOfLikes= likes.length();
        JSONObject response = new JSONObject();
        post.put("likes",noOfLikes);

        response.put("method", methodName);
        response.put("post", post);

        String ownerId= post.getString("user_id");

        if (isAuthorizedToView("posts", loggedInUserId, ownerId) || loggedInUserId.equals(ownerId)) {
            return response;
       }
        throw new CustomException("Not authorized to view");

    }


    public static JSONObject getPosts(JSONObject paramsObject, String loggedInUserId, String methodName) throws ArangoDBException, CustomException, IOException, InterruptedException{
        //TODO: Make use of the pagination params (do not spend much time on this)
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        String ownerId = paramsObject.getString("userId");
        if (isAuthorizedToView("posts", loggedInUserId, ownerId) || loggedInUserId.equals(ownerId)) {
            //@TODO: Check if the user exists
            JSONArray posts = Cache.getPostsFromCache(ownerId, pageIndex, pageSize);
            if(posts==null) {
                posts = ArangoInterfaceMethods.getPosts(ownerId);
                Cache.insertPostsIntoCache(posts,ownerId,pageIndex,pageSize);
            }

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
        throw new CustomException("Not authorized to view");
    }

    public static JSONObject deletePost(JSONObject paramsObject, String loggedInUserId, String methodName) throws Exception {
        String postId = paramsObject.getString("postId");
        JSONObject postToDelete = ArangoInterfaceMethods.getPost(postId);
        String ownerId= postToDelete.getString("user_id");

        if(loggedInUserId.equals(ownerId)) {
            ArangoInterfaceMethods.deletePost(postId);
            JSONObject post = new JSONObject();
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            post.put("id", postId);
            response.put("post", post);
            return response;
        }
        throw new CustomException("Not authorized to delete");
    }

    public static JSONObject createPost(JSONObject paramsObject, String loggedInUserId, String methodName) throws Exception{
        String postId = null;

            //TODO: Parse tags in media and @ACTIVITIES_TEAM create activities for their users

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

        response.put("method", methodName);
        response.put("postId",postId);
        response.put("response", postResponse);
        return response;

    }

    public static JSONObject createPostLike(JSONObject paramsObject, String loggedInUserId, String methodName) throws Exception{
        //TODO: Create activity for the post owner @ACTIVITIES_TEAM, except if he is a retard who likes his own image
        String postId = paramsObject.getString("postId");

            JSONObject post = ArangoInterfaceMethods.getPost(postId);
            JSONArray likes = post.getJSONArray("likes");
            for(int i=0; i<likes.length();i++){
                if(likes.get(i).equals(loggedInUserId))
                    throw new CustomException("You already liked this post");
            }
            ArangoInterfaceMethods.likePost(postId, loggedInUserId);
            JSONObject res = new JSONObject();
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            res.put("postID", postId);
            response.put("response", res);
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

//    public static JSONObject createPostTag(JSONObject paramsObject, String loggedInUserId, String methodName){
//
//    }

    //TODO:
    public static JSONObject getTaggedPosts(JSONObject paramsObject, String loggedInUserId, String methodName) throws Exception {
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        String ownerId = paramsObject.getString("userId");

//        JSONObject obj2= new JSONObject();
//        obj2.put("user_id",loggedInUserId);
//        obj2.put("caption","Taken By Mohamed ABouzeid");
//        obj2.put("media", new ArrayList<String>());
//        obj2.put("likes", new ArrayList<String>());
//        obj2.put("tags",new ArrayList<String>());
//        obj2.put("location","{ name: C1, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
//        obj2.put("comments", new ArrayList<String>());
//        obj2.put("created_at",new Timestamp(System.currentTimeMillis()));
//        obj2.put("updated_at",new Timestamp(System.currentTimeMillis()));
//        obj2.put("blocked_at",new Timestamp(System.currentTimeMillis()));
//        obj2.put("deleted_at",new Timestamp(System.currentTimeMillis()));
//
//        String id1 = ArangoInterfaceMethods.insertPost(obj2,loggedInUserId);
//
//        tagUserInPost(""+ loggedInUserId, ""+id1);
//

        if(isAuthorizedToView("posts", loggedInUserId, ownerId) || loggedInUserId.equals(ownerId)) {
            ArrayList<String> postIds = ArangoInterfaceMethods.getAllTaggedPosts(loggedInUserId);
            JSONArray posts = ArangoInterfaceMethods.getPosts(postIds);
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            response.put("posts", posts);
            response.put("error", "null");
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

//        JSONObject obj2= new JSONObject();
//        obj2.put("user_id",loggedInUserId);
//        obj2.put("caption","Taken By Mohamed ABouzeid");
//        obj2.put("media", new ArrayList<String>());
//        obj2.put("likes", new ArrayList<String>());
//        obj2.put("tags",new ArrayList<String>());
//        obj2.put("location","{ name: C1, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
//        obj2.put("comments", new ArrayList<String>());
//        obj2.put("created_at",new Timestamp(System.currentTimeMillis()));
//        obj2.put("updated_at",new Timestamp(System.currentTimeMillis()));
//        obj2.put("blocked_at",new Timestamp(System.currentTimeMillis()));
//        obj2.put("deleted_at",new Timestamp(System.currentTimeMillis()));
//
//        String id1 = ArangoInterfaceMethods.insertPost(obj2,loggedInUserId);
//        makeHashtagNode(""+paramsObject.getString("name"));
//
//        tagPostInHashtag(""+id1, ""+paramsObject.getString("name"));

        ArrayList<String> postIds= ArangoInterfaceMethods.getAllPostsTaggedInHashtag(""+paramsObject.getString("name"));
        JSONArray posts= ArangoInterfaceMethods.getPosts(postIds);

        JSONObject response = new JSONObject();
        response.put("method",methodName);
        response.put("posts",posts);
        return response;
    }

}
