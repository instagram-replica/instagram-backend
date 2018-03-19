package services.users;

import auth.JWT;
import auth.JWTPayload;
import exceptions.CustomException;
import exceptions.JSONException;
import json.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.sql.users.User;

import java.io.IOException;
import java.util.List;

import static persistence.sql.Main.closeConnection;
import static persistence.sql.Main.openConnection;

public class Controller extends shared.mq_server.Controller {
    public Controller() {
        super();
    }

    // TODO: Handle all JSON getters failures

    @Override
    public JSONObject execute(JSONObject payload, String viewerId) throws IOException {
        // TODO: Handle failure to connect to JDBC URL
        Controller.initialize();

        String method;
        JSONObject params;
        JSONObject response;

        try {
            method = JSONParser.getString("method", payload);
            params = JSONParser.getJSONObject("params", payload);
        } catch (JSONException e) {
            e.printStackTrace();
            Controller.teardown();
            return Helpers.constructErrorResponse(e.getMessage());
        }

        switch (method) {
            case "signup":
                response = Controller.handleSignup(params);
                break;
            case "login":
                response = Controller.handleLogin(params);
                break;
            case "getUser":
                response = Controller.handleGetUser(params);
                break;
            case "updateUser":
                response = Controller.handleUpdateUser(params);
                break;
            case "searchUsers":
                response = Controller.handleSearchUsers(params);
                break;
            case "getUsersByIds":
                response = Controller.handleGetUsersByIds(params);
                break;
            case "getUsersIdsByUsernames":
                response = Controller.handleGetUsersIdsByUsernames(params);
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

        Controller.teardown();
        return response;
    }

    private static void initialize() throws IOException {
        openConnection();
    }

    private static void teardown() {
        closeConnection();
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
                    JSONParser.getString("email", params),
                    JSONParser.getString("password", params)
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
            User user = Logic.getUser(JSONParser.getString("id", params));
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
                    JSONParser.getString("term", params),
                    JSONParser.getInt("offset", params),
                    JSONParser.getInt("limit", params)
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
                    JSONParser.getJSONArray("ids", params)
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
                    JSONParser.getJSONArray("usernames", params)
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
