package services.users;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.sql.users.User;

import java.util.ArrayList;
import java.util.List;

public class Helpers {
  public static User mapJSONToUser(JSONObject json) {
    return new User.Builder()
      .id((String) getSafely(json, "id"))
      .username((String) getSafely(json, "username"))
      .email((String) getSafely(json, "email"))
      .password((String) getSafely(json, "password"))
      .isPrivate((Boolean) getSafely(json, "isPrivate"))
      .fullName((String) getSafely(json, "fullName"))
      .gender((String) getSafely(json, "gender"))
      .bio((String) getSafely(json, "bio"))
      .phoneNumber((String) getSafely(json, "phoneNumber"))
      .profilePictureUrl((String) getSafely(json, "profilePictureUrl"))
      .websiteUrl((String) getSafely(json, "websiteUrl"))
      .build();
  }

  public static JSONObject mapUserToJSON(User user) {
    if (user == null) {
      return new JSONObject();
    }

    return new JSONObject()
      .put("id", mapJavaNullToJSONNull(user.id))
      .put("username", mapJavaNullToJSONNull(user.username))
      .put("email", mapJavaNullToJSONNull(user.email))
      .put("isPrivate", mapJavaNullToJSONNull(user.isPrivate))
      .put("fullName", mapJavaNullToJSONNull(user.fullName))
      .put("gender", mapJavaNullToJSONNull(user.gender))
      .put("bio", mapJavaNullToJSONNull(user.bio))
      .put("phoneNumber", mapJavaNullToJSONNull(user.phoneNumber))
      .put("profilePictureUrl", mapJavaNullToJSONNull(user.profilePictureUrl))
      .put("websiteUrl", mapJavaNullToJSONNull(user.websiteUrl))
      .put("verifiedAt", mapJavaNullToJSONNull(user.verifiedAt))
      .put("createdAt", mapJavaNullToJSONNull(user.createdAt))
      .put("updatedAt", mapJavaNullToJSONNull(user.updatedAt))
      .put("blockedAt", mapJavaNullToJSONNull(user.blockedAt))
      .put("deletedAt", mapJavaNullToJSONNull(user.deletedAt));
  }

  public static JSONObject constructOKResponse(JSONObject data) {
    return new JSONObject().put("data", data).put("error", JSONObject.NULL);
  }

  public static JSONObject constructOKResponse(JSONArray data) {
    return new JSONObject().put("data", data).put("error", JSONObject.NULL);
  }

  public static JSONObject constructErrorResponse() {
    String GENERIC_ERROR_MESSAGE = "Internal server error";
    return constructErrorResponse(GENERIC_ERROR_MESSAGE);
  }

  public static JSONObject constructErrorResponse(String message) {
    JSONObject error = new JSONObject().put("message", message);
    return new JSONObject().put("data", JSONObject.NULL).put("error", error);
  }

  public static List<Object> convertJSONArrayToList(JSONArray jsonArray) {
    List<Object> list = new ArrayList<>();

    for (int i = 0; i < jsonArray.length(); i++) {
      list.add(jsonArray.get(i));
    }

    return list;
  }

  public static JSONArray convertUsersListToJSONArray(List<User> list) {
    JSONArray jsonArray = new JSONArray();

    for (User element : list) {
      jsonArray.put(mapUserToJSON(element));
    }

    return jsonArray;
  }

  public static JSONArray convertStringsListToJSONArray(List<String> list) {
    JSONArray jsonArray = new JSONArray();

    for (String element : list) {
      jsonArray.put(element);
    }

    return jsonArray;
  }

  private static Object mapJavaNullToJSONNull(Object object) {
    return object == null ? JSONObject.NULL : object;
  }

  private static Object getSafely(JSONObject json, String key) {
    return json.has(key) ? json.get(key) : null;
  }
}
