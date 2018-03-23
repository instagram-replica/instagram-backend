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
import services.posts.PostsActions;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static persistence.sql.Main.closeConnection;
import static persistence.sql.Main.openConnection;
import static utilities.Main.readPropertiesFile;

public class Controller extends shared.mq_server.Controller {

    private Properties props;

    public Controller(){
        super();
        try {
            props = readPropertiesFile("src/main/resources/posts_mapper.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        try {
            Controller.initialize();
        } catch (Exception e) {
            e.printStackTrace();
            return Helpers.constructErrorResponse();
        }

        JSONObject data = new JSONObject();
        JSONObject error = new JSONObject();
        String methodName;
        String methodSignature;
        JSONObject paramsObject;

        try {
            methodName = jsonObject.getString("method");
            methodSignature = props.getProperty(methodName);
            paramsObject = jsonObject.getJSONObject("params");
        } catch (Exception e) {
            e.printStackTrace();
            Controller.teardown();
            return Helpers.constructErrorResponse(e.getMessage());
        }

        try {
            Method method = PostsActions.class.getMethod(methodSignature, JSONObject.class, String.class);
            data = (JSONObject) method.invoke(null,paramsObject, userId);
        }
        catch(org.json.JSONException e){
            e.printStackTrace();
            error.put("description",utilities.Main.stringifyJSONException(e));
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getMessage());
            error.put("description", Helpers.constructErrorResponse());
        }

        JSONObject response = new JSONObject();
        response.put("error",error);
        response.put("data",data);

        Controller.teardown();
        return response;
    }

    private static void initialize() throws IOException {
        openConnection();
    }

    private static void teardown() {
        closeConnection();
    }

    private static JSONObject handleSignup(JSONObject params, String viewerId) {
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

    private static JSONObject handleLogin(JSONObject params, String viewerId) {
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

    private static JSONObject handleGetUser(JSONObject params, String viewerId) {
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

    private static JSONObject handleUpdateUser(JSONObject params, String viewerId) {
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

    private static JSONObject handleSearchUsers(JSONObject params, String viewerId) {
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

    private static JSONObject handleGetUsersByIds(JSONObject params, String viewerId) {
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

    private static JSONObject handleGetUsersIdsByUsernames(JSONObject params, String viewerId) {
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

    private static JSONObject handleIsAuthorizedToView(JSONObject params, String viewer) {
        try {
            String viewerId = JSONParser.getString("viewerId", params);
            String viewedId = JSONParser.getString("viewedId", params);

            boolean isAuthorizedToView = Logic.isAuthorizedToView(
                    viewerId,
                    viewedId
            );

            return Helpers.constructOKResponse(
                    new JSONObject()
                            .put("authorized", isAuthorizedToView)
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
            Controller.send("users", "activities", jsonForActivities, viewer);
        } catch (Exception e) {
            error = Helpers.constructErrorResponse();
        }
        return new JSONObject()
                .put("data", data)
                .put("error", error);
    }

    public static JSONObject handleBlockUser(JSONObject params, String viewerId){
        String blockedUser = params.getString("userId");
        boolean blockedSuccess = ArangoInterfaceMethods.blockUser(viewerId,blockedUser);
        boolean unfollowSuccess =ArangoInterfaceMethods.unFollowUser(blockedUser,viewerId);
        JSONObject data = new JSONObject();
        JSONObject error = new JSONObject();
        data.put("blockSuccess", blockedSuccess&&unfollowSuccess);
        return new JSONObject()
                .put("data", data)
                .put("error", error);

    }

    public static JSONObject handleUnblockUser(JSONObject params, String viewerId){
        String unblockedUser = params.getString("userId");
        boolean unblockedSuccess = ArangoInterfaceMethods.unblockUser(viewerId,unblockedUser);
        JSONObject data = new JSONObject();
        JSONObject error = new JSONObject();
        data.put("unblockSuccess", unblockedSuccess);
        return new JSONObject()
                .put("data", data)
                .put("error", error);

    }

    public static JSONObject handleReportUser(JSONObject params, String viewerId){
        String reportedUsers = params.getString("userId");
        boolean reportedSuccess = ArangoInterfaceMethods.reportUser(viewerId,reportedUsers);
        JSONObject data = new JSONObject();
        JSONObject error = new JSONObject();
        data.put("reportSuccess", reportedSuccess);
        return new JSONObject()
                .put("data", data)
                .put("error", error);

    }

}
