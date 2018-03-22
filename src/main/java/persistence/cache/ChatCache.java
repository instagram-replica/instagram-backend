package persistence.cache;

import org.json.JSONObject;
import redis.clients.jedis.Jedis;

public class ChatCache {
    public static void insertThreadIntoCache(JSONObject thread, String id) {
        System.out.println("INSERTING INTO CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "thread$" + id;
        jedis.set(key, thread.toString());
        jedis.expire(key, CacheInstance.EXPIRY_TIME);
        jedis.close();
    }


    public static JSONObject getThreadFromCache(String id) {
        System.out.println("READING FROM CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "thread$" + id;
        String jsonThread = jedis.get(key);
        jedis.close();
        if (jsonThread != null) {
            return new JSONObject(jsonThread);
        } else {
            return null;
        }
    }

}
