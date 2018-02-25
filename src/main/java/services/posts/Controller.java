package services.posts;

import org.json.JSONObject;

public class Controller extends shared.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        String methodName = jsonObject.getString("method");
        JSONObject paramsObject = jsonObject.getJSONObject("params");
        switch (methodName){
            case "getPosts": return Team2.getPosts(paramsObject, userId, methodName);
            case "getPost": return Team2.getPost(paramsObject, userId, methodName);
            case "createPost": return Team2.createPost(paramsObject, userId, methodName);
           // case "getTaggedPosts": return Team2.getTaggedPosts(paramsObject, userId);
            case "deletePost": return Team2.deletePost(paramsObject, userId, methodName);
            case "createPostLike": return Team2.createPostLike(paramsObject, userId, methodName);
        }
        System.out.println(methodName);
        System.out.println(paramsObject.toString());
        JSONObject newJsonObj = new JSONObject();
        newJsonObj.put("application", "feed/posts");
        return newJsonObj;
    }


}
