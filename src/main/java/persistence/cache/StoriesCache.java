package persistence.cache;

import org.json.JSONArray;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

public class StoriesCache {

    public static void insertUserStoriesIntoCache(JSONArray stories, String userId) {
        System.out.println("INSERTING INTO CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "friendsstories$" + userId;
        jedis.set(key, stories.toString());
        jedis.expire(key, CacheInstance.EXPIRY_TIME);
        jedis.close();
    }
    public static void insertDiscoverStoriesIntoCache(JSONArray stories, String userId) {
        System.out.println("INSERTING INTO CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "discoverstories$" + userId;
        jedis.set(key, stories.toString());
        jedis.expire(key, CacheInstance.EXPIRY_TIME);
        jedis.close();
    }
    public static JSONArray getUserStoriesFromCache(String userId) {
        System.out.println("READING FROM CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "friendsstories$" + userId;
        String jsonStories = jedis.get(key);
        jedis.close();
        if (jsonStories != null) {
            return new JSONArray(jsonStories);
        } else {
            return null;
        }
    }
    public static JSONArray getDiscoverStoriesFromCache(String userId) {
        System.out.println("READING FROM CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "discoverstories$" + userId;
        String jsonStories = jedis.get(key);
        jedis.close();
        if (jsonStories != null) {
            return new JSONArray(jsonStories);
        } else {
            return null;
        }
    }
    public static JSONArray getMyStoriesFromCache(String userId) {
        System.out.println("READING FROM CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "mystories$" + userId;
        String jsonStories = jedis.get(key);
        jedis.close();
        if (jsonStories != null) {
            return new JSONArray(jsonStories);
        } else {
            return null;
        }
    }



    public static void insertStoryIntoCache(JSONObject story, String id) {
        System.out.println("INSERTING INTO CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "story$" + id;
        jedis.set(key, story.toString());
        jedis.expire(key, CacheInstance.EXPIRY_TIME);
        jedis.close();
    }


    public static JSONObject getStoryFromCache(String id) {
        System.out.println("READING FROM CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "story$" + id;
        String jsonStory = jedis.get(key);
        jedis.close();
        if (jsonStory != null) {
            return new JSONObject(jsonStory);
        } else {
            return null;
        }
    }
}
