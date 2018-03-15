package services.posts;


import org.json.JSONObject;
import shared.Settings;

public class Controller extends shared.mq_server.Controller {
    private Settings settings;

    public Controller(Settings settings) {
        super();
        this.settings = settings;
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        String methodName = jsonObject.getString("method");
        JSONObject paramsObject = jsonObject.getJSONObject("params");
        Posts posts = new Posts(settings);
        Comments comments = new Comments(settings);
        switch (methodName) {
            case "getPosts":
                return posts.getPosts(paramsObject, userId, methodName);
            case "getPost":
                return posts.getPost(paramsObject, userId, methodName);
            case "createPost":
                return posts.createPost(paramsObject, userId, methodName);
            case "getTaggedPosts":
                return posts.getTaggedPosts(paramsObject, userId, methodName);
            case "deletePost":
                return posts.deletePost(paramsObject, userId, methodName);
            case "createPostLike":
                return posts.createPostLike(paramsObject, userId, methodName);
            case "createComment":
                return comments.createComment(paramsObject, userId, methodName);
            case "getComments":
                return comments.getCommentsOnPost(paramsObject, userId, methodName);
            case "createCommentReply":
                return comments.createCommentReply(paramsObject, userId, methodName);
            case "getPostLikers":
                return posts.getPostLikers(paramsObject, userId, methodName);
        }

        JSONObject newJsonObj = new JSONObject();
        newJsonObj.put("application", "feed/posts");
        return newJsonObj;
    }


}
