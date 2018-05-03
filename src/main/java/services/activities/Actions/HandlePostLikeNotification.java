package services.activities.Actions;

import org.json.JSONObject;
import persistence.nosql.ActivityMethods;

public class HandlePostLikeNotification implements Action{

    public static JSONObject execute(JSONObject jsonObject, String userId) {
        String receiverId = jsonObject.getString("receiverId");
        String postID = jsonObject.getString("postID");

        JSONObject notificationJSON = new JSONObject();
        JSONObject innerJSON = new JSONObject();
        innerJSON.put("type", "liking_post");
        innerJSON.put("post_id",postID);
        notificationJSON.put("activity_type",innerJSON);
        notificationJSON.put("sender_id", userId);
        notificationJSON.put("receiver_id", receiverId);
        notificationJSON.put("created_at",new java.util.Date());
        notificationJSON.put("blocked_at","null");
        notificationJSON.put("id",utilities.Main.generateUUID());
        String id  = ActivityMethods.insertNotification(notificationJSON);

        return new JSONObject().put("id", id);
    }
}
