package services.users;

import org.json.JSONObject;
import shared.MQServer.Queue;

import java.io.IOException;

import static persistence.sql.Main.openConnection;

public class Controller extends shared.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) throws IOException {
        String methodName = jsonObject.getString("method");
        JSONObject paramsObject = jsonObject.getJSONObject("params");
        switch (methodName) {
            case "signUp":
                return Authentication.SignUp(paramsObject, userId);
            case "getUserInfo":
                return Authentication.GetUserInfo(paramsObject, userId);
            case "createFollow":
                return UserActions.CreateFollow(paramsObject, userId);
            case "createUnfollow":
                return UserActions.CreateUnfollow(paramsObject, userId);
            case "deleteUser":
                return UserActions.DeleteUser(paramsObject, userId);
        }
        return new JSONObject();
    }
}
