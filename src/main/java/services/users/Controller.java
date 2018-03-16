package services.users;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.sql.users.User;

import java.util.List;

import static persistence.sql.Main.closeConnection;
import static persistence.sql.Main.openConnection;
import static persistence.sql.users.Main.getUsersByIds;
import static persistence.sql.users.Main.getUsersIdsByUsernames;

public class Controller extends shared.MQServer.Controller {
    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject payload, String viewerId) throws Exception {
        openConnection();

        String method = payload.getString("method");
        JSONObject params = payload.getJSONObject("params");

        JSONObject response;

        switch (method) {
            case "signup":
                response = Authentication.SignUp(params);
                break;
            case "login":
                response = new JSONObject();
                break;
            case "getProfile":
                response = Authentication.GetUserInfo(params, viewerId);
                break;
            case "updateProfile":
                response = UserActions.UpdateProfile(params, viewerId);
                break;
            case "followUser":
                response = UserActions.CreateFollow(params, viewerId);
                break;
            case "unfollowUser":
                response = UserActions.CreateUnfollow(params, viewerId);
                break;
            case "blockUser":
                response = UserActions.CreateBlockUser(params, viewerId);
                break;
            case "unblockUser":
                response = UserActions.DeleteBlockUser(params, viewerId);
                break;
            case "reportUser":
                response = UserActions.CreateUserReport(params, viewerId);
                break;
            case "isUserAuthorizedToView":
                response = Authentication.authorizedToView(params.getString("viewerId"), params.getString("toBeViewedId"));
                break;
            case "getUsersByIds":
                // TODO @maged: Refactor logic into dedicated file
                List<User> users = getUsersByIds(
                        new String[]{} // TODO @magdy: Swap with data from params object
                );
                response = new JSONObject()
                        .put("response", new JSONObject().put("data", new JSONArray(users)));
                break;
            case "getUsersIdsByUsernames":
                // TODO @maged: Refactor logic into dedicated file
                List<String> ids = getUsersIdsByUsernames(
                        new String[]{} // TODO @magdy: Swap with data from params object
                );
                response = new JSONObject()
                        .put("response", new JSONObject().put("data", new JSONArray(ids)));
                break;
            case "searchUsers":
                response = new JSONObject();
                break;
            default:
                response = new JSONObject();
        }

        closeConnection();
        return response;
    }
}
