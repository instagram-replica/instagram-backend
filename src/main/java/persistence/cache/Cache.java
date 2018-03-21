package persistence.cache;

import org.json.JSONArray;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import static utilities.Main.readPropertiesFile;

public class Cache {
    private static Properties properties;
    public static final int EXPIRY_TIME = 1;

    static {
        try {
            properties = readPropertiesFile("src/main/resources/redis.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void insertPostIntoCache(JSONObject post, String postId) {
        System.out.println("INSERTING INTO CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
        try (Jedis jedis = pool.getResource()) {
            String key = "post$" + postId;
            jedis.set(key, post.toString());
            jedis.expire(key, EXPIRY_TIME);
        }
        pool.close();
    }

    public static JSONObject getPostFromCache(String postId) {
        System.out.println("READING FROM CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
        try (Jedis jedis = pool.getResource()) {
            String key = "post$" + postId;
            String jsonPost = jedis.get(key);
            pool.close();
            if (jsonPost != null) {
                return new JSONObject(jsonPost);
            } else {
                return null;
            }
        }
    }

    public static void insertPostsIntoCache(JSONArray post, String userId, int pageIndex, int pageSize) {
        System.out.println("INSERTING INTO CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
        try (Jedis jedis = pool.getResource()) {
            String key = "posts$" + userId + "$" + pageIndex + "$" + pageSize;
            jedis.set(key, post.toString());
            jedis.expire(key, EXPIRY_TIME);
        }
        pool.close();
    }

    public static JSONArray getPostsFromCache(String userId, int pageIndex, int pageSize) {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
        try (Jedis jedis = pool.getResource()) {
            String key = "posts$" + userId + "$" + pageIndex + "$" + pageSize;
            String jsonPosts = jedis.get(key);
            pool.close();
            if (jsonPosts != null) {
                System.out.println("READING FROM CACHE");
                return new JSONArray(jsonPosts);
            } else {
                return null;
            }
        }
    }

    public static void insertCommentsIntoCache(JSONArray comments, String postId) {
        System.out.println("INSERTING INTO CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
        try (Jedis jedis = pool.getResource()) {
            String key = "comments$" + postId;
            jedis.set(key, comments.toString());
            jedis.expire(key, EXPIRY_TIME);
        }
        pool.close();
    }

    public static JSONArray getCommentsFromCache(String postId) {
        System.out.println("READING FROM CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
        try (Jedis jedis = pool.getResource()) {
            String key = "comments$" + postId;
            String jsonComments = jedis.get(key);
            pool.close();
            if (jsonComments != null) {
                return new JSONArray(jsonComments);
            } else {
                return null;
            }
        }
    }


    public static void insertUserStoriesIntoCache(JSONArray stories, String userId) {
        System.out.println("INSERTING INTO CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
        try (Jedis jedis = pool.getResource()) {
            String key = "friendsstories$" + userId;
            jedis.set(key, stories.toString());
            jedis.expire(key, EXPIRY_TIME);
        }
        pool.close();
    }
    public static void insertDiscoverStoriesIntoCache(JSONArray stories, String userId) {
        System.out.println("INSERTING INTO CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
        try (Jedis jedis = pool.getResource()) {
            String key = "discoverstories$" + userId;
            jedis.set(key, stories.toString());
            jedis.expire(key, EXPIRY_TIME);
        }
        pool.close();
    }
    public static JSONArray getUserStoriesFromCache(String userId) {
        System.out.println("READING FROM CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
        try (Jedis jedis = pool.getResource()) {
            String key = "friendsstories$" + userId;
            String jsonStories = jedis.get(key);
            pool.close();
            if (jsonStories != null) {
                return new JSONArray(jsonStories);
            } else {
                return null;
            }
        }
    }
    public static JSONArray getDiscoverStoriesFromCache(String userId) {
        System.out.println("READING FROM CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
        try (Jedis jedis = pool.getResource()) {
            String key = "discoverstories$" + userId;
            String jsonStories = jedis.get(key);
            pool.close();
            if (jsonStories != null) {
                return new JSONArray(jsonStories);
            } else {
                return null;
            }
        }
    }
    public static JSONArray getMyStoriesFromCache(String userId) {
        System.out.println("READING FROM CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
        try (Jedis jedis = pool.getResource()) {
            String key = "mystories$" + userId;
            String jsonStories = jedis.get(key);
            pool.close();
            if (jsonStories != null) {
                return new JSONArray(jsonStories);
            } else {
                return null;
            }
        }
    }



    public static void insertStoryIntoCache(JSONObject story, String id) {
        System.out.println("INSERTING INTO CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
        try (Jedis jedis = pool.getResource()) {
            String key = "story$" + id;
            jedis.set(key, story.toString());
            jedis.expire(key, EXPIRY_TIME);
        }
        pool.close();
    }


    public static JSONObject getStoryFromCache(String id) {
        System.out.println("READING FROM CACHE");
        JedisPool pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
        try (Jedis jedis = pool.getResource()) {
            String key = "story$" + id;
            String jsonStory = jedis.get(key);
            pool.close();
            if (jsonStory != null) {
                return new JSONObject(jsonStory);
            } else {
                return null;
            }
        }
    }


}
