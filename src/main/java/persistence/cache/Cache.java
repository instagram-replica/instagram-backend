package persistence.cache;

import org.json.JSONArray;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

public class Cache {

    public static final int EXPIRY_TIME = 3600;


    public static void insertPostIntoCache(JSONObject post, String postId){
        System.out.println("INSERTING INTO CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
        try (Jedis jedis = pool.getResource()) {
            String key = "post$"+postId;
            jedis.set(key,post.toString());
            jedis.expire(key,EXPIRY_TIME);
        }
        pool.close();
    }

    public static JSONObject getPostFromCache(String postId){
        System.out.println("READING FROM CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
        try (Jedis jedis = pool.getResource()) {
            String key = "post$"+postId;
            String jsonPost = jedis.get(key);
            pool.close();
            if(jsonPost!=null){
                System.out.println(new JSONObject(jsonPost));
                return new JSONObject(jsonPost);
            }
            else{
                System.out.println("NULL");
                return null;
            }
        }
    }


    public static void insertPostsIntoCache(JSONArray post, String userId, int pageIndex, int pageSize){
        System.out.println("INSERTING INTO CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
        try (Jedis jedis = pool.getResource()) {
            String key = "posts$"+userId+"$"+pageIndex+"$"+pageSize;
            jedis.set(key,post.toString());
            jedis.expire(key,EXPIRY_TIME);
        }
        pool.close();
    }

    public static JSONArray getPostsFromCache(String userId, int pageIndex, int pageSize){
        System.out.println("READING FROM CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
        try (Jedis jedis = pool.getResource()) {
            String key = "posts$"+userId+"$"+pageIndex+"$"+pageSize;
            String jsonPosts = jedis.get(key);
            pool.close();
            if(jsonPosts!=null){
                System.out.println(new JSONArray(jsonPosts));
                return new JSONArray(jsonPosts);
            }
            else{
                System.out.println("NULL");
                return null;
            }
        }
    }


    public static void insertUserStoriesIntoCache(JSONArray stories, String userId){
        System.out.println("INSERTING INTO CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
        try (Jedis jedis = pool.getResource()) {
            String key = "userstories$"+userId;
            jedis.set(key,stories.toString());
            jedis.expire(key,EXPIRY_TIME);
        }
        pool.close();
    }

    public static JSONArray getUserStoriesFromCache(String userId){
        System.out.println("READING FROM CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
        try (Jedis jedis = pool.getResource()) {
            String key = "userstories$"+userId;
            String jsonStories = jedis.get(key);
            pool.close();
            if(jsonStories!=null){
                System.out.println(new JSONArray(jsonStories));
                return new JSONArray(jsonStories);
            }
            else{
                System.out.println("NULL");
                return null;
            }
        }
    }


    public static void insertStoryIntoCache(JSONObject story, String id){
        System.out.println("INSERTING INTO CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
        try (Jedis jedis = pool.getResource()) {
            String key = "story$"+id;
            jedis.set(key,story.toString());
            jedis.expire(key,EXPIRY_TIME);
        }
        pool.close();
    }

    public static JSONObject getStoryFromCache(String id){
        System.out.println("READING FROM CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
        try (Jedis jedis = pool.getResource()) {
            String key = "story$"+id;
            String jsonStory = jedis.get(key);
            pool.close();
            if(jsonStory!=null){
                System.out.println(new JSONObject(jsonStory));
                return new JSONObject(jsonStory);
            }
            else{
                System.out.println("NULL");
                return null;
            }
        }
    }


}