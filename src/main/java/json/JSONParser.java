package json;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONParser {
    public static boolean getBoolean(String key, JSONObject json) throws exceptions.JSONException {
        try {
            return json.getBoolean(key);
        } catch (org.json.JSONException e) {
            throw new exceptions.JSONException(e.getMessage());
        }
    }

    public static int getInt(String key, JSONObject json) throws exceptions.JSONException {
        try {
            return json.getInt(key);
        } catch (org.json.JSONException e) {
            throw new exceptions.JSONException(e.getMessage());
        }
    }

    public static long getLong(String key, JSONObject json) throws exceptions.JSONException {
        try {
            return json.getLong(key);
        } catch (org.json.JSONException e) {
            throw new exceptions.JSONException(e.getMessage());
        }
    }

    public static double getDouble(String key, JSONObject json) throws exceptions.JSONException {
        try {
            return json.getDouble(key);
        } catch (org.json.JSONException e) {
            throw new exceptions.JSONException(e.getMessage());
        }
    }

    public static String getString(String key, JSONObject json) throws exceptions.JSONException {
        try {
            return json.getString(key);
        } catch (org.json.JSONException e) {
            throw new exceptions.JSONException(e.getMessage());
        }
    }

    public static JSONObject getJSONObject(String key, JSONObject json) throws exceptions.JSONException {
        try {
            return json.getJSONObject(key);
        } catch (org.json.JSONException e) {
            throw new exceptions.JSONException(e.getMessage());
        }
    }

    public static JSONArray getJSONArray(String key, JSONObject json) throws exceptions.JSONException {
        try {
            return json.getJSONArray(key);
        } catch (org.json.JSONException e) {
            throw new exceptions.JSONException(e.getMessage());
        }
    }
}
