package services.users;

import auth.JWT;
import auth.JWTPayload;
import exceptions.CustomException;
import exceptions.JSONException;
import json.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.ArangoInterfaceMethods;
import persistence.sql.users.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static persistence.sql.Main.closeConnection;
import static persistence.sql.Main.openConnection;

public class Controller extends shared.mq_server.Controller {
    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject payload, String viewerId) {
        try {
            Controller.initialize();
        } catch (Exception e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse();
        }

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
                response = Controller.handleIsAuthorizedToView(params);
                break;
            case "followUser":
                response = Controller.handleFollowUser(params, viewerId);
                break;
            case "unfollowUser":
                response = Controller.handleUnFollowUser(params, viewerId);
                break;
            case "blockUser":
                // TODO @ARANGODB
                response = Helpers.constructErrorResponse();
                break;
            case "unblockUser":
                // TODO @ARANGODB
                response = Helpers.constructErrorResponse();
                break;
            case "reportUser":
                // TODO @ARANGODB
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

    private static JSONObject handleIsAuthorizedToView(JSONObject params) {
        try {
            String viewerId = JSONParser.getString("viewerId", params);
            String viewedId = JSONParser.getString("viewedId", params);

            boolean isAuthorizedToView = Logic.isAuthorizedToView(
                    viewerId,
                    viewedId
            );

            return Helpers.constructOKResponse(
                    new JSONObject()
                            .put("isAuthorizedToView", isAuthorizedToView)
            );
        } catch (CustomException e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse();
        }
    }

    private static JSONObject handleUnFollowUser(JSONObject params, String viewer){
        String unfollowedUser = params.getString("userId");
        boolean followDone = ArangoInterfaceMethods.unFollowUser(viewer, unfollowedUser);
        JSONObject data = new JSONObject();
        JSONObject error = new JSONObject();
        data.put("unfollowSuccess", followDone);

        return new JSONObject()
                .put("data", data)
                .put("error", error);
    }
}
    private static JSONObject handleFollowUser(JSONObject params, String viewer){
        String followedUser = params.getString("userId");
        boolean followDone = ArangoInterfaceMethods.followUser(viewer, followedUser);
        JSONObject data = new JSONObject();
        JSONObject error = new JSONObject();
        data.put("followSuccess", followDone);

        JSONObject jsonForActivities = new JSONObject();
        JSONObject paramsForActivities = new JSONObject();
        paramsForActivities.put("userId", followedUser);
        jsonForActivities.put("method", "createFollow");
        jsonForActivities.put("params", paramsForActivities);

        try {
            Controller.send("activities", "users", jsonForActivities, viewer);
        } catch (Exception e) {
            error =  Helpers.constructErrorResponse();
        }
        return new JSONObject()
                .put("data", data)
                .put("error", error);
    }
}
