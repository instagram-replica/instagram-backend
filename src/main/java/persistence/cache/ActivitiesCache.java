package persistence.cache;
import org.json.JSONArray;
import redis.clients.jedis.Jedis;

public class ActivitiesCache {
    public static void insertNotificationsIntoCache(JSONArray notifications, String userId, int start, int size) {
        System.out.println("INSERTING INTO CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "notifications$" + userId + "$" + start + "$" + size;
        jedis.set(key, notifications.toString());
        jedis.expire(key, CacheInstance.EXPIRY_TIME);
        jedis.close();
    }

    public static JSONArray getNotificationsFromCache( String userId, int start, int size) {
        System.out.println("READING FROM CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "notifications$" + userId + "$" + start + "$" + size;
        String notificationsArray = jedis.get(key);
        jedis.close();
        if (notificationsArray != null) {
            return new JSONArray(notificationsArray);
        } else {
            return null;
        }
    }
}
