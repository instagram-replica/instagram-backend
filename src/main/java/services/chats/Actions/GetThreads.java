package services.chats.Actions;

import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.ChatCache;
import persistence.nosql.GraphMethods;
import persistence.nosql.ThreadMethods;

import java.util.ArrayList;

public class GetThreads implements Action {
    public static JSONObject execute(JSONObject jsonObject, String userId) throws CustomException {
        ArrayList<String> threadsIds = GraphMethods.getAllThreadsForUser(userId);
        ArrayList<JSONObject> threads = new ArrayList<>();
        for (int i = 0; i < threadsIds.size(); i++) {
            String id = threadsIds.get(i);
            JSONObject thread = ChatCache.getThreadFromCache(id);
            if (thread == null) {
                thread = ThreadMethods.getThread(id);
                ChatCache.insertThreadIntoCache(thread, id);
            }
            String name = thread.getString("name");
            threads.add(new JSONObject().put("threadId", id).put("name", name));
        }
        JSONArray threadsArray = new JSONArray(threads);
        JSONObject result = new JSONObject();
        result.put("threads", threadsArray);
        return result;
    }
}
