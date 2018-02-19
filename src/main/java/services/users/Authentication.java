package services.users;

import org.json.JSONObject;
import persistence.sql.users.Main;
import persistence.sql.users.User;

public class Authentication {

    public static JSONObject SignIn(JSONObject paramsoject){
        String name=paramsoject.getString("username");
        String password=paramsoject.getString("passwordHash");
        User user = Main.getUserById(name);
        if(user.getPasswordHash().equals(password)){
            JSONObject response = new JSONObject();
            JSONObject item = new JSONObject();
            item.put("sessionId", 12);
            response.put("response", item);
            return response;
        }else
            return new JSONObject().put("error", "null");
    }
}
