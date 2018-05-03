package services.chats.Actions;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.GraphMethods;
import persistence.nosql.ThreadMethods;
import java.sql.Timestamp;
import java.util.ArrayList;

public class CreateThread implements Action {
    public static JSONObject execute(JSONObject jsonObject, String userId) throws CustomException {
        JSONObject thread = new JSONObject();

        thread.put("creator_id", userId);
        thread.put("name", jsonObject.getString("threadName"));
        thread.put("created_at", new Timestamp(System.currentTimeMillis()));
        thread.put("deleted_at", "null");
        thread.put("blocked_at", "null");
        thread.put("messages", new ArrayList<String>());

        String threadId = ThreadMethods.insertThread(thread);

        //get users and join them
        JSONArray users = jsonObject.getJSONArray("threadUsers");
        for (int i = 0; i < users.length(); i++) {
            GraphMethods.joinThread(users.getJSONObject(i).getString("id"), threadId);
        }
        return new JSONObject().put("threadId", threadId);
    }
}
