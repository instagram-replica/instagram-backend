package services.posts;


import org.json.JSONObject;
import shared.Settings;

public class Controller extends shared.mq_server.Controller {
    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        String methodName = jsonObject.getString("method");
        JSONObject paramsObject = jsonObject.getJSONObject("params");
        switch (methodName) {
            case "getPosts":
                return Posts.getPosts(paramsObject, userId, methodName);
            case "getPost":
                return Posts.getPost(paramsObject, userId, methodName);
            case "createPost":
                return Posts.createPost(paramsObject, userId, methodName);
            case "getTaggedPosts":
                return Posts.getTaggedPosts(paramsObject, userId, methodName);
            case "deletePost":
                return Posts.deletePost(paramsObject, userId, methodName);
            case "createPostLike":
                return Posts.createPostLike(paramsObject, userId, methodName);
//            case "deletePostLike":
//                return Posts.deletePostLike(paramsObject,userId,methodName);
            case "createComment":
                return Comments.createComment(paramsObject, userId, methodName);
            case "getComments":
                return Comments.getCommentsOnPost(paramsObject, userId, methodName);
            case "createCommentReply":
                return Comments.createCommentReply(paramsObject, userId, methodName);
            case "getPostLikers":
                return Posts.getPostLikers(paramsObject, userId, methodName);
            case "updatePost":
                return Posts.updatePost(paramsObject,userId,methodName);
        }

        JSONObject newJsonObj = new JSONObject();
        newJsonObj.put("application", "feed/posts");
        return newJsonObj;
    }


}
