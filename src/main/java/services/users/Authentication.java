package services.users;

import org.json.JSONObject;
import persistence.sql.users.*;

public class Authentication {


    public static JSONObject SignUp(JSONObject params, String userId){
        User newUser = new User();
        newUser.setUsername(params.getString("userName"));
        newUser.setFullName(params.getString("fullName"));
        newUser.setPasswordHash(params.getString("passwordHash"));
        newUser.setEmail(params.getString("email"));
        newUser.setPhoneNumber(params.getString("phone"));

        JSONObject response = new JSONObject();
        response.put("sessionId", 12);
        return response;
    }


}
