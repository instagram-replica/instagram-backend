package services.users;

import org.json.JSONObject;

public class Controller extends shared.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject newJsonObj = new JSONObject();

        String methodName = jsonObject.getString("method");
        JSONObject paramsObject = jsonObject.getJSONObject("params");
        switch (methodName){
            case "signUp": return Authentication.SignUp(paramsObject, userId);
            case "createFollow": return UserActions.CreateFollow(paramsObject, userId);
            case "createUnfollow": return UserActions.CreateUnfollow(paramsObject, userId);
        }

        newJsonObj.put("application", "users");
        return newJsonObj;
    }
}
