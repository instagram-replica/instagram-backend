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
        }
        System.out.println(methodName);
        System.out.println(paramsObject.toString());
        JSONObject newJsonObj = new JSONObject();
        newJsonObj.put("application", "feed/posts");
        return newJsonObj;
    }


}
