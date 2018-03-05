package services.users;

import org.json.JSONObject;
import persistence.sql.users.Gender;
import persistence.sql.users.Main;
import persistence.sql.users.User;

import java.sql.Date;
import java.time.Instant;

import static shared.Helpers.createJSONError;

public class UserActions {
    public static JSONObject CreateFollow(JSONObject paramsObject, String loggedInUserId) {
        //TODO: Create activitiy(notification) for the user requested user
        //TODO: Migrate this to use NOSQL
        JSONObject jObject = new JSONObject();
        String toBeFollowedUserId = paramsObject.getString("userId");
        boolean followDone = Main.createFollow(loggedInUserId, toBeFollowedUserId);
        JSONObject inner = new JSONObject();
        if (followDone) {
            inner.put("success", "true");
            inner.put("error", "null");
        } else {
            inner.put("success", "false");
            //TODO: Better error handling
            inner.put("error", "0");
        }
        jObject.put("response", inner);

        return jObject;
    }

    public static JSONObject CreateUnfollow(JSONObject paramsObject, String loggedInUserId) {
        JSONObject jObject = new JSONObject();
        String toBeUnfollowedUserId = paramsObject.getString("userId");
        boolean unfollowDone = Main.deleteFollow(loggedInUserId, toBeUnfollowedUserId);

        JSONObject inner = new JSONObject();
        if (unfollowDone) {
            inner.put("success", "true");
            inner.put("error", "null");
        } else {
            inner.put("success", "false");
            inner.put("error", "0");
        }
        jObject.put("response", inner);

        return jObject;
    }

    public static JSONObject DeleteUser(JSONObject paramsObject, String loggedInUserId)

    {
        JSONObject jObject = new JSONObject();
        JSONObject inner = new JSONObject();
        try {
            boolean deleted = Main.deleteUser(loggedInUserId);
            inner.put("id", loggedInUserId);
            jObject.put("user", inner);
            jObject.put("error", "null");
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }
        return jObject;
    }

    public static JSONObject UpdateProfile(JSONObject paramsObject, String loggedInUserId) {
        JSONObject jObject = new JSONObject();
        JSONObject inner = new JSONObject();
        //TODO: @MAGED Inject image url before this runs
        String blob = paramsObject.getString("blob");
        String name = paramsObject.getString("name");
        String username = paramsObject.getString("username");
        String website = paramsObject.getString("website");
        String bio = paramsObject.getString("bio");
        String email = paramsObject.getString("email");
        String phone = paramsObject.getString("phone");
        String gender = paramsObject.getString("gender");

        User user = null; //or session id
        try {
            user = Main.getUserById(loggedInUserId);
            user.setFullName(name);
            user.setUsername(username);
            user.setWebsiteUrl(website);
            user.setBio(bio);
            user.setEmail(email);
            user.setPhoneNumber(phone);
            user.setGender(gender);
            jObject.put("data", paramsObject);
            jObject.put("error", "null");
            Main.updateUser(loggedInUserId, user);
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }

        return jObject;
    }

    public static JSONObject CreateBlockUser(JSONObject paramsObject, String loggedInUserId) {
        //TODO: Test this via postman
        JSONObject jObject = new JSONObject();
        String userIdToBeBlocked = paramsObject.getString("userId");

        // insert into table user_blocks as blocker_id: loggedInUserId, as blocked_id: userIdToBeBlocked
        try {
            Main.blockUser(loggedInUserId, userIdToBeBlocked);
            jObject.put("success", true);
            jObject.put("error", "null");
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }

        return jObject;
    }

    public static JSONObject CreateUserReport(JSONObject paramsObject, String loggedInUserId) {
        //TODO: Test this via postman
        JSONObject jsonObject = new JSONObject();
        String userIdToBeReported = paramsObject.getString("userId");

        try {
            Main.reportUser(loggedInUserId, userIdToBeReported);
            jsonObject.put("success", true);
            jsonObject.put("error", "null");
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }

        return jsonObject;
    }
//    public static JSONObject CreateUserDeactivate(JSONObject paramsObject, String loggedInUserId)
//    {
//        JSONObject jsonObject = new JSONObject();
//        boolean deactivated = Main.deactivateAccount(loggedInUserId);
//        jsonObject.put("success",deactivated);
//        if(deactivated)
//            jsonObject.put("error","null");
//        else jsonObject.put("error","cannot deactivate account");
//        return jsonObject;
//    }

}
