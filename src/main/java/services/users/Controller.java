package services.users;

import org.json.JSONObject;
import shared.MQServer.Queue;

import java.io.IOException;

import static persistence.sql.Main.closeConnection;
import static persistence.sql.Main.openConnection;


public class Controller extends shared.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) throws IOException {
        //TODO: @MAGDY Find a better way of opening and closing db connection
        openConnection();

        String methodName = jsonObject.getString("method");
        JSONObject paramsObject = jsonObject.getJSONObject("params");
        JSONObject resJSON = new JSONObject();
        switch (methodName) {
            case "signUp":
                resJSON = Authentication.SignUp(paramsObject);
                break;
            case "getUserInfo":
                resJSON = Authentication.GetUserInfo(paramsObject, userId);
                break;
            case "createFollow":
                resJSON = UserActions.CreateFollow(paramsObject, userId);
                break;
            case "udpateProfile":
                resJSON = UserActions.UpdateProfile(paramsObject, userId);
                break;
            case "createUnfollow":
                resJSON = UserActions.CreateUnfollow(paramsObject, userId);
                break;
            case "deleteUser":
                resJSON = UserActions.DeleteUser(paramsObject, userId);
                break;
            case "authorizedToView":
                resJSON = Authentication.authorizedToView(paramsObject.getString("viewerId"), paramsObject.getString("toBeViewedId"));
        }
        closeConnection();
        return resJSON;
    }
}
