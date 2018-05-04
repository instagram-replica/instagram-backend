package services.activities.Actions;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.ActivityMethods;
import persistence.sql.users.Database;

import java.sql.SQLException;
import java.sql.Timestamp;

public class HandlePostTagNotification implements Action{

    public static JSONObject execute(JSONObject jsonObject, String userId) throws SQLException {
        JSONArray receivers = jsonObject.getJSONArray("taggedUsers");
        String postID = jsonObject.getString("postID");

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
            ActivityMethods.insertNotification(notifyReceiver);
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
        String id = ActivityMethods.insertNotification(notificationJSON);

        return new JSONObject().put("id", jsonObject);
    }
}
