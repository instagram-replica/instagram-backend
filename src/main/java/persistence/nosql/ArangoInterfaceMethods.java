package persistence.nosql;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.EdgeDefinition;
import com.arangodb.entity.GraphEntity;
import com.arangodb.model.GraphCreateOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArangoInterfaceMethods {

    private static ArangoDB arangoDB = new ArangoDB.Builder().build();
    static String dbName =  "InstagramAQL";

    private static final String threadsCollectionName = "Threads";
    private static final String notificationsCollectionName = "Notifications";
    private static final String activitiesCollectionName = "Activities";
    private static final String storiesCollectionName = "Stories";
    private static final String postsCollectionName = "Posts";
    private static final String bookmarksCollectionName = "Bookmarks";



    public static void main(String[]args) {
        initializeDB();
        followsGraph();
    }


    public static void initializeDB(){
        try {
            if(!arangoDB.getDatabases().contains(dbName)){
                arangoDB.createDatabase(dbName);
                System.out.println("Database created: " + dbName);
                CollectionEntity threadsCollection = arangoDB.db(dbName).createCollection(threadsCollectionName);
                System.out.println("Collection created: " + threadsCollection.getName());

                CollectionEntity notificationsCollection = arangoDB.db(dbName).createCollection(notificationsCollectionName);
                System.out.println("Collection created: " + notificationsCollection.getName());

                CollectionEntity ActivitiesCollection = arangoDB.db(dbName).createCollection(activitiesCollectionName);
                System.out.println("Collection created: " + ActivitiesCollection.getName());

                CollectionEntity storiesCollection = arangoDB.db(dbName).createCollection(storiesCollectionName);
                System.out.println("Collection created: " + storiesCollection.getName());

                CollectionEntity postsCollection = arangoDB.db(dbName).createCollection(postsCollectionName);
                System.out.println("Collection created: " + postsCollection.getName());

                CollectionEntity bookmarksCollection = arangoDB.db(dbName).createCollection(bookmarksCollectionName);
                System.out.println("Collection created: " + bookmarksCollection.getName());
            }

        } catch (ArangoDBException e) {
            System.err.println("Failed to create database and collections: " + dbName + "; " + e.getMessage());
        }


    }

    public static void closeConnection(){
        arangoDB.shutdown();
    }

//    public static boolean validateThreadJSON(JSONObject threadJSON){
//        try{
//            UUID id =  UUID.fromString(threadJSON.get("id").toString());
//        }
//        catch (Exception e){
//            System.err.print("ID is not of valid, " + e.getMessage());
//            return false;
//        }
//
//        try{
//            UUID creator_id =  UUID.fromString(threadJSON.get("creator_id").toString());
//        }
//        catch (Exception e){
//            System.err.print("creator_id is not of valid, " + e.getMessage());
//            return false;
//        }
//
//        try{
//            JSONArray jsonArray = threadJSON.getJSONArray("users_ids");
//
//        }
//        catch (Exception e){
//            System.err.print("users_ids is not of valid array, " + e.getMessage());
//            return false;
//        }
//
//        try{
//            UUID id =  UUID.fromString(threadJSON.get("id").toString());
//        }
//        catch (Exception e){
//            System.err.print("ID is not of valid, " + e.getMessage());
//            return false;
//        }
//        return false;
//    }

    //Thread CRUD
    public static void insertThread(JSONObject threadJSON){

        try {


            BaseDocument myObject = new BaseDocument();
            myObject.setKey(threadJSON.get("id").toString());
            myObject.addAttribute("id", threadJSON.get("id").toString());
            myObject.addAttribute("creator_id", threadJSON.get("creator_id").toString());
            myObject.addAttribute("users_ids", threadJSON.get("users_ids").toString());
            myObject.addAttribute("name", threadJSON.get("name").toString());
            myObject.addAttribute("created_at", threadJSON.get("created_at").toString());
            myObject.addAttribute("deleted_at", threadJSON.get("deleted_at").toString());
            myObject.addAttribute("blocked_at", threadJSON.get("blocked_at").toString());
            myObject.addAttribute("messages", threadJSON.get("messages").toString());
            arangoDB.db(dbName).collection(threadsCollectionName).insertDocument(myObject);
            System.out.println("Thread inserted");
        } catch (ArangoDBException e) {
            System.err.println("Failed to insert thread. " + e.getMessage());
        } catch (JSONException e){
            System.err.println("JSON Thread Incorrect format. " + e.getMessage());
        }

    }

    public static JSONObject getThread(String id){
        try {
            BaseDocument threadDoc = arangoDB.db(dbName).collection(threadsCollectionName).getDocument(id,
                    BaseDocument.class);
            if(threadDoc == null){
                throw new ArangoDBException("Thread with ID: " + id+" Not Found");
            }
            JSONObject threadJSON  = new JSONObject(threadDoc.getProperties());
            System.out.println("OUT:  "+new JSONObject(threadJSON));
            return reformatJSON(threadJSON);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get Thread: " + e.getMessage());
            return null;
        }
    }

    public static void updateThread(String id,JSONObject threadJSON){
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.setKey(threadJSON.get("id").toString());
            myObject.addAttribute("id", threadJSON.get("id").toString());
            myObject.addAttribute("creator_id", threadJSON.get("creator_id").toString());
            myObject.addAttribute("users_ids", threadJSON.get("users_ids").toString());
            myObject.addAttribute("name", threadJSON.get("name").toString());
            myObject.addAttribute("created_at", threadJSON.get("created_at").toString());
            myObject.addAttribute("deleted_at", threadJSON.get("deleted_at").toString());
            myObject.addAttribute("blocked_at", threadJSON.get("blocked_at").toString());
            myObject.addAttribute("messages", threadJSON.get("messages").toString());
            arangoDB.db(dbName).collection(threadsCollectionName).updateDocument(id, myObject);
            System.out.println("Thread Updated");
        } catch (ArangoDBException e) {
            System.err.println("Failed to Update Thread. " + e.getMessage());
        } catch (JSONException e){
            System.err.println("JSON Thread Incorrect format. " + e.getMessage());
        }
    }

    public static void deleteThread(String id){
        try {
            arangoDB.db(dbName).collection(threadsCollectionName).deleteDocument(id);
            System.out.println("Thread Deleted: "+id);
        } catch (ArangoDBException e){
            System.err.println("Thread ID does not exist:  "+id+",  "+e.getMessage());
        }
    }


    //NOTIFICATION CRUD
    public static void insertNotification(JSONObject notificationJSON){
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.setKey(notificationJSON.get("id").toString());
            myObject.addAttribute("id", notificationJSON.get("id").toString());
            myObject.addAttribute("activity_type", notificationJSON.get("activity_type").toString());
            myObject.addAttribute("receiver_id", notificationJSON.get("receiver_id").toString());
            myObject.addAttribute("sender_id", notificationJSON.get("sender_id").toString());
            myObject.addAttribute("created_at", notificationJSON.get("created_at").toString());
            myObject.addAttribute("blocked_at", notificationJSON.get("blocked_at").toString());
            arangoDB.db(dbName).collection(notificationsCollectionName).insertDocument(myObject);
            System.out.println("Notification inserted");
        } catch (ArangoDBException e) {
            System.err.println("Failed to insert Notification. " + e.getMessage());
        } catch (JSONException e){
            System.err.println("JSON Notification Incorrect format. " + e.getMessage());
        }
    }

    public static JSONObject getNotification(String id){
        try {
            BaseDocument notificationDoc = arangoDB.db(dbName).collection(notificationsCollectionName).getDocument( id,
                    BaseDocument.class);
            if(notificationDoc == null){
                throw new ArangoDBException("Notification with ID: " + id+" Not Found");
            }
            JSONObject notificationJSON  = new JSONObject(notificationDoc.getProperties());
            System.out.println("OUT:  "+new JSONObject(notificationJSON));
            return reformatJSON(notificationJSON);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get Notification: " + e.getMessage());
            return null;
        }
    }

    public static void updateNotification(String id,JSONObject notificationJSON){
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.setKey(notificationJSON.get("id").toString());
            myObject.addAttribute("id", notificationJSON.get("id").toString());
            myObject.addAttribute("activity_type", notificationJSON.get("activity_type").toString());
            myObject.addAttribute("receiver_id", notificationJSON.get("receiver_id").toString());
            myObject.addAttribute("sender_id", notificationJSON.get("sender_id").toString());
            myObject.addAttribute("created_at", notificationJSON.get("created_at").toString());
            myObject.addAttribute("blocked_at", notificationJSON.get("blocked_at").toString());
            arangoDB.db(dbName).collection(notificationsCollectionName).updateDocument(id, myObject);
            System.out.println("Notification Updated");
        } catch (ArangoDBException e) {
            System.err.println("Failed to Update Notification. " + e.getMessage());
        } catch (JSONException e){
            System.err.println("JSON Notification Incorrect format. " + e.getMessage());
        }
    }

    public static void deleteNotification(String id){
        try {
            arangoDB.db(dbName).collection(notificationsCollectionName).deleteDocument(id);
            System.out.println("Notification Deleted: "+id);
        } catch (ArangoDBException e){
            System.err.println("Notification ID does not exist:  "+id+",  "+e.getMessage());
        }
    }



    //ACTIVITY CRUD
    public static void insertActivity(JSONObject activityJSON){
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.setKey(activityJSON.get("id").toString());
            myObject.addAttribute("id", activityJSON.get("id").toString());
            myObject.addAttribute("activity_type", activityJSON.get("activity_type").toString());
            myObject.addAttribute("receiver_id", activityJSON.get("receiver_id").toString());
            myObject.addAttribute("sender_id", activityJSON.get("sender_id").toString());
            myObject.addAttribute("created_at", activityJSON.get("created_at").toString());
            myObject.addAttribute("blocked_at", activityJSON.get("blocked_at").toString());
            arangoDB.db(dbName).collection(activitiesCollectionName).insertDocument(myObject);
            System.out.println("Activity inserted");
        } catch (ArangoDBException e) {
            System.err.println("Failed to insert Activity. " + e.getMessage());
        } catch (JSONException e){
            System.err.println("JSON Activity Incorrect format. " + e.getMessage());
        }
    }

    public static JSONObject getActivity(String id){
        try {
            BaseDocument activityDoc = arangoDB.db(dbName).collection(activitiesCollectionName).getDocument(id,
                    BaseDocument.class);
            if(activityDoc == null){
                throw new ArangoDBException("Activity with ID: " + id+" Not Found");
            }
            JSONObject activityJSON  = new JSONObject(activityDoc.getProperties());
            System.out.println("OUT:  "+new JSONObject(activityJSON));
            return reformatJSON(activityJSON);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get Activity: " + e.getMessage());
            return null;
        }
    }

    public static void updateActivity(String id,JSONObject activityJSON){
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.setKey(activityJSON.get("id").toString());
            myObject.addAttribute("id", activityJSON.get("id").toString());
            myObject.addAttribute("activity_type", activityJSON.get("activity_type").toString());
            myObject.addAttribute("receiver_id", activityJSON.get("receiver_id").toString());
            myObject.addAttribute("sender_id", activityJSON.get("sender_id").toString());
            myObject.addAttribute("created_at", activityJSON.get("created_at").toString());
            myObject.addAttribute("blocked_at", activityJSON.get("blocked_at").toString());
            arangoDB.db(dbName).collection(activitiesCollectionName).updateDocument(id, myObject);
            System.out.println("Activity Updated");
        } catch (ArangoDBException e) {
            System.err.println("Failed to Update Activity. " + e.getMessage());
        } catch (JSONException e){
            System.err.println("JSON Activity Incorrect format. " + e.getMessage());
        }
    }

    public static void deleteActivity(String id){
        try {
            arangoDB.db(dbName).collection(activitiesCollectionName).deleteDocument(id);
            System.out.println("Activity Deleted: "+id);
        } catch (ArangoDBException e){
            System.err.println("Activity ID does not exist:  "+id+",  "+e.getMessage());
        }
    }


    //STORY CRUD
    public static void insertStory(JSONObject storyJSON){
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.setKey(storyJSON.get("id").toString());
            myObject.addAttribute("id", storyJSON.get("id").toString());
            myObject.addAttribute("user_id", storyJSON.get("user_id").toString());
            myObject.addAttribute("is_featured", storyJSON.get("is_featured").toString());
            myObject.addAttribute("media_id", storyJSON.get("media_id").toString());
            myObject.addAttribute("reports", storyJSON.get("reports").toString());
            myObject.addAttribute("seen_by_users_ids", storyJSON.get("seen_by_users_ids").toString());
            myObject.addAttribute("created_at", storyJSON.get("created_at").toString());
            myObject.addAttribute("deleted_at", storyJSON.get("deleted_at").toString());
            myObject.addAttribute("expired_at", storyJSON.get("expired_at").toString());
            myObject.addAttribute("blocked_at", storyJSON.get("blocked_at").toString());
            arangoDB.db(dbName).collection(storiesCollectionName).insertDocument(myObject);
            System.out.println("Story inserted");
        } catch (ArangoDBException e) {
            System.err.println("Failed to insert Story. " + e.getMessage());
        } catch (JSONException e){
            System.err.println("JSON Story Incorrect format. " + e.getMessage());
        }
    }

    public static JSONObject getStory(String id){
        try {
            BaseDocument storyDoc = arangoDB.db(dbName).collection(storiesCollectionName).getDocument(id,
                    BaseDocument.class);
            if(storyDoc == null){
                throw new ArangoDBException("Story with ID: " + id+" Not Found");
            }
            JSONObject storyJSON  = new JSONObject(storyDoc.getProperties());
            System.out.println("OUT:  "+new JSONObject(storyJSON));
            return reformatJSON(storyJSON);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get Story: " + e.getMessage());
            return null;
        }
    }

    public static void updateStory(String id,JSONObject storyJSON){
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.setKey(storyJSON.get("id").toString());
            myObject.addAttribute("id", storyJSON.get("id").toString());
            myObject.addAttribute("user_id", storyJSON.get("user_id").toString());
            myObject.addAttribute("is_featured", storyJSON.get("is_featured").toString());
            myObject.addAttribute("media_id", storyJSON.get("media_id").toString());
            myObject.addAttribute("reports", storyJSON.get("reports").toString());
            myObject.addAttribute("seen_by_users_ids", storyJSON.get("seen_by_users_ids").toString());
            myObject.addAttribute("created_at", storyJSON.get("created_at").toString());
            myObject.addAttribute("deleted_at", storyJSON.get("deleted_at").toString());
            myObject.addAttribute("expired_at", storyJSON.get("expired_at").toString());
            myObject.addAttribute("blocked_at", storyJSON.get("blocked_at").toString());
            arangoDB.db(dbName).collection(storiesCollectionName).updateDocument(id, myObject);
            System.out.println("Story Updated");
        } catch (ArangoDBException e) {
            System.err.println("Failed to Update Story. " + e.getMessage());
        } catch (JSONException e){
            System.err.println("JSON Story Incorrect format. " + e.getMessage());
        }
    }

    public static void deleteStory(String id){
        try {
            arangoDB.db(dbName).collection(storiesCollectionName).deleteDocument(id);
            System.out.println("Story Deleted: "+id);
        } catch (ArangoDBException e){
            System.err.println("Story ID does not exist:  "+id+",  "+e.getMessage());
        }
    }


    //POSTS CRUD
    public static void insertPost(JSONObject postJSON){
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.setKey(postJSON.get("id").toString());
            myObject.addAttribute("id", postJSON.get("id").toString());
            myObject.addAttribute("user_id", postJSON.get("user_id").toString());
            myObject.addAttribute("caption", postJSON.get("caption").toString());
            myObject.addAttribute("media", postJSON.get("media").toString());
            myObject.addAttribute("likes", postJSON.get("likes").toString());
            myObject.addAttribute("tags", postJSON.get("tags").toString());
            myObject.addAttribute("location", postJSON.get("location").toString());
            myObject.addAttribute("comments", postJSON.get("comments").toString());
            myObject.addAttribute("created_at", postJSON.get("created_at").toString());
            myObject.addAttribute("updated_at", postJSON.get("updated_at").toString());
            myObject.addAttribute("blocked_at", postJSON.get("blocked_at").toString());
            myObject.addAttribute("deleted_at", postJSON.get("deleted_at").toString());
            arangoDB.db(dbName).collection(postsCollectionName).insertDocument(myObject);
            System.out.println("Post inserted");
        } catch (ArangoDBException e) {
            System.err.println("Failed to insert Post. " + e.getMessage());
        } catch (JSONException e){
            System.err.println("JSON Post Incorrect format. " + e.getMessage());
        }
    }

    public static JSONObject getPost(String id){
        try {
            BaseDocument postDoc = arangoDB.db(dbName).collection(postsCollectionName).getDocument(id,
                    BaseDocument.class);
            if(postDoc == null){
                throw new ArangoDBException("Post with ID: " + id+" Not Found");
            }
            JSONObject postJSON  = new JSONObject(postDoc.getProperties());
            System.out.println("OUT:  "+new JSONObject(postJSON));
            return reformatJSON(postJSON);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get Post: " + e.getMessage());
            return null;
        }
    }


    public static void updatePost(String id,JSONObject postJSON){
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.setKey(postJSON.get("id").toString());
            myObject.addAttribute("id", postJSON.get("id").toString());
            myObject.addAttribute("user_id", postJSON.get("user_id").toString());
            myObject.addAttribute("caption", postJSON.get("caption").toString());
            myObject.addAttribute("media", postJSON.get("media").toString());
            myObject.addAttribute("likes", postJSON.get("likes").toString());
            myObject.addAttribute("tags", postJSON.get("tags").toString());
            myObject.addAttribute("location", postJSON.get("location").toString());
            myObject.addAttribute("comments", postJSON.get("comments").toString());
            myObject.addAttribute("created_at", postJSON.get("created_at").toString());
            myObject.addAttribute("updated_at", postJSON.get("updated_at").toString());
            myObject.addAttribute("blocked_at", postJSON.get("blocked_at").toString());
            myObject.addAttribute("deleted_at", postJSON.get("deleted_at").toString());
            arangoDB.db(dbName).collection(postsCollectionName).updateDocument(id, myObject);
            System.out.println("Post Updated");
        } catch (ArangoDBException e) {
            System.err.println("Failed to Update Post. " + e.getMessage());
        } catch (JSONException e){
            System.err.println("JSON Post Incorrect format. " + e.getMessage());
        }
    }

    public static void deletePost(String id){
        try {
            arangoDB.db(dbName).collection(postsCollectionName).deleteDocument(id);
            System.out.println("Post Deleted: "+id);
        } catch (ArangoDBException e){
            System.err.println("Post ID does not exist:  "+id+",  "+e.getMessage());
        }
    }



    //BOOKMARKS CRUD
    public static void insertBookmark(JSONObject bookmarkJSON){
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.setKey(bookmarkJSON.get("user_id").toString());
            myObject.addAttribute("user_id", bookmarkJSON.get("user_id").toString());
            myObject.addAttribute("posts_ids", bookmarkJSON.get("posts_ids").toString());
            arangoDB.db(dbName).collection(bookmarksCollectionName).insertDocument(myObject);
            System.out.println("Bookmark inserted");
        } catch (ArangoDBException e) {
            System.err.println("Failed to insert Bookmark. " + e.getMessage());
        } catch (JSONException e){
            System.err.println("JSON Bookmark Incorrect format. " + e.getMessage());
        }
    }

    public static JSONObject getBookmark(String id){
        try {
            BaseDocument bookmarkDoc = arangoDB.db(dbName).collection(bookmarksCollectionName).getDocument(id,
                    BaseDocument.class);
            if(bookmarkDoc == null){
                throw new ArangoDBException("Bookmark with ID: " + id+" Not Found");
            }
            System.out.println("YY: "+bookmarkDoc.getProperties());
            JSONObject bookmarkJSON  = new JSONObject(bookmarkDoc.getProperties());
            System.out.println("XX: "+bookmarkJSON);
            return reformatJSON(bookmarkJSON);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get Bookmark: " + e.getMessage());
            return null;
        }
    }

    public static void updateBookmark(String id,JSONObject bookmarkJSON){
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.setKey(bookmarkJSON.get("user_id").toString());
            myObject.addAttribute("user_id", bookmarkJSON.get("user_id").toString());
            myObject.addAttribute("posts_ids", bookmarkJSON.get("posts_ids").toString());
            arangoDB.db(dbName).collection(bookmarksCollectionName).updateDocument(id, myObject);
            System.out.println("Bookmark Updated");
        } catch (ArangoDBException e) {
            System.err.println("Failed to Update Bookmark. " + e.getMessage());
        } catch (JSONException e){
            System.err.println("JSON Bookmark Incorrect format. " + e.getMessage());
        }
    }

    public static void deleteBookmark(String id){
        try {
            arangoDB.db(dbName).collection(bookmarksCollectionName).deleteDocument(id);
            System.out.println("Bookmark Deleted: "+id);
        } catch (ArangoDBException e){
            System.err.println("Bookmark ID does not exist:  "+id+",  "+e.getMessage());
        }
    }


    //COMMENTS CRUD
    public static void insertCommentonPost(String postID, JSONObject comment){
        JSONObject post = getPost(postID);
        ((JSONArray) post.get("comments")).put(comment);
        updatePost(postID,post);
    }



    private static JSONObject reformatJSON(JSONObject json){
        String openingArray = "\"\\[";
        String closedArray = "]\"";
        String backslash = "\\\\";
        String jsonString = json.toString();
        jsonString = jsonString.replaceAll(openingArray,"\\[")
                                .replaceAll(closedArray, "\\]")
                                .replaceAll(backslash,"");

        return new JSONObject(jsonString);

    }


}