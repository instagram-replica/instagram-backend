package services.users;

import auth.JWT;
import auth.JWTPayload;
import exceptions.CustomException;
import exceptions.JSONException;
import json.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.GraphMethods;
import persistence.sql.Database;
import persistence.sql.users.User;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Controller extends shared.mq_server.Controller {

    public static Properties props;

    public Controller() throws IOException{
        super();
        Database.openConnection();
    }

    @Override
    public JSONObject execute(JSONObject payload, String viewerId) {
        String method;
        JSONObject params;
        JSONObject response;

        try {
            method = JSONParser.getString("method", payload);
            params = JSONParser.getJSONObject("params", payload);
        } catch (JSONException e) {
            e.printStackTrace();
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
                response = Controller.handleUnfollowUser(params, viewerId);
                break;
            case "blockUser":
                response = Controller.handleBlockUser(params, viewerId);
                break;
            case "unblockUser":
                response = Controller.handleUnblockUser(params, viewerId);
                break;
            case "reportUser":
                response = Controller.handleReportUser(params, viewerId);
                break;
            default:
                response = Helpers.constructErrorResponse();
        }

        return response;
    }

    public static JSONObject handleFollowUser(JSONObject params, String userId) {
        boolean followed = GraphMethods.followUser(userId, params.getString("userId"));
        JSONObject jsonForActivities = new JSONObject();
        jsonForActivities.put("method", "createFollow");
        jsonForActivities.put("params", params);
        JSONObject response = new JSONObject();
        try {
            shared.mq_server.Controller.send("users", "activities", jsonForActivities, userId);
        } catch (IOException e) {
            response.put("notification", "exception occurred");
        } catch (InterruptedException e) {
            response.put("notification", "exception occurred");
        }
        return response.put("followed", followed);
    }

    public static JSONObject handleUnfollowUser(JSONObject params, String userId) {
        boolean unfollowed = GraphMethods.unFollowUser(userId, params.getString("userId"));
        return new JSONObject().put("unfollowed", unfollowed);
    }

    public static JSONObject handleBlockUser(JSONObject params, String userId) {
        boolean blocked = GraphMethods.blockUser(userId, params.getString("userId"));
        return new JSONObject().put("blocked", blocked);
    }

    public static JSONObject handleUnblockUser(JSONObject params, String userId) {
        boolean unblocked = GraphMethods.unblockUser(userId, params.getString("userId"));
        return new JSONObject().put("unblocked", unblocked);
    }

    public static JSONObject handleReportUser(JSONObject params, String userId) {
        boolean reported = GraphMethods.reportUser(userId, params.getString("userId"));
        return new JSONObject().put("reported", reported);
    }
    public static JSONObject handleSignup(JSONObject params) {
        try {
            User user = Logic.signup(Helpers.mapJSONToUser(params));
            String token = JWT.signJWT(new JWTPayload.Builder().userId(user.id).build());

            return Helpers.constructOKResponse(
                    new JSONObject().put("user", Helpers.mapUserToJSON(user)).put("token", token)
            );
        } catch (CustomException e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse();
        }
    }

    public static JSONObject handleLogin(JSONObject params) {
        try {
            User user = Logic.login(
                    JSONParser.getString("email", params),
                    JSONParser.getString("password", params)
            );

            String token = JWT.signJWT(new JWTPayload.Builder().userId(user.id).build());

            return Helpers.constructOKResponse(
                    new JSONObject().put("user", Helpers.mapUserToJSON(user)).put("token", token)
            );
        } catch (CustomException e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse();
        }
    }

    public static JSONObject handleGetUser(JSONObject params) {
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

    public static JSONObject handleUpdateUser(JSONObject params) {
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

    public static JSONObject handleSearchUsers(JSONObject params) {
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

    public static JSONObject handleGetUsersByIds(JSONObject params) {
        try {
            String[] ids = Helpers
                    .convertJSONArrayToList(JSONParser.getJSONArray("ids", params))
                    .stream()
                    .toArray(String[]::new);

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

    public static JSONObject handleGetUsersIdsByUsernames(JSONObject params) {
        try {
            String[] usernames = Helpers
                    .convertJSONArrayToList(JSONParser.getJSONArray("usernames", params))
                    .stream()
                    .toArray(String[]::new);

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

    public static JSONObject handleIsAuthorizedToView(JSONObject params) {
        try {
            String viewerId = JSONParser.getString("viewerId", params);
            String viewedId = JSONParser.getString("viewedId", params);

            boolean isAuthorizedToView = Logic.isAuthorizedToView(viewerId, viewedId);

            return Helpers.constructOKResponse(
                    new JSONObject().put("isAuthorizedToView", isAuthorizedToView)
            );
        } catch (CustomException e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse();
        }
    }
}
