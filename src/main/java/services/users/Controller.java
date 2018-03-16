package services.users;

import org.json.JSONObject;

import static persistence.sql.Main.closeConnection;
import static persistence.sql.Main.openConnection;

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
                response = new JSONObject();
                break;
            case "login":
                response = new JSONObject();
                break;
            case "getProfile":
                response = new JSONObject();
                break;
            case "updateProfile":
                response = new JSONObject();
                break;
            case "followUser":
                response = new JSONObject();
                break;
            case "unfollowUser":
                response = new JSONObject();
                break;
            case "blockUser":
                response = new JSONObject();
                break;
            case "unblockUser":
                response = new JSONObject();
                break;
            case "reportUser":
                response = new JSONObject();
                break;
            case "isUserAuthorizedToView":
                response = new JSONObject();
                break;
            case "getUsersByIds":
                response = new JSONObject();
                break;
            case "getUsersIdsByUsernames":
                response = new JSONObject();
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
