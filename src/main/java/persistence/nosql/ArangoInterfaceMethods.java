package persistence.nosql;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.model.AqlQueryOptions;
import com.arangodb.model.CollectionCreateOptions;
import com.arangodb.util.MapBuilder;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.sun.deploy.util.Base64Wrapper;
import org.javalite.activejdbc.Base;
import org.javalite.http.Post;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import persistence.nosql.Datatypes.*;
import persistence.nosql.Datatypes.Thread;

public class ArangoInterfaceMethods {

    protected static ArangoDB arangoDB = new ArangoDB.Builder().build();;
    static String dbName =  "InstagramAQL";

    //static final String messagesCollectionName = "Messages";
    static final String threadsCollectionName = "Threads";
    //static final String postActivitiesCollectionName = "PostActivities";
    //static final String userActivitiesCollectionName = "UserActivities";
    static final String notificationsCollectionName = "Notifications";
    static final String activitiesCollectionName = "Activities";
//    static final String hashtagsCollectionName = "Hashtags";
    //static final String commentsCollectionName = "Comments";
    static final String storiesCollectionName = "Stories";
    static final String postsCollectionName = "Posts";
    static final String bookmarksCollectionName = "Bookmarks";



    public static void main(String[]args) {
        try {
            if(arangoDB.getDatabases().contains(dbName)){
                arangoDB.db(dbName).drop();
            }
            arangoDB.createDatabase(dbName);
            System.out.println("Database created: " + dbName);

        } catch (ArangoDBException e) {
            System.err.println("Failed to create database: " + dbName + "; " + e.getMessage());
        }

        try {
//            CollectionEntity messagesCollection = arangoDB.db(dbName).createCollection(messagesCollectionName);
//            System.out.println("Collection created: " + messagesCollection.getName());

            CollectionEntity threadsCollection = arangoDB.db(dbName).createCollection(threadsCollectionName);
            System.out.println("Collection created: " + threadsCollection.getName());

//            CollectionEntity postActivitiesCollection = arangoDB.db(dbName).createCollection(postActivitiesCollectionName);
//            System.out.println("Collection created: " + postActivitiesCollection.getName());

//            CollectionEntity userActivitiesCollection = arangoDB.db(dbName).createCollection(userActivitiesCollectionName);
//            System.out.println("Collection created: " + userActivitiesCollection.getName());

            CollectionEntity notificationsCollection = arangoDB.db(dbName).createCollection(notificationsCollectionName);
            System.out.println("Collection created: " + notificationsCollection.getName());

            CollectionEntity ActivitiesCollection = arangoDB.db(dbName).createCollection(activitiesCollectionName);
            System.out.println("Collection created: " + ActivitiesCollection.getName());

//            CollectionEntity hashtagsCollection = arangoDB.db(dbName).createCollection(hashtagsCollectionName);
//            System.out.println("Collection created: " + hashtagsCollection.getName());

//            CollectionEntity commentsCollection = arangoDB.db(dbName).createCollection(commentsCollectionName);
//            System.out.println("Collection created: " + commentsCollection.getName());

            CollectionEntity storiesCollection = arangoDB.db(dbName).createCollection(storiesCollectionName);
            System.out.println("Collection created: " + storiesCollection.getName());

            CollectionEntity postsCollection = arangoDB.db(dbName).createCollection(postsCollectionName);
            System.out.println("Collection created: " + postsCollection.getName());

            CollectionEntity bookmarksCollection = arangoDB.db(dbName).createCollection(bookmarksCollectionName);
            System.out.println("Collection created: " + bookmarksCollection.getName());

//            Message x = new Message("Hello",UUID.randomUUID(),UUID.randomUUID());
//            UUID tst = x.getId();
//            String msgID  = utilities.Main.generateUUID();
//            JSONObject messageObj = new JSONObject();
//            messageObj.put("id", msgID);
//            messageObj.put("user_id",utilities.Main.generateUUID());
//            messageObj.put("text","Hello");
//            messageObj.put("created_at", new Timestamp(System.currentTimeMillis()));
//            messageObj.put("deleted_at",new Timestamp(System.currentTimeMillis()));
//            messageObj.put("blocked_at",new Timestamp(System.currentTimeMillis()));
//            messageObj.put("liker_ids",new ArrayList<String>());
//            messageObj.put("media_id",utilities.Main.generateUUID());
//
//            JSONObject updatedMessageObj = new JSONObject();
//            updatedMessageObj.put("id", msgID);
//            updatedMessageObj.put("user_id",utilities.Main.generateUUID());
//            updatedMessageObj.put("text","MISOOOOo");
//            updatedMessageObj.put("created_at", new Timestamp(System.currentTimeMillis()));
//            updatedMessageObj.put("deleted_at",new Timestamp(System.currentTimeMillis()));
//            updatedMessageObj.put("blocked_at",new Timestamp(System.currentTimeMillis()));
//            updatedMessageObj.put("liker_ids",new ArrayList<String>());
//            updatedMessageObj.put("media_id",utilities.Main.generateUUID());
//
//
//            insertMessage(messageObj);
//            getMessage(msgID);
//            updateMessage(msgID, updatedMessageObj);
//            getMessage(msgID);
//            deleteMessage(msgID);
//            getMessage(msgID);


        } catch (ArangoDBException e) {
            System.err.println("Failed to create collections: " + e.getMessage());
        }




    }



    //MESSAGE CRUD
//    public static void insertMessage(JSONObject messageJSON){
//
//        try {
//            BaseDocument myObject = new BaseDocument();
//            myObject.setKey(messageJSON.get("id").toString());
//            myObject.addAttribute("text", messageJSON.get("text").toString());
//            myObject.addAttribute("user_id", messageJSON.get("user_id").toString());
//            myObject.addAttribute("created_at", messageJSON.get("created_at").toString());
//            myObject.addAttribute("deleted_at", messageJSON.get("deleted_at").toString());
//            myObject.addAttribute("blocked_at", messageJSON.get("blocked_at").toString());
//            myObject.addAttribute("liker_ids", messageJSON.get("liker_ids").toString());
//            myObject.addAttribute("media_id", messageJSON.get("media_id").toString());
//            arangoDB.db(dbName).collection(messagesCollectionName).insertDocument(myObject);
//            System.out.println("Message inserted");
//        } catch (ArangoDBException e) {
//            System.err.println("Failed to insert message. " + e.getMessage());
//        } catch (JSONException e){
//            System.err.println("JSON Message Incorrect format. " + e.getMessage());
//        }
//
//    }
//
//    public static JSONObject getMessage(String id){
//
//        try {
//            BaseDocument messageDoc = arangoDB.db(dbName).collection(messagesCollectionName).getDocument( id,
//                    BaseDocument.class);
//            if(messageDoc == null){
//                throw new ArangoDBException("Message with ID: " + id+" Not Found");
//            }
//            JSONObject messageJSON = new JSONObject(messageDoc);
//            System.out.println(messageJSON);
//            return new JSONObject(messageJSON);
//        } catch (ArangoDBException e) {
//            System.err.println("Failed to get Message: " + e.getMessage());
//            return null;
//        }
//
//
//    }
//
//    public static void updateMessage(String id,JSONObject messageJSON){
//        try {
//            BaseDocument myObject = new BaseDocument();
//            myObject.setKey(messageJSON.get("id").toString());
//            myObject.addAttribute("text", messageJSON.get("text").toString());
//            myObject.addAttribute("user_id", messageJSON.get("user_id").toString());
//            myObject.addAttribute("created_at", messageJSON.get("created_at").toString());
//            myObject.addAttribute("deleted_at", messageJSON.get("deleted_at").toString());
//            myObject.addAttribute("blocked_at", messageJSON.get("blocked_at").toString());
//            myObject.addAttribute("liker_ids", messageJSON.get("liker_ids").toString());
//            myObject.addAttribute("media_id", messageJSON.get("media_id").toString());
//            arangoDB.db(dbName).collection(messagesCollectionName).updateDocument(id, myObject);
//            System.out.println("Message Updated");
//        } catch (ArangoDBException e) {
//            System.err.println("Failed to Update message. " + e.getMessage());
//        } catch (JSONException e){
//            System.err.println("JSON Message Incorrect format. " + e.getMessage());
//        }
//
//    }
//
//    public static void deleteMessage(String id){
//        try {
//            arangoDB.db(dbName).collection(messagesCollectionName).deleteDocument(id);
//            System.out.println("Message Deleted: "+id);
//        } catch (ArangoDBException e){
//            System.err.println("MessageID does not exist:  "+id+",  "+e.getMessage());
//        }
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


//    //ACTIVITYTYPE CRUD
//    public static void insertActivityType(JSONObject activityTypeJSON){
//
//    }
//
//    public static Post getActivityType(UUID id){
//
//        return null;
//    }
//
//    public static void updateActivityType(UUID id,ActivityType activityType){
//
//    }
//
//    public static void deleteActivityType(UUID id){
//
//    }


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


//
//    //HASHTAG CRUD
//    public static void insertHashtag(Hashtag hashtag){
//
//    }
//
//    public static Hashtag getHashtag(String text){
//        return null;
//    }
//
//    public static void updateHashtag(String text,Hashtag hashtag){
//
//    }
//
//    public static void deleteHashtag(String text){
//
//    }
//
//
//    //COMMENT CRUD
//    public static void insertComment(Comment comment){
//
//    }
//
//    public static Comment getComment(UUID id){
//        return null;
//    }
//
//    public static void updateComment(UUID id,Comment comment){
//
//    }
//
//    public static void deleteComment(UUID id){
//
//    }
//


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
            myObject.addAttribute("id", bookmarkJSON.get("id").toString());
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
            JSONObject bookmarkJSON  = new JSONObject(bookmarkDoc.getProperties());
            System.out.println("OUT:  "+new JSONObject(bookmarkJSON));
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
            myObject.addAttribute("id", bookmarkJSON.get("id").toString());
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


    public static JSONObject reformatJSON(JSONObject json){
        System.out.println("____________________ "+ json.toString());
        String openingArray = "\"\\[";
        String closedArray = "\\]\"";
        String jsonString = json.toString();
        jsonString = jsonString.replaceAll(openingArray,"[").replaceAll(closedArray, "]");
        JSONObject newSimpleJSON = new JSONObject(jsonString);
        System.out.println("____________________ "+ newSimpleJSON.toString());
        return newSimpleJSON;



    }


}