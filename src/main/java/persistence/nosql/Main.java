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
import org.javalite.http.Post;
import persistence.nosql.Datatypes.*;
import persistence.nosql.Datatypes.Thread;

public class Main {

    protected static ArangoDB arangoDB;
    static final String dbName =  "InstagramAQL";

    static final String messagesCollectionName = "Messages";
    static final  String threadsCollectionName = "Threads";
    static final String postActivitiesCollectionName = "PostActivities";
    static final String userActivitiesCollectionName = "UserActivities";

    static final String notificationsCollectionName = "Notifications";
    static final String ActivitiesCollectionName = "Activities";
    static final String hashtagsCollectionName = "Hashtags";
    static final String commentsCollectionName = "Comments";

    static final String storiesCollectionName = "Stories";

    public static void main(String[]args) {
        arangoDB = new ArangoDB.Builder().build();
        try {
            //if()
            //arangoDB.db(dbName);
            arangoDB.db(dbName).drop();
            arangoDB.createDatabase(dbName);
            System.out.println("Database created: " + dbName);

        } catch (ArangoDBException e) {
            System.err.println("Failed to create database: " + dbName + "; " + e.getMessage());
        }

        try {
            CollectionEntity messagesCollection = arangoDB.db(dbName).createCollection(messagesCollectionName);
            System.out.println("Collection created: " + messagesCollection.getName());

            CollectionEntity threadsCollection = arangoDB.db(dbName).createCollection(threadsCollectionName);
            System.out.println("Collection created: " + threadsCollection.getName());

            CollectionEntity postActivitiesCollection = arangoDB.db(dbName).createCollection(postActivitiesCollectionName);
            System.out.println("Collection created: " + postActivitiesCollection.getName());

            CollectionEntity userActivitiesCollection = arangoDB.db(dbName).createCollection(userActivitiesCollectionName);
            System.out.println("Collection created: " + userActivitiesCollection.getName());

            CollectionEntity notificationsCollection = arangoDB.db(dbName).createCollection(notificationsCollectionName);
            System.out.println("Collection created: " + notificationsCollection.getName());

            CollectionEntity ActivitiesCollection = arangoDB.db(dbName).createCollection(ActivitiesCollectionName);
            System.out.println("Collection created: " + ActivitiesCollection.getName());

            CollectionEntity hashtagsCollection = arangoDB.db(dbName).createCollection(hashtagsCollectionName);
            System.out.println("Collection created: " + hashtagsCollection.getName());

            CollectionEntity commentsCollection = arangoDB.db(dbName).createCollection(commentsCollectionName);
            System.out.println("Collection created: " + commentsCollection.getName());

            CollectionEntity storiesCollection = arangoDB.db(dbName).createCollection(storiesCollectionName);
            System.out.println("Collection created: " + storiesCollection.getName());

            Message x = new Message("Hello",UUID.randomUUID(),UUID.randomUUID());
            UUID tst = x.getId();
            insertMessage(x);
            Message returned = getMessage(tst);
            System.out.println("R: "+returned);


        } catch (ArangoDBException e) {
            System.err.println("Failed to create collections: " + e.getMessage());
        }


//
//        WRITE
//        UUID x = UUID.randomUUID();
//        BaseDocument myObject = new BaseDocument();
//        myObject.setKey("myKey");
//        myObject.addAttribute("a", x);
//        myObject.addAttribute("b", 42);
//        try {
//            arangoDB.db(dbName).collection(storiesCollectionName).insertDocument(myObject);
//            System.out.println("Document created");
//            System.out.println("COUNT: "+arangoDB.db(dbName).collection(storiesCollectionName).count().getCount());
//            System.out.println("documentget: "+arangoDB.db(dbName).collection(storiesCollectionName).getDocument("myKey",BaseDocument.class).getProperties());
//        } catch (ArangoDBException e) {
//            System.err.println("Failed to create document. " + e.getMessage());
//        }
//
//
//        //READ1
//        try {
//            BaseDocument myDocument = arangoDB.db(dbName).collection(collectionName).getDocument("myKey",
//                    BaseDocument.class);
//            System.out.println("Key: " + myDocument.getKey());
//            System.out.println("Attribute a: " + myDocument.getAttribute("a"));
//            System.out.println("Attribute b: " + myDocument.getAttribute("b"));
//        } catch (ArangoDBException e) {
//            System.err.println("Failed to get document: myKey; " + e.getMessage());
//        }
//
//
//        //READ2
//        try {
//            VPackSlice myDocument = arangoDB.db(dbName).collection(collectionName).getDocument("myKey",
//                    VPackSlice.class);
//            System.out.println("Key: " + myDocument.get("_key").getAsString());
//            System.out.println("Attribute a: " + myDocument.get("a").getAsString());
//            System.out.println("Attribute b: " + myDocument.get("b").getAsInt());
//        } catch (ArangoDBException | VPackException e) {
//            System.err.println("Failed to get document: myKey; " + e.getMessage());
//        }
//
//
//        //UPDATE
//        myObject.addAttribute("c", "Bar");
//        try {
//            arangoDB.db(dbName).collection(collectionName).updateDocument("myKey", myObject);
//        } catch (ArangoDBException e) {
//            System.err.println("Failed to update document. " + e.getMessage());
//        }
//
//
//        //READ 3
//        try {
//            ArrayList<String> x = new ArrayList<String>() ;
//            x.add("myKey");
//            System.out.println("_________________");
//            System.out.println( arangoDB.db(dbName).collection(collectionName).getDocuments(x,BaseDocument.class).getDocuments().toString());
////            System.out.println("Key: " + myUpdatedDocument.getKey());
////            System.out.println("Attribute a: " + myUpdatedDocument.getAttribute("a"));
////            System.out.println("Attribute b: " + myUpdatedDocument.getAttribute("b"));
////            System.out.println("Attribute c: " + myUpdatedDocument.getAttribute("c"));
//        } catch (ArangoDBException e) {
//            System.err.println("Failed to get document: myKey; " + e.getMessage());
//        }
//
//
//        //DELETE
//        try {
//            arangoDB.db(dbName).collection(collectionName).deleteDocument("myKey");
//        } catch (ArangoDBException e) {
//            System.err.println("Failed to delete document. " + e.getMessage());
//        }
//
//
//
//        //QUERY INSERT
//        ArangoCollection collection = arangoDB.db(dbName).collection(collectionName);
//        for (int i = 0; i < 10; i++) {
//            BaseDocument value = new BaseDocument();
//            value.setKey(String.valueOf(i));
//            value.addAttribute("name", "Homer");
//            value.addAttribute("number", "Homer"+i);
//            collection.insertDocument(value);
//        }
//
//
//        //QUERY READ
//        try {
//            String query = "FOR t IN firstCollection FILTER t.name == @name RETURN t";
//            Map<String, Object> bindVars = new MapBuilder().put("name", "Homer").get();
//            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
//                    BaseDocument.class);
//            cursor.forEachRemaining(aDocument -> {
//                System.out.println("Key: " + aDocument.getAttribute("number"));
//            });
//        } catch (ArangoDBException e) {
//            System.err.println("Failed to execute query. " + e.getMessage());
//        }
//
//
//        try {
//            String query = "FOR t IN firstCollection FILTER t.name == @name "
//                    + "REMOVE t IN firstCollection LET removed = OLD RETURN removed";
//            Map<String, Object> bindVars = new MapBuilder().put("name", "Homer").get();
//            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
//                    BaseDocument.class);
//            cursor.forEachRemaining(aDocument -> {
//                System.out.println("Removed document " + aDocument.getKey());
//            });
//        } catch (ArangoDBException e) {
//            System.err.println("Failed to execute query. " + e.getMessage());
//        }




    }



    //MESSAGE CRUD
    public static void insertMessage(Message message){


        BaseDocument myObject = new BaseDocument();
        myObject.setKey(message.getId().toString());
        myObject.addAttribute("text", message.getText());
        myObject.addAttribute("user_id", message.getUserId());
        myObject.addAttribute("created_at", message.getCreatedAt());
        myObject.addAttribute("deleted_at", "");
        myObject.addAttribute("blocked_at", "");
        myObject.addAttribute("liker_ids", message.getLikerIds());
        myObject.addAttribute("media_id", message.getMediaId());
        try {
            arangoDB.db(dbName).collection(messagesCollectionName).insertDocument(myObject);
            System.out.println("Message inserted");
        } catch (ArangoDBException e) {
            System.err.println("Failed to insert message. " + e.getMessage());
        }

    }

    public static Message getMessage(UUID id){

        try {
            BaseDocument messageDoc = arangoDB.db(dbName).collection(messagesCollectionName).getDocument(id.toString(),
                    BaseDocument.class);
//            System.out.println("Key: " + myDocument.getKey());
//            System.out.println("Attribute a: " + myDocument.getAttribute("a"));
//            System.out.println("Attribute b: " + myDocument.getAttribute("b"));

            System.out.println(messageDoc.getAttribute("created_at").getClass());
            return new Message(UUID.fromString(messageDoc.getKey())    ,
                    (String)messageDoc.getAttribute("text"),
                    UUID.fromString((String)messageDoc.getAttribute("user_id")),
                    StringToTimeStamp((String)messageDoc.getAttribute("created_at")),
                    StringToTimeStamp((String)messageDoc.getAttribute("deleted_at")),
                    StringToTimeStamp((String)messageDoc.getAttribute("blocked_at")),
                    (ArrayList<UUID>) messageDoc.getAttribute("liker_ids"),
                     (UUID.fromString((String)messageDoc.getAttribute("media_id"))));
        } catch (Exception e) {
            System.err.println("Failed to get Message: " + e.getMessage());
            return null;
        }


    }


    public static void updateMessage(UUID id,Message message){

    }

    public static void deleteMessage(UUID id){

    }



    //Thread CRUD
    public static void insertThread(Thread thread){


    }

    public static Thread getThread(UUID id){

        return null;
    }

    public static void updateThread(UUID id,Thread thread){

    }

    public static void deleteThread(UUID id){

    }




    //ACTIVITYTYPE CRUD
    public static void insertActivityType(ActivityType activityType){


    }

    public static Post getActivityType(UUID id){

        return null;
    }

    public static void updateActivityType(UUID id,ActivityType activityType){

    }

    public static void deleteActivityType(UUID id){

    }




    //NOTIFICATION CRUD
    public static void insertNotification(Notification notification){

    }

    public static Notification getNotification(UUID id){
        return null;
    }

    public static void updateNotification(UUID id,Notification notification){

    }

    public static void deleteNotification(UUID id){

    }



    //ACTIVITY CRUD
    public static void insertActivity(Activity ctivity){

    }

    public static Activity getActivity(UUID id){

        return null;
    }

    public static void updateActivity(UUID id,Activity activity){

    }

    public static void deleteActivity(UUID id){

    }



    //HASHTAG CRUD
    public static void insertHashtag(Hashtag hashtag){

    }

    public static Hashtag getHashtag(String text){
        return null;
    }

    public static void updateHashtag(String text,Hashtag hashtag){

    }

    public static void deleteHashtag(String text){

    }




    //COMMENT CRUD
    public static void insertComment(Comment comment){

    }

    public static Comment getComment(UUID id){
        return null;
    }

    public static void updateComment(UUID id,Comment comment){

    }

    public static void deleteComment(UUID id){

    }



    //STORY CRUD
    public static void insertStory(Story story){

    }

    public static Story getStory(UUID id){

        return null;
    }

    public static void updateStory(UUID id,Story story){

    }

    public static void deleteStory(UUID id){

    }


    public static Timestamp StringToTimeStamp(String time) throws Exception{

        if(time.equals("")){
            return null;
        }
        time = time.replaceAll("T"," ");
        time = time.replaceAll("Z","");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date parsedDate = dateFormat.parse(time);
        return new java.sql.Timestamp(parsedDate.getTime());
    }


}