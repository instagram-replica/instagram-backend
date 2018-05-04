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

public class CacheInstance {
    private static Properties properties;
    public static JedisPool pool;
    public static final int EXPIRY_TIME = 3600;

    static {
        try {
            properties = readPropertiesFile("src/main/resources/redis.properties");
            pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
