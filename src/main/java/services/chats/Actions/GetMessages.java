package services.chats.Actions;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.ChatCache;
import persistence.nosql.ThreadMethods;
import java.util.ArrayList;

public class GetMessages implements Action {
    public static JSONObject execute(JSONObject jsonObject, String userId) throws CustomException {
        JSONObject res = new JSONObject();
        String threadId = jsonObject.getString("threadId");
        JSONObject thread = ChatCache.getThreadFromCache(threadId);
        if(thread==null) {
            thread = ThreadMethods.getThread(threadId);
            ChatCache.insertThreadIntoCache(thread,threadId);
        }
        JSONArray messages = thread.getJSONArray("messages");

        int pageSize = jsonObject.getInt("pageSize");
        int offset = jsonObject.getInt("pageIndex") * pageSize;
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
}
