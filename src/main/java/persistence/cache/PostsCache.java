package persistence.cache;

import org.json.JSONArray;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

public class PostsCache {

    public static void insertPostIntoCache(JSONObject post, String postId) {
        System.out.println("INSERTING INTO CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "post$" + postId;
        jedis.set(key, post.toString());
        jedis.expire(key, CacheInstance.EXPIRY_TIME);
        jedis.close();

    }

    public static JSONObject getPostFromCache(String postId) {
        System.out.println("READING FROM CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "post$" + postId;
        String jsonPost = jedis.get(key);
        jedis.close();
        if (jsonPost != null) {
            return new JSONObject(jsonPost);
        } else {
            return null;
        }
    }

    public static void insertPostsIntoCache(JSONArray post, String userId, int pageIndex, int pageSize) {
        System.out.println("INSERTING INTO CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "posts$" + userId + "$" + pageIndex + "$" + pageSize;
        jedis.set(key, post.toString());
        jedis.expire(key, CacheInstance.EXPIRY_TIME);
        jedis.close();
    }

    public static JSONArray getPostsFromCache(String userId, int pageIndex, int pageSize) {
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "posts$" + userId + "$" + pageIndex + "$" + pageSize;
        String jsonPosts = jedis.get(key);
        jedis.close();
        if (jsonPosts != null) {
            System.out.println("READING FROM CACHE");
            return new JSONArray(jsonPosts);
        } else {
            return null;
        }

    }

    public static void insertCommentsIntoCache(JSONArray comments, String postId) {
        System.out.println("INSERTING INTO CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "comments$" + postId;
        jedis.set(key, comments.toString());
        jedis.expire(key, CacheInstance.EXPIRY_TIME);
        jedis.close();
    }

    public static JSONArray getCommentsFromCache(String postId) {
        System.out.println("READING FROM CACHE");
        Jedis jedis = CacheInstance.pool.getResource();
        String key = "comments$" + postId;
        String jsonComments = jedis.get(key);
        jedis.close();
        if (jsonComments != null) {
            return new JSONArray(jsonComments);
        } else {
            return null;
        }
    }

}
