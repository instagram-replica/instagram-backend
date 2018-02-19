package services.users;

import org.json.JSONObject;
import persistence.sql.users.Main;
import persistence.sql.users.User;

import java.sql.Date;

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
    public static JSONObject DeleteUser(JSONObject paramsObject)
    {
        JSONObject jObject = new JSONObject();
        JSONObject inner = new JSONObject();
        String userId = paramsObject.getString("userId");

        //@stub
        User deletedUser  = Main.deleteUser(userId);
        inner.put("id",userId);
        jObject.put("user", inner);
        if(deletedUser.getDeletedAt()!=null)
        {
            jObject.put("error","null");
        }
        else {
            jObject.put("error","user not deleted");
        }
        return jObject;

    }
}
