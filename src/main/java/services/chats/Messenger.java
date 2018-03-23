package services.chats;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.ArangoInterfaceMethods;
import persistence.nosql.GraphMethods;
import persistence.nosql.ThreadMethods;
import utilities.Main;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Messenger {
    public static JSONObject createMessage(JSONObject params, String userId) throws CustomException {
        String threadId = params.getString("threadId");

        String messageId = Main.generateUUID();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        JSONObject message = new JSONObject();
        message.put("id", messageId);
        message.put("text", params.getString("text"));
        message.put("user_id", userId);
        message.put("created_at", time);
        message.put("deleted_at", "null");
        message.put("blocked_at", "null");

        ThreadMethods.insertMessageOnThread(threadId, message);

        JSONObject res = new JSONObject();
        res.put("id", messageId);
        res.put("user_id", userId);
        res.put("text", params.getString("text"));
        res.put("created_at", time);

        return res;
    }

    public static JSONObject createThread(JSONObject paramsObject, String userId) {
        JSONObject thread = new JSONObject();

        thread.put("creator_id", userId);
        thread.put("name", paramsObject.getString("threadName"));
        thread.put("created_at", new Timestamp(System.currentTimeMillis()));
        thread.put("deleted_at", "null");
        thread.put("blocked_at", "null");
        thread.put("messages", new ArrayList<String>());

        String threadId = ThreadMethods.insertThread(thread);

        //get users and join them
        JSONArray users = paramsObject.getJSONArray("threadUsers");
        for (int i = 0; i < users.length(); i++) {
            GraphMethods.joinThread(users.getJSONObject(i).getString("id"), threadId);
        }
        return new JSONObject().put("threadId", threadId);
    }

    public static JSONObject getMessages(JSONObject paramsObject, String userId) throws CustomException {
        JSONObject res = new JSONObject();
        String threadId = paramsObject.getString("threadId");
        JSONObject thread = ThreadMethods.getThread(threadId);
        JSONArray messages = thread.getJSONArray("messages");

        int pageSize = paramsObject.getInt("pageSize");
        int offset = paramsObject.getInt("pageIndex") * pageSize;
        int end = offset + pageSize;
        ArrayList<JSONObject> messagesToReturn = new ArrayList<>();

        int minEnd = end < messages.length() ? end : messages.length();
        for (int i = offset; i < minEnd; i++) {
            messagesToReturn.add(messages.getJSONObject(i));
        }

        JSONArray resultMessages = new JSONArray(messagesToReturn);
        res.put("threadName", thread.getString("name"));
        res.put("threadId", threadId);
        res.put("messages", resultMessages);
        return res;
    }

    public static JSONObject getThreads(JSONObject paramsObject, String userId) throws CustomException {
        ArrayList<String> threadsIds = GraphMethods.getAllThreadsForUser(userId);
        ArrayList<JSONObject> threads = new ArrayList<>();
        for (int i = 0; i < threadsIds.size(); i++) {
            String id = threadsIds.get(i);
            String name = ThreadMethods.getThread(id).getString("name");
            threads.add(new JSONObject().put("threadId", id).put("name", name));
        }
        JSONArray threadsArray = new JSONArray(threads);
        JSONObject result = new JSONObject();
        result.put("threads", threadsArray);
        return result;
    }
}
