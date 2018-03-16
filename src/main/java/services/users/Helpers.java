package services.users;

import org.json.JSONObject;
import persistence.sql.users.User;

public class Helpers {
    private static User mapJSONToUser(JSONObject json) {
        // TODO
        return new User.Builder().build();
    }

    private static JSONObject mapUserToJSON(User user) {
        // TODO
        return new JSONObject();
    }

    private static JSONObject constructOKResponse() {
        // TODO
        return new JSONObject();
    }

    private static JSONObject constructErrorResponse() {
        // TODO
        return new JSONObject();
    }

    public static User nullifyCredentials(User user) {
        // TODO
        return new User.Builder().build();
    }
}
