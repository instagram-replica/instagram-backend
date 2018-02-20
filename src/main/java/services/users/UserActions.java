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
        JSONObject inner = new JSONObject();
        inner.put("success", "true");
        inner.put("error","null");
        jObject.put("response", inner);

        return jObject;
    }
    public static JSONObject CreateUnfollow(JSONObject paramsObject, String loggedInUserId) {
        JSONObject jObject = new JSONObject();
        String toBeUnfollowedUserId = paramsObject.getString("userId");
        JSONObject inner = new JSONObject();
        inner.put("success", "true");
        inner.put("error","null");
        jObject.put("response", inner);

        return jObject;
    }
    public static JSONObject DeleteUser(JSONObject paramsObject, String loggedInUserId)

    {
        JSONObject jObject = new JSONObject();
        JSONObject inner = new JSONObject();
//        String userId = paramsObject.getString("userId");

        //@stub
        User deletedUser  = Main.deleteUser(loggedInUserId);
        inner.put("id",loggedInUserId);
        jObject.put("user", inner);
        if(deletedUser.getDeletedAt()!=null){
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

        User user = new User();
        user.setFullName(name);
        user.setUsername(username);
        user.setWebsiteUrl(website);
        user.setBio(bio);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setGender(Gender.valueOf(gender));
        jObject.put("data", paramsObject);
        user = Main.updateUser(loggedInUserId,user);
        jObject.put("error","null");
        //must validate if update was successful
//        if(user.getUpdatedAt().before(Date.valueOf()))

//        jObject.put("error",error);
        return jObject;
    }

}
