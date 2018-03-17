package services.users;

import auth.AuthenticationException;
import auth.JWT;
import auth.JWTPayload;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.sql.users.DatabaseException;
import persistence.sql.users.User;
import services.users.validation.ValidationException;

import java.io.IOException;
import java.util.List;

import static persistence.sql.Main.closeConnection;
import static persistence.sql.Main.openConnection;

public class Controller extends shared.MQServer.Controller {
    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject payload, String viewerId) throws Exception {
        openConnection();

        // TODO: Handle inability to find method or params
        String method = payload.getString("method");
        JSONObject params = payload.getJSONObject("params");

        JSONObject response;

        switch (method) {
            case "signup":
                response = handleSignup(params);
                break;
            case "login":
                response = handleLogin(params);
                break;
            case "getProfile":
                response = handleGetProfile(params);
                break;
            case "updateProfile":
                response = handleUpdateProfile(params);
                break;
            case "searchUsers":
                response = new JSONObject();
                break;
            case "getUsersByIds":
                response = handleGetUsersByIds(params);
                break;
            case "getUsersIdsByUsernames":
                response = handleGetUsersIdsByUsernames(params);
                break;
            case "followUser":
                // TODO: Insert follow edge between nodes in ArangoDB graph database
                response = new JSONObject();
                break;
            case "unfollowUser":
                // TODO: Remove follow edge between nodes in ArangoDB graph database
                response = new JSONObject();
                break;
            case "blockUser":
                // TODO: Insert block edge between nodes in ArangoDB graph database
                response = new JSONObject();
                break;
            case "unblockUser":
                // TODO: Remove block edge between nodes in ArangoDB graph database
                response = new JSONObject();
                break;
            case "reportUser":
                // TODO
                response = new JSONObject();
                break;
            case "isUserAuthorizedToView":
                // TODO
                response = new JSONObject();
                break;
            default:
                response = new JSONObject();
        }

        closeConnection();
        return response;
    }

    private static JSONObject handleSignup(JSONObject params)
            throws DatabaseException, ValidationException, IOException {
        // TODO: Handle errors thrown

        User user = Logic.signup(Helpers.mapJSONToUser(params));

        String token = JWT.signJWT(
                new JWTPayload.Builder()
                        .userId(user.id)
                        .build()
        );

        return Helpers.constructOKResponse(
                new JSONObject()
                        .put("user", Helpers.mapUserToJSON(user))
                        .put("token", token)
        );
    }

    private static JSONObject handleLogin(JSONObject params)
            throws ValidationException, AuthenticationException, IOException {
        // TODO: Handle errors thrown

        User user = Logic.login(
                params.getString("email"),
                params.getString("password")
        );

        String token = JWT.signJWT(
                new JWTPayload.Builder()
                        .userId(user.id)
                        .build()
        );

        return Helpers.constructOKResponse(
                new JSONObject()
                        .put("user", Helpers.mapUserToJSON(user))
                        .put("token", token)
        );
    }

    private static JSONObject handleGetProfile(JSONObject params) throws ValidationException {
        // TODO: Handle errors thrown

        User user = Logic.getProfile(params.getString("userId"));
        return Helpers.constructOKResponse(Helpers.mapUserToJSON(user));
    }

    private static JSONObject handleUpdateProfile(JSONObject params)
            throws DatabaseException, ValidationException {
        // TODO: Handle errors thrown

        User user = Logic.updateProfile(Helpers.mapJSONToUser(params));
        return Helpers.constructOKResponse(Helpers.mapUserToJSON(user));
    }

    private static JSONObject handleGetUsersByIds(JSONObject params) {
        // TODO: Handle errors thrown

        String[] ids = Helpers.convertJSONArrayToList(
                params.getJSONArray("ids")
        ).stream().toArray(String[]::new);

        List<User> users = Logic.getUsersByIds(ids);
        JSONArray usersJSON = Helpers.convertUsersListToJSONArray(users);

        return Helpers.constructOKResponse(usersJSON);
    }

    private static JSONObject handleGetUsersIdsByUsernames(JSONObject params) {
        // TODO: Handle errors thrown

        String[] usernames = Helpers.convertJSONArrayToList(
                params.getJSONArray("usernames")
        ).stream().toArray(String[]::new);

        List<String> usersIds = Logic.getUsersIdsByUsernames(usernames);
        JSONArray usersJSON = Helpers.convertStringsListToJSONArray(usersIds);

        return Helpers.constructOKResponse(usersJSON);
    }
}
