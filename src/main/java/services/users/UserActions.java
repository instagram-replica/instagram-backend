package services.users;

import org.json.JSONObject;
import persistence.sql.users.Gender;
import persistence.sql.users.Main;
import persistence.sql.users.User;

import java.sql.Date;
import java.time.Instant;

public class UserActions {
    public static JSONObject CreateFollow(JSONObject paramsObject, String loggedInUserId) {
        JSONObject jObject = new JSONObject();
        String toBeFollowedUserId = paramsObject.getString("userId");
        boolean followDone = Main.createFollow(loggedInUserId,toBeFollowedUserId);
        JSONObject inner = new JSONObject();
        if(followDone){
            inner.put("success", "true");
            inner.put("error","null");
        }else{
            inner.put("success", "false");
            inner.put("error","0");
        }
        jObject.put("response", inner);

        return jObject;
    }
    public static JSONObject CreateUnfollow(JSONObject paramsObject, String loggedInUserId) {
        JSONObject jObject = new JSONObject();
        String toBeUnfollowedUserId = paramsObject.getString("userId");
        boolean unfollowDone = Main.deleteFollow(loggedInUserId,toBeUnfollowedUserId);

        JSONObject inner = new JSONObject();
        if(unfollowDone){
            inner.put("success", "true");
            inner.put("error","null");
        }else {
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
        boolean isDeleted  = Main.deleteUser(loggedInUserId);
        inner.put("id",loggedInUserId);
        jObject.put("user", inner);
        if(isDeleted){
            jObject.put("error","null");
        }
        else{
            jObject.put("error","user not deleted");
        }
        return jObject;
    }
    public static JSONObject UpdateProfile(JSONObject paramsObject, String loggedInUserId)
    {
        JSONObject jObject = new JSONObject();
        JSONObject inner = new JSONObject();

        String blob = paramsObject.getString("blob");
        String name = paramsObject.getString("name");
        String username = paramsObject.getString("username");
        String website = paramsObject.getString("website");
        String bio = paramsObject.getString("bio");
        String email = paramsObject.getString("email");
        String phone = paramsObject.getString("phone");
        String gender = paramsObject.getString("gender");

        User user = Main.getUserById(loggedInUserId); //or session id
        user.setFullName(name);
        user.setUsername(username);
        user.setWebsiteUrl(website);
        user.setBio(bio);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setGender(Gender.valueOf(gender));
        jObject.put("data", paramsObject);
        boolean updated = Main.updateUser(loggedInUserId,user);

        //must validate if update was successful
        if(updated){
            jObject.put("error","null");
        }else {
            jObject.put("error","update profile was not successful");
        }

        return jObject;
    }

    public static JSONObject CreateBlockUser(JSONObject paramsObject, String loggedInUserId)
    {
        JSONObject jObject = new JSONObject();
        String userIdToBeBlocked = paramsObject.getString("userId");

       // insert into table user_blocks as blocker_id: loggedInUserId, as blocked_id: userIdToBeBlocked
        boolean blocked = Main.blockUser(loggedInUserId,userIdToBeBlocked);

        jObject.put("success",blocked);
        if(blocked)
            jObject.put("error","null");
        else
        jObject.put("error","cant block user");

        return jObject;
    }
    public static JSONObject CreateUserReport(JSONObject paramsObject, String loggedInUserId)
    {
        JSONObject jsonObject = new JSONObject();
        String userIdToBeReported = paramsObject.getString("userId");

        boolean reported = Main.reportUser(loggedInUserId,userIdToBeReported);

        jsonObject.put("success",reported);
        if(reported)
            jsonObject.put("error","null");
        else
            jsonObject.put("error","cant report user");

        return  jsonObject;
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
