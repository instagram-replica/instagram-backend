package services.users;

import auth.JWT;
import auth.JWTPayload;
import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import exceptions.DatabaseException;
import persistence.sql.users.User;
import exceptions.ValidationException;

import java.util.List;

import static persistence.sql.Main.closeConnection;
import static persistence.sql.Main.openConnection;

public class Controller extends shared.mq_server.Controller {
    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject payload, String viewerId) throws Exception {
        // TODO: Handle failure to connect to JDBC URL
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
                response = handleSearchUsers(params);
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

    private static JSONObject handleSignup(JSONObject params) {
        try {
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
        } catch (CustomException e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse();
        }
    }

    private static JSONObject handleLogin(JSONObject params) {
        try {
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
        } catch (CustomException e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse();
        }
    }

    private static JSONObject handleGetProfile(JSONObject params) {
        try {
            User user = Logic.getProfile(params.getString("id"));
            return Helpers.constructOKResponse(Helpers.mapUserToJSON(user));
        } catch (CustomException e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse();
        }
    }

    private static JSONObject handleUpdateProfile(JSONObject params)
            throws DatabaseException, ValidationException {
        // TODO: Handle errors thrown

        User user = Logic.updateProfile(Helpers.mapJSONToUser(params));
        return Helpers.constructOKResponse(Helpers.mapUserToJSON(user));
    }

    // TODO: Generalize search criteria
    private static JSONObject handleSearchUsers(JSONObject params) {
        // TODO: Handle errors thrown

        List<User> users = Logic.searchUsersByFullName(
                params.getString("fullName"),
                params.getInt("offset"),
                params.getInt("limit")
        );

        JSONArray usersJSON = Helpers.convertUsersListToJSONArray(users);
        return Helpers.constructOKResponse(usersJSON);
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
