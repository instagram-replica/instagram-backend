package services.posts;

import org.json.JSONObject;

public class Controller extends shared.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
//        System.out.println(jsonObject.toString());
        String methodName = jsonObject.getString("method");
        JSONObject paramsObject = jsonObject.getJSONObject("params");
        switch (methodName){
            case "getPosts": return Team1.getPosts(paramsObject, userId);
            case "getPost": return Team2.getPost(paramsObject, userId);
            case "getTaggedPosts": return Team2.getTaggedPosts(paramsObject, userId);
            case "createPostLike": return Team2.createPostLike(paramsObject, userId, methodName);
            case "createComment" : return Team1.createComment(paramsObject,userId,methodName);
            case "getComments" : return Team1.getCommentsOnPost(paramsObject,userId,methodName);
            case "createCommentReply": return Team1.createCommentReply(paramsObject,userId,methodName);
        }
        System.out.println(methodName);
        System.out.println(paramsObject.toString());
        JSONObject newJsonObj = new JSONObject();
        newJsonObj.put("application", "feed/posts");
        return newJsonObj;
    }


}
