package services.chats.Actions;

import exceptions.CustomException;
import org.json.JSONObject;
import persistence.nosql.ThreadMethods;
import utilities.Main;

import java.sql.Timestamp;

public class CreateMessage implements Action {
    public static JSONObject execute(JSONObject jsonObject, String userId) throws CustomException {
        String threadId = jsonObject.getString("threadId");

        String messageId = Main.generateUUID();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        JSONObject message = new JSONObject();
        message.put("id", messageId);
        message.put("text", jsonObject.getString("text"));
        message.put("user_id", userId);
        message.put("created_at", time);
        message.put("deleted_at", "null");
        message.put("blocked_at", "null");

        ThreadMethods.insertMessageOnThread(threadId, message);

        JSONObject res = new JSONObject();
        res.put("id", messageId);
        res.put("user_id", userId);
        res.put("text", jsonObject.getString("text"));
        res.put("created_at", time);

        return res;
    }
}
