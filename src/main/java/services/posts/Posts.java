package services.posts;

import com.arangodb.ArangoDBException;
import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.PostsCache;
import persistence.nosql.GraphMethods;
import persistence.nosql.PostMethods;

import java.io.IOException;
import java.util.ArrayList;


import static persistence.nosql.FeedMethods.getDiscoveryFeed;
import static persistence.nosql.GraphMethods.isHashtagNode;
import static persistence.nosql.GraphMethods.makeHashtagNode;
import static persistence.nosql.GraphMethods.tagUserInPost;
import static persistence.nosql.PostMethods.unlikePost;
import static shared.Helpers.createJSONError;
import static shared.Helpers.getUsersByIds;
import static shared.Helpers.isAuthorizedToView;

public class Posts {

    public static JSONObject updatePost(JSONObject paramsObject, String loggedInUserId, String methodName) throws CustomException {
        String postId = paramsObject.getString("postId");

        JSONObject post = null;
        JSONObject updatedPost = null;

        post = PostMethods.getPost(postId);
        String ownerId = post.getString("user_id");
        if (loggedInUserId.equals(ownerId)) {
            PostMethods.updatePost(postId, paramsObject);
            updatedPost = PostMethods.getPost(postId);
            JSONObject response = new JSONObject();

            //Replacing likes array with no of likes instead
//                JSONArray likes = updatedPost.getJSONArray("likes");
//                int noOfLikes= likes.length();
//                updatedPost.put("likes",noOfLikes);

            JSONObject postResponse = new JSONObject();
            postResponse.put("post", updatedPost);

            response.put("method", methodName);
            response.put("postId", postId);
            response.put("response", postResponse);


            return response;
        }
        throw new CustomException("Not authorized to update post");

    }

    //TODO: Returns list of users (actual users not ids) who liked a post
    public static JSONObject getPostLikers(JSONObject paramsObject, String loggedInUserId, String methodName) throws CustomException, IOException, InterruptedException {
        String postId = paramsObject.getString("postId");
        JSONObject post = null;
        post = PostMethods.getPost(postId);

        String ownerId = post.getString("user_id");
        JSONArray userIds = post.getJSONArray("likes");
        if (isAuthorizedToView(loggedInUserId, "posts", loggedInUserId, ownerId) && userIds.length() != 0) {
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            response.put("users", getUsersByIds("posts", userIds, loggedInUserId));
            return response;
        }
        throw new CustomException("Not authorized to view");
    }

    public static JSONObject getPost(JSONObject paramsObject, String loggedInUserId, String methodName) throws Exception {
        String postId = paramsObject.getString("postId");
        JSONObject post = null;
        post = PostsCache.getPostFromCache(postId);
        if (post == null) {
            post = PostMethods.getPost(postId);
            PostsCache.insertPostIntoCache(post, postId);
        }
        JSONArray likes = post.getJSONArray("likes");
        int noOfLikes = likes.length();
        JSONObject response = new JSONObject();
        post.put("likes", noOfLikes);

        response.put("method", methodName);
        response.put("post", post);

        String ownerId = post.getString("user_id");
        System.out.println(loggedInUserId + " :LOGGEDIN");
        System.out.println(ownerId + " :OWNER");
        if (isAuthorizedToView(loggedInUserId, "posts", loggedInUserId, ownerId)) {
            return response;
        }
        throw new CustomException("Not authorized to view");

    }

    public static JSONObject getPosts(JSONObject paramsObject, String loggedInUserId, String methodName) throws ArangoDBException, CustomException, IOException, InterruptedException {
        //TODO: Make use of the pagination params (do not spend much time on this)
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        String ownerId = paramsObject.getString("userId");

        if (isAuthorizedToView(loggedInUserId, "posts", loggedInUserId, ownerId)) {
            //@TODO: Check if the user exists
            JSONArray posts = PostsCache.getPostsFromCache(ownerId, pageIndex, pageSize);
            if (posts == null) {
                posts = PostMethods.getPosts(ownerId);
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
            response.put("method", methodName);
            response.put("posts", posts);
            return response;
        }
        throw new CustomException("Not authorized to view");
    }

    public static JSONObject deletePost(JSONObject paramsObject, String loggedInUserId, String methodName) throws Exception {
        String postId = paramsObject.getString("postId");
        JSONObject postToDelete = PostMethods.getPost(postId);
        String ownerId = postToDelete.getString("user_id");
        if (loggedInUserId.equals(ownerId)) {
            PostMethods.deletePost(postId);
            JSONObject post = new JSONObject();
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            post.put("id", postId);
            response.put("post", post);
            return response;
        }
        throw new CustomException("Not authorized to delete");
    }

    public static JSONObject createPost(JSONObject paramsObject, String loggedInUserId, String methodName) throws Exception {
        String postId = null;

        //TODO: Parse tags in media and @ACTIVITIES_TEAM create activities for their users

        //    if(paramsObject.getJSONObject("post").get("user_id").toString().equals(loggedInUserId))
        postId = PostMethods.insertPost(paramsObject.getJSONObject("post"), loggedInUserId);
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

    public static JSONObject createPostLike(JSONObject paramsObject, String loggedInUserId, String methodName) throws Exception {
        //TODO: Create activity for the post owner @ACTIVITIES_TEAM, except if he is a retard who likes his own image
        String postId = paramsObject.getString("postId");

        JSONObject post = PostMethods.getPost(postId);
        JSONArray likes = post.getJSONArray("likes");
        for (int i = 0; i < likes.length(); i++) {
            if (likes.get(i).equals(loggedInUserId))
                throw new CustomException("You already liked this post");
        }
        PostMethods.likePost(postId, loggedInUserId);
        JSONObject res = new JSONObject();
        JSONObject response = new JSONObject();
        response.put("method", methodName);
        res.put("postID", postId);
        response.put("response", res);
        return response;
    }

    public static JSONObject deletePostLike(JSONObject paramsObject, String loggedInUserId, String methodName) {
        String postId = paramsObject.getString("postId");
        try {
            JSONObject post = PostMethods.getPost(postId);
            JSONArray likes = post.getJSONArray("likes");
            for (int i = 0; i < likes.length(); i++) {
                if (likes.get(i).equals(loggedInUserId)) {
                    unlikePost(postId, loggedInUserId);
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

    public static JSONObject createPostTag(JSONObject paramsObject, String loggedInUserId, String methodName) throws Exception {
        String userId = paramsObject.getString("userId");
        String postId = paramsObject.getString("postId");
        JSONObject post = PostMethods.getPost(postId);
        String postOwner = post.getString("user_id");
        if (isAuthorizedToView(loggedInUserId, "posts", loggedInUserId, userId) && loggedInUserId.equals(postOwner)) {
            tagUserInPost("" + userId, "" + postId);
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            response.put("taggedId", userId);
            return response;

        }
        throw new CustomException("Not authorized to tag");
    }

    public static JSONObject getTaggedPosts(JSONObject paramsObject, String loggedInUserId, String methodName) throws Exception {
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        String userId = paramsObject.getString("userId");

        if (isAuthorizedToView(loggedInUserId, "posts", loggedInUserId, userId)) {
            ArrayList<String> postIds = GraphMethods.getAllTaggedPosts(userId);
            JSONArray posts = PostMethods.getPosts(postIds);
            JSONObject response = new JSONObject();
            response.put("method", methodName);
            response.put("posts", posts);
            return response;
        } else
            throw new CustomException("Not authorized to view");

    }

    //@TODO: Ping geddo
    public static JSONObject getDiscoverFeed(JSONObject paramsObject, String loggedInUserId, String methodName) {
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");

        ArrayList<JSONObject> feed = getDiscoveryFeed("" + loggedInUserId, pageSize, pageIndex);
        JSONObject response = new JSONObject();
        response.put("method", methodName);
        response.put("posts", feed);

        return response;
    }

    //@TODO: Ping geddo
    public static JSONObject createPostHashtags(JSONObject paramsObject, String loggedInUserId, String methodName) throws Exception {

        String postId = paramsObject.getString("postId");
        //String hashtag = paramsObject.getString("name");
        JSONArray hashtags = paramsObject.getJSONArray("name");
        JSONObject post = PostMethods.getPost(postId);
        String ownerId = post.getString("user_id");

        if (loggedInUserId.equals(ownerId)) {
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

    public static JSONObject getHashtagPosts(JSONObject paramsObject, String loggedInUserId, String methodName) {

        ArrayList<String> postIds = GraphMethods.getAllPostsTaggedInHashtag("" + paramsObject.getString("name"));
        JSONArray posts = PostMethods.getPosts(loggedInUserId);

        JSONObject response = new JSONObject();
        response.put("method", methodName);
        response.put("posts", posts);
        return response;
    }

}