package persistence.nosql;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static persistence.nosql.ArangoInterfaceMethods.notificationsCollectionName;

public class ActivityMethods {
    static ArangoDB arangoDB= ArangoInterfaceMethods.arangoDB;
    static String dbName = ArangoInterfaceMethods.dbName;

    public static final String notificationsCollectionName = ArangoInterfaceMethods.notificationsCollectionName;
    public static final String activitiesCollectionName = ArangoInterfaceMethods.activitiesCollectionName;
    //NOTIFICATION CRUD
    public static String insertNotification(JSONObject notificationJSON) {

        BaseDocument myObject = new BaseDocument();
        myObject.addAttribute("activity_type", notificationJSON.get("activity_type").toString());
        myObject.addAttribute("receiver_id", notificationJSON.get("receiver_id").toString());
        myObject.addAttribute("sender_id", notificationJSON.get("sender_id").toString());
        myObject.addAttribute("created_at", notificationJSON.get("created_at").toString());
        myObject.addAttribute("blocked_at", notificationJSON.get("blocked_at").toString());
        String id = arangoDB.db(dbName).collection(notificationsCollectionName).insertDocument(notificationJSON.toString()).getKey();
        System.out.println("Notification inserted");
        return id;
    }

    public static JSONObject getNotification(String id) throws CustomException {
        BaseDocument notificationDoc = arangoDB.db(dbName).collection(notificationsCollectionName).getDocument(id,
                BaseDocument.class);
        if (notificationDoc == null) {
            throw new CustomException("Notification with ID: " + id + " Not Found");
        }
        JSONObject notificationJSON = new JSONObject(notificationDoc.getProperties());
        return ArangoInterfaceMethods.reformatJSON(notificationJSON);

    }

    public static void updateNotification(String id, JSONObject notificationJSON) {
        BaseDocument myObject = new BaseDocument();
        myObject.addAttribute("activity_type", notificationJSON.get("activity_type").toString());
        myObject.addAttribute("receiver_id", notificationJSON.get("receiver_id").toString());
        myObject.addAttribute("sender_id", notificationJSON.get("sender_id").toString());
        myObject.addAttribute("created_at", notificationJSON.get("created_at").toString());
        myObject.addAttribute("blocked_at", notificationJSON.get("blocked_at").toString());
        arangoDB.db(dbName).collection(notificationsCollectionName).updateDocument(id, notificationJSON.toString());
        System.out.println("Notification Updated");
    }

    public static void deleteNotification(String id) throws CustomException{
        try {
            arangoDB.db(dbName).collection(notificationsCollectionName).deleteDocument(id);
            System.out.println("Notification Deleted: " + id);
        } catch (ArangoDBException e) {
            throw new CustomException("Notification ID does not exist:  " + id);
        }
    }


    //ACTIVITY CRUD
    public static String insertActivity(JSONObject activityJSON) {

        BaseDocument myObject = new BaseDocument();
        myObject.addAttribute("activity_type", activityJSON.get("activity_type").toString());
        myObject.addAttribute("receiver_id", activityJSON.get("receiver_id").toString());
        myObject.addAttribute("sender_id", activityJSON.get("sender_id").toString());
        myObject.addAttribute("created_at", activityJSON.get("created_at").toString());
        myObject.addAttribute("blocked_at", activityJSON.get("blocked_at").toString());
        String id = arangoDB.db(dbName).collection(activitiesCollectionName).insertDocument(activityJSON.toString()).getKey();
        System.out.println("Activity inserted");
        return id;
    }

    public static JSONObject getActivity(String id) throws CustomException{

        BaseDocument activityDoc = arangoDB.db(dbName).collection(activitiesCollectionName).getDocument(id,
                BaseDocument.class);
        if (activityDoc == null) {
            throw new CustomException("Activity with ID: " + id + " Not Found");
        }
        JSONObject activityJSON = new JSONObject(activityDoc.getProperties());
        return ArangoInterfaceMethods.reformatJSON(activityJSON);

    }

    public static void updateActivity(String id, JSONObject activityJSON) {
        BaseDocument myObject = new BaseDocument();
        myObject.addAttribute("activity_type", activityJSON.get("activity_type").toString());
        myObject.addAttribute("receiver_id", activityJSON.get("receiver_id").toString());
        myObject.addAttribute("sender_id", activityJSON.get("sender_id").toString());
        myObject.addAttribute("created_at", activityJSON.get("created_at").toString());
        myObject.addAttribute("blocked_at", activityJSON.get("blocked_at").toString());
        arangoDB.db(dbName).collection(activitiesCollectionName).updateDocument(id, activityJSON.toString());
        System.out.println("Activity Updated");
    }

    public static void deleteActivity(String id) throws CustomException{
        try {
            arangoDB.db(dbName).collection(activitiesCollectionName).deleteDocument(id);
            System.out.println("Activity Deleted: " + id);
        } catch (ArangoDBException e) {
            throw new CustomException("Activity ID does not exist:  " + id);
        }
    }

    public static JSONArray getNotifications(String user_id, int start, int limit) {

        String dbQuery = "For notification in " + notificationsCollectionName
                + " FILTER notification.receiver_id == " + "'"+user_id+"'"
                + " SORT notification.created_at"
                + " Limit "+ start + ", " + limit
                + " RETURN notification";
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(dbQuery, null, null, BaseDocument.class);
        JSONArray result = new JSONArray();
        cursor.forEachRemaining(aDocument -> {
            JSONObject postJSON = new JSONObject(aDocument.getProperties());
            result.put(ArangoInterfaceMethods.reformatJSON(postJSON));
        });
        return result;

    }

    public static JSONArray getActivities(ArrayList<String> followings, int start, int limit) {

        String dbQuery = "For activity in " + activitiesCollectionName
                + " FILTER activity.sender_id IN " + new JSONArray(followings)
                + " SORT activity.created_at"
                + " Limit "+ start + ", " + limit
                + " RETURN activity";
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(dbQuery, null, null, BaseDocument.class);
        JSONArray result = new JSONArray();
        cursor.forEachRemaining(aDocument -> {
            JSONObject postJSON = new JSONObject(aDocument.getProperties());
            result.put(ArangoInterfaceMethods.reformatJSON(postJSON));
        });
        return result;
    }

}
