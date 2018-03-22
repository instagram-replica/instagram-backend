package services.activities;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.ActivitiesCache;
import persistence.nosql.ArangoInterfaceMethods;
import persistence.sql.users.Database;

import java.sql.Timestamp;

public class NotificationActions {
//TODO: insert in notifications table!!!
    public static void handlePostLikeNotification(JSONObject params, String userId) {
        String receiverId = params.getString("receiverId");
        String postID = params.getString("postID");

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
        ArangoInterfaceMethods.insertNotification(notificationJSON);
        ArangoInterfaceMethods.insertActivity(notificationJSON);
    }

    public static void handleFollowNotification(JSONObject params, String userId) {
        String receiverId = params.getString("userId");

        JSONObject notificationJSON = new JSONObject();
        JSONObject innerJSON = new JSONObject();
        innerJSON.put("type", "following");
        notificationJSON.put("activity_type",innerJSON);
        notificationJSON.put("sender_id", userId);
        notificationJSON.put("receiver_id", receiverId);
        notificationJSON.put("created_at",new java.util.Date());
        notificationJSON.put("blocked_at","null");
        notificationJSON.put("id",utilities.Main.generateUUID());
        ArangoInterfaceMethods.insertNotification(notificationJSON);
        ArangoInterfaceMethods.insertActivity(notificationJSON);
    }

    public static void handlePostTagNotification(JSONObject params, String userId) {
        JSONArray receivers = params.getJSONArray("taggedUsers");
        String postID = params.getString("postID");

        for(int i=0;i<receivers.length();i++){
            JSONObject taggedPerson = receivers.getJSONObject(i);
            JSONObject notifyReceiver = new JSONObject();
            // TODO: Make call to users service to request resource
            String recID = Database.getUserByUsername(taggedPerson.getString("username")).id;
            notifyReceiver.put("activity_type","{ type: tag, user_id: " + recID+ "\"");
            notifyReceiver.put("receiver_id", recID);
            notifyReceiver.put("sender_id", userId);
            notifyReceiver.put("created_at", new Timestamp(System.currentTimeMillis()));
            notifyReceiver.put("blocked_at",new Timestamp(System.currentTimeMillis()));
            ArangoInterfaceMethods.insertNotification(notifyReceiver);
        }

        JSONObject notificationJSON =new JSONObject();
        JSONObject innerJSON = new JSONObject();
        innerJSON.put("type","tagging");
        innerJSON.put("tagged_users",receivers);
        innerJSON.put("post_id", postID);
        notificationJSON.put("activity_type",innerJSON);
        notificationJSON.put("sender_id", userId);
        //notificationJSON.put("receiver_id", receiverId);

        notificationJSON.put("created_at",new java.util.Date());
        notificationJSON.put("blocked_at","null");
        notificationJSON.put("id",utilities.Main.generateUUID());
        ArangoInterfaceMethods.insertNotification(notificationJSON);
    }


    public static void handleCommentNotification(JSONObject params, String userId) {
        String receiverId = params.getString("receiverId");
        String commentID = params.getString("commentID");

        JSONObject activityJSON = new JSONObject();
        JSONObject innerJSON = new JSONObject();
        innerJSON.put("type", "commenting");
        innerJSON.put("comment_id", commentID);
        activityJSON.put("activity_type", innerJSON);
        activityJSON.put("sender_id", userId);
        activityJSON.put("receiver_id", receiverId);
        activityJSON.put("created_at",new java.util.Date());
        activityJSON.put("blocked_at","null");
        activityJSON.put("id",utilities.Main.generateUUID());
        ArangoInterfaceMethods.insertNotification(activityJSON);
    }

    public static void handleCommentReplyNotification(JSONObject params, String userId) {
        String receiverId = params.getString("receiverId");
        String commentID = params.getString("commentID");

        JSONObject activityJSON = new JSONObject();
        JSONObject innerJSON = new JSONObject();
        innerJSON.put("type", "commenting");
        innerJSON.put("comment_id", commentID);
        activityJSON.put("activity_type", innerJSON);
        activityJSON.put("sender_id", userId);
        activityJSON.put("receiver_id", receiverId);
        activityJSON.put("created_at",new java.util.Date());
        activityJSON.put("blocked_at","null");
        activityJSON.put("id",utilities.Main.generateUUID());
        ArangoInterfaceMethods.insertActivity(activityJSON);
    }

    public static JSONObject handleGettingNotifications(JSONObject params, String userId){
        int size = params.getInt("pageSize");
        int start = params.getInt("pageIndex") * size;

        JSONArray notifications = ActivitiesCache.getNotificationsFromCache(userId, start, size);
        if(notifications==null) {
            notifications = ArangoInterfaceMethods.getNotifications(userId, start, size);
            ActivitiesCache.insertNotificationsIntoCache(notifications,userId, start, size);
        }


        JSONObject result = new JSONObject();
        result.put("notifications", notifications);
        return result;
    }
}
