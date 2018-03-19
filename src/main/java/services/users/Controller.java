package services.users;

import auth.JWT;
import auth.JWTPayload;
import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.sql.users.User;

import java.util.List;

import static persistence.sql.Main.closeConnection;
import static persistence.sql.Main.openConnection;

public class Controller extends shared.mq_server.Controller {
    public Controller() {
        super();
    }

    // TODO: Handle all JSON getters failures

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
            case "getUser":
                response = handleGetUser(params);
                break;
            case "updateUser":
                response = handleUpdateUser(params);
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
            case "isUserAuthorizedToView":
                // TODO: Implement logic
                response = Helpers.constructErrorResponse();
                break;
            case "followUser":
                // TODO: Insert follow edge between nodes in ArangoDB graph database
                response = Helpers.constructErrorResponse();
                break;
            case "unfollowUser":
                // TODO: Remove follow edge between nodes in ArangoDB graph database
                response = Helpers.constructErrorResponse();
                break;
            case "blockUser":
                // TODO: Insert block edge between nodes in ArangoDB graph database
                response = Helpers.constructErrorResponse();
                break;
            case "unblockUser":
                // TODO: Remove block edge between nodes in ArangoDB graph database
                response = Helpers.constructErrorResponse();
                break;
            case "reportUser":
                // TODO: Insert report edge between nodes in ArangoDB graph database
                response = Helpers.constructErrorResponse();
                break;
            default:
                response = Helpers.constructErrorResponse();
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

    private static JSONObject handleGetUser(JSONObject params) {
        try {
            User user = Logic.getUser(params.getString("id"));
            return Helpers.constructOKResponse(Helpers.mapUserToJSON(user));
        } catch (CustomException e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse();
        }
    }

    private static JSONObject handleUpdateUser(JSONObject params) {
        // TODO: Verify rightful ownership
        try {
            User user = Logic.updateUser(Helpers.mapJSONToUser(params));
            return Helpers.constructOKResponse(Helpers.mapUserToJSON(user));
        } catch (CustomException e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse();
        }
    }

    private static JSONObject handleSearchUsers(JSONObject params) {
        try {
            List<User> users = Logic.searchUsers(
                    params.getString("term"),
                    params.getInt("offset"),
                    params.getInt("limit")
            );

            JSONArray usersJSON = Helpers.convertUsersListToJSONArray(users);
            return Helpers.constructOKResponse(usersJSON);
        } catch (CustomException e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse();
        }
    }

    private static JSONObject handleGetUsersByIds(JSONObject params) {
        try {
            String[] ids = Helpers.convertJSONArrayToList(
                    params.getJSONArray("ids")
            ).stream().toArray(String[]::new);

            List<User> users = Logic.getUsersByIds(ids);
            JSONArray usersJSON = Helpers.convertUsersListToJSONArray(users);

            return Helpers.constructOKResponse(usersJSON);
        } catch (CustomException e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse();
        }
    }

    private static JSONObject handleGetUsersIdsByUsernames(JSONObject params) {
        try {
            String[] usernames = Helpers.convertJSONArrayToList(
                    params.getJSONArray("usernames")
            ).stream().toArray(String[]::new);

            List<String> usersIds = Logic.getUsersIdsByUsernames(usernames);
            JSONArray usersJSON = Helpers.convertStringsListToJSONArray(usersIds);

            return Helpers.constructOKResponse(usersJSON);
        } catch (CustomException e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse();
        }
    }
}
