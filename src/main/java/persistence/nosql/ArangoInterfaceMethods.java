package persistence.nosql;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.*;
import com.arangodb.entity.*;
import com.arangodb.model.GraphCreateOptions;
import com.arangodb.util.MapBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import java.io.IOException;
import java.util.*;

import static persistence.sql.Main.openConnection;
import static persistence.sql.users.Main.getAllUsersIds;

public class ArangoInterfaceMethods {

    private static ArangoDB arangoDB = new ArangoDB.Builder().build();
    static String dbName = "InstagramAQL";

    private static final String threadsCollectionName = "Threads";
    private static final String notificationsCollectionName = "Notifications";
    private static final String activitiesCollectionName = "Activities";
    private static final String storiesCollectionName = "Stories";
    private static final String postsCollectionName = "Posts";
    private static final String bookmarksCollectionName = "Bookmarks";
    private static final String userCollectionName = "Users";
    private static final String hashtagCollectionName = "Hashtags";

    private static final String graphUserFollowsCollectionName = "UserFollows";
    private static final String graphUserInteractsCollectionName = "UserInteracts";

    private static final String graphName = "InstagramGraph";


    public static void main(String[] args) throws Exception {
//        arangoDB.db(dbName).drop();
        initializeDB();

//        initializeGraphCollections();
//        String id1 = utilities.Main.generateUUID();
//        String id2 = utilities.Main.generateUUID();
//        String userid1 = utilities.Main.generateUUID();
//        JSONObject obj = new JSONObject();
//        obj.put("id", id1);
//        obj.put("caption", "Taken By Heba EL Gen");
//        obj.put("media", new ArrayList<String>());
//        obj.put("likes", new ArrayList<String>());
//        obj.put("tags", new ArrayList<String>());
//        obj.put("comments", new ArrayList<String>());
//        obj.put("location", "{ name: EspressoLab, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
//        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
//        obj.put("updated_at", new Timestamp(System.currentTimeMillis()));
//        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));
//        obj.put("deleted_at", new Timestamp(System.currentTimeMillis()));
//        ArangoInterfaceMethods.insertPost(obj, userid1);
//        System.out.println(userid1);

//         JSONObject obj2 = new JSONObject();
//         obj.put("id", id2);
//         obj.put("user_id",userid1);
//         obj.put("caption","Taken By MiSO EL Gen");
//         obj.put("media", new ArrayList<String>());
//         obj.put("likes", new ArrayList<String>());
//         obj.put("tags",new ArrayList<String>());
//         obj.put("location","{ name: EspressoLab, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
//         obj.put("created_at",new Timestamp(System.currentTimeMillis()));
//         obj.put("updated_at",new Timestamp(System.currentTimeMillis()));
//         obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));
//         obj.put("deleted_at",new Timestamp(System.currentTimeMillis()));
//         ArangoInterfaceMethods.insertPost(obj);
//         JSONArray result = getPosts(userid1);


    }


    public static void initializeDB() {
        try {
            if (!arangoDB.getDatabases().contains(dbName)) {
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

    public static void closeConnection() {
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
    public static String insertThread(JSONObject threadJSON) {

        try {

            BaseDocument myObject = new BaseDocument();
            myObject.addAttribute("creator_id", threadJSON.get("creator_id").toString());
            myObject.addAttribute("users_ids", threadJSON.get("users_ids").toString());
            myObject.addAttribute("name", threadJSON.get("name").toString());
            myObject.addAttribute("created_at", threadJSON.get("created_at").toString());
            myObject.addAttribute("deleted_at", threadJSON.get("deleted_at").toString());
            myObject.addAttribute("blocked_at", threadJSON.get("blocked_at").toString());
            myObject.addAttribute("messages", threadJSON.get("messages").toString());
            String id = arangoDB.db(dbName).collection(threadsCollectionName).insertDocument(threadJSON.toString()).getKey();
            System.out.println("Thread inserted");
            return id;
        } catch (ArangoDBException e) {
            System.err.println("Failed to insert thread. " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("JSON Thread Incorrect format. " + e.getMessage());
        }
        return null;
    }

    public static JSONObject getThread(String id) {
        try {
            BaseDocument threadDoc = arangoDB.db(dbName).collection(threadsCollectionName).getDocument(id,
                    BaseDocument.class);
            if (threadDoc == null) {
                throw new ArangoDBException("Thread with ID: " + id + " Not Found");
            }
            JSONObject threadJSON = new JSONObject(threadDoc.getProperties());
            return reformatJSON(threadJSON);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get Thread: " + e.getMessage());
            return null;
        }
    }

    public static void updateThread(String id, JSONObject threadJSON) {
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.addAttribute("creator_id", threadJSON.get("creator_id").toString());
            myObject.addAttribute("users_ids", threadJSON.get("users_ids").toString());
            myObject.addAttribute("name", threadJSON.get("name").toString());
            myObject.addAttribute("created_at", threadJSON.get("created_at").toString());
            myObject.addAttribute("deleted_at", threadJSON.get("deleted_at").toString());
            myObject.addAttribute("blocked_at", threadJSON.get("blocked_at").toString());
            myObject.addAttribute("messages", threadJSON.get("messages").toString());
            arangoDB.db(dbName).collection(threadsCollectionName).updateDocument(id, threadJSON.toString());
            System.out.println("Thread Updated");
        } catch (ArangoDBException e) {
            System.err.println("Failed to Update Thread. " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("JSON Thread Incorrect format. " + e.getMessage());
        }
    }

    public static void deleteThread(String id) {
        try {
            arangoDB.db(dbName).collection(threadsCollectionName).deleteDocument(id);
            System.out.println("Thread Deleted: " + id);
        } catch (ArangoDBException e) {
            System.err.println("Thread ID does not exist:  " + id + ",  " + e.getMessage());
        }
    }


    //NOTIFICATION CRUD
    public static String insertNotification(JSONObject notificationJSON) {
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.addAttribute("activity_type", notificationJSON.get("activity_type").toString());
            myObject.addAttribute("receiver_id", notificationJSON.get("receiver_id").toString());
            myObject.addAttribute("sender_id", notificationJSON.get("sender_id").toString());
            myObject.addAttribute("created_at", notificationJSON.get("created_at").toString());
            myObject.addAttribute("blocked_at", notificationJSON.get("blocked_at").toString());
            String id = arangoDB.db(dbName).collection(notificationsCollectionName).insertDocument(notificationJSON.toString()).getKey();
            System.out.println("Notification inserted");
            return id;
        } catch (ArangoDBException e) {
            System.err.println("Failed to insert Notification. " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("JSON Notification Incorrect format. " + e.getMessage());
        }
        return null;
    }

    public static JSONObject getNotification(String id) {
        try {
            BaseDocument notificationDoc = arangoDB.db(dbName).collection(notificationsCollectionName).getDocument(id,
                    BaseDocument.class);
            if (notificationDoc == null) {
                throw new ArangoDBException("Notification with ID: " + id + " Not Found");
            }
            JSONObject notificationJSON = new JSONObject(notificationDoc.getProperties());
            return reformatJSON(notificationJSON);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get Notification: " + e.getMessage());
            return null;
        }
    }

    public static void updateNotification(String id, JSONObject notificationJSON) {
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.addAttribute("activity_type", notificationJSON.get("activity_type").toString());
            myObject.addAttribute("receiver_id", notificationJSON.get("receiver_id").toString());
            myObject.addAttribute("sender_id", notificationJSON.get("sender_id").toString());
            myObject.addAttribute("created_at", notificationJSON.get("created_at").toString());
            myObject.addAttribute("blocked_at", notificationJSON.get("blocked_at").toString());
            arangoDB.db(dbName).collection(notificationsCollectionName).updateDocument(id, notificationJSON.toString());
            System.out.println("Notification Updated");
        } catch (ArangoDBException e) {
            System.err.println("Failed to Update Notification. " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("JSON Notification Incorrect format. " + e.getMessage());
        }
    }

    public static void deleteNotification(String id) {
        try {
            arangoDB.db(dbName).collection(notificationsCollectionName).deleteDocument(id);
            System.out.println("Notification Deleted: " + id);
        } catch (ArangoDBException e) {
            System.err.println("Notification ID does not exist:  " + id + ",  " + e.getMessage());
        }
    }


    //ACTIVITY CRUD
    public static String insertActivity(JSONObject activityJSON) {
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.addAttribute("activity_type", activityJSON.get("activity_type").toString());
            myObject.addAttribute("receiver_id", activityJSON.get("receiver_id").toString());
            myObject.addAttribute("sender_id", activityJSON.get("sender_id").toString());
            myObject.addAttribute("created_at", activityJSON.get("created_at").toString());
            myObject.addAttribute("blocked_at", activityJSON.get("blocked_at").toString());
            String id = arangoDB.db(dbName).collection(activitiesCollectionName).insertDocument(activityJSON.toString()).getKey();
            System.out.println("Activity inserted");
            return id;
        } catch (ArangoDBException e) {
            System.err.println("Failed to insert Activity. " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("JSON Activity Incorrect format. " + e.getMessage());
        }
        return null;
    }

    public static JSONObject getActivity(String id) {
        try {
            BaseDocument activityDoc = arangoDB.db(dbName).collection(activitiesCollectionName).getDocument(id,
                    BaseDocument.class);
            if (activityDoc == null) {
                throw new ArangoDBException("Activity with ID: " + id + " Not Found");
            }
            JSONObject activityJSON = new JSONObject(activityDoc.getProperties());
            return reformatJSON(activityJSON);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get Activity: " + e.getMessage());
            return null;
        }
    }

    public static void updateActivity(String id, JSONObject activityJSON) {
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.addAttribute("activity_type", activityJSON.get("activity_type").toString());
            myObject.addAttribute("receiver_id", activityJSON.get("receiver_id").toString());
            myObject.addAttribute("sender_id", activityJSON.get("sender_id").toString());
            myObject.addAttribute("created_at", activityJSON.get("created_at").toString());
            myObject.addAttribute("blocked_at", activityJSON.get("blocked_at").toString());
            arangoDB.db(dbName).collection(activitiesCollectionName).updateDocument(id, activityJSON.toString());
            System.out.println("Activity Updated");
        } catch (ArangoDBException e) {
            System.err.println("Failed to Update Activity. " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("JSON Activity Incorrect format. " + e.getMessage());
        }
    }

    public static void deleteActivity(String id) {
        try {
            arangoDB.db(dbName).collection(activitiesCollectionName).deleteDocument(id);
            System.out.println("Activity Deleted: " + id);
        } catch (ArangoDBException e) {
            System.err.println("Activity ID does not exist:  " + id + ",  " + e.getMessage());
        }
    }


    //STORY CRUD
    public static String insertStory(JSONObject storyJSON) {
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.addAttribute("user_id", storyJSON.get("user_id").toString());
            myObject.addAttribute("is_featured", storyJSON.get("is_featured").toString());
            myObject.addAttribute("media_id", storyJSON.get("media_id").toString());
            myObject.addAttribute("reports", storyJSON.get("reports").toString());
            myObject.addAttribute("seen_by_users_ids", storyJSON.get("seen_by_users_ids").toString());
            myObject.addAttribute("created_at", storyJSON.get("created_at").toString());
            myObject.addAttribute("deleted_at", storyJSON.get("deleted_at").toString());
            myObject.addAttribute("expired_at", storyJSON.get("expired_at").toString());
            myObject.addAttribute("blocked_at", storyJSON.get("blocked_at").toString());
            String id = arangoDB.db(dbName).collection(storiesCollectionName).insertDocument(storyJSON.toString()).getKey();
            System.out.println("Story inserted");
            return id;
        } catch (ArangoDBException e) {
            System.err.println("Failed to insert Story. " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("JSON Story Incorrect format. " + e.getMessage());
        }
        return null;
    }

    public static JSONObject getStory(String id) {
        try {
            BaseDocument storyDoc = arangoDB.db(dbName).collection(storiesCollectionName).getDocument(id,
                    BaseDocument.class);
            if (storyDoc == null) {
                throw new ArangoDBException("Story with ID: " + id + " Not Found");
            }
            JSONObject storyJSON = new JSONObject(storyDoc.getProperties());
            return reformatJSON(storyJSON);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get Story: " + e.getMessage());
            return null;
        }
    }

    public static boolean updateStory(String id, JSONObject storyJSON) {
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.addAttribute("user_id", storyJSON.get("user_id").toString());
            myObject.addAttribute("is_featured", storyJSON.get("is_featured").toString());
            myObject.addAttribute("media_id", storyJSON.get("media_id").toString());
            myObject.addAttribute("reports", storyJSON.get("reports").toString());
            myObject.addAttribute("seen_by_users_ids", storyJSON.get("seen_by_users_ids").toString());
            myObject.addAttribute("created_at", storyJSON.get("created_at").toString());
            myObject.addAttribute("deleted_at", storyJSON.get("deleted_at").toString());
            myObject.addAttribute("expired_at", storyJSON.get("expired_at").toString());
            myObject.addAttribute("blocked_at", storyJSON.get("blocked_at").toString());
            arangoDB.db(dbName).collection(storiesCollectionName).updateDocument(id, storyJSON.toString());
            System.out.println("Story Updated");
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Failed to Update Story. " + e.getMessage());
            return false;
        } catch (JSONException e) {
            System.err.println("JSON Story Incorrect format. " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteStory(String id) {
        try {
            arangoDB.db(dbName).collection(storiesCollectionName).deleteDocument(id);
            System.out.println("Story Deleted: " + id);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Story ID does not exist:  " + id + ",  " + e.getMessage());
            return false;
        }
    }

    public static JSONArray getStories(String user_id) {

        try {
            String dbQuery = "For story in " + storiesCollectionName + " FILTER story.user_id == " + user_id + " RETURN story";
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(dbQuery, null, null, BaseDocument.class);
            JSONArray result = new JSONArray();
            cursor.forEachRemaining(aDocument -> {
                JSONObject postJSON = new JSONObject(aDocument.getProperties());
                result.put(reformatJSON(postJSON));
            });
            return result;
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
            return null;
        }

    }


    //POSTS CRUD
    public static String insertPost(JSONObject postJSON, String userId) throws Exception {
        try {

            BaseDocument myObject = new BaseDocument();
            myObject.addAttribute("user_id", userId);
            myObject.addAttribute("caption", postJSON.get("caption").toString());
            myObject.addAttribute("media", postJSON.get("media"));
            //TODO: @MAGDY location gets inserted in a wrong way (with key "map")
            myObject.addAttribute("location", postJSON.getJSONObject("location"));
            myObject.addAttribute("comments", new ArrayList<>());
            myObject.addAttribute("likes", new ArrayList<>());
            myObject.addAttribute("created_at", new Timestamp(System.currentTimeMillis()));
            myObject.addAttribute("updated_at", null);
            myObject.addAttribute("blocked_at", null);
            myObject.addAttribute("deleted_at", null);
            String id = arangoDB.db(dbName).collection(postsCollectionName).insertDocument(myObject).getKey();
            System.out.println("Post inserted");
            return id;
        } catch (ArangoDBException e) {
            System.err.println("Failed to insert Post. " + e.getMessage());
            return "";
        } catch (JSONException e) {
            throw new Exception(e.getMessage());
        }
//        return null;
    }

    public static JSONObject getPost(String id) throws Exception {
        try {
            BaseDocument postDoc = arangoDB.db(dbName).collection(postsCollectionName).getDocument(id,
                    BaseDocument.class);
            if (postDoc == null) {
                throw new Exception("Post with ID: " + id + " Not Found");
            }
            JSONObject postJSON = new JSONObject(postDoc.getProperties());
            postJSON.put("id", postDoc.getKey());
            return reformatJSON(postJSON);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get Post: " + e.getMessage());
            return null;
        }
    }

    public static JSONArray getPosts(String userId) {
        try {
            String query = "FOR t IN " + postsCollectionName + " FILTER t.user_id == @id RETURN t";
            Map<String, Object> bindVars = new MapBuilder().put("id", userId).get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            JSONArray result = new JSONArray();
            cursor.forEachRemaining(aDocument -> {
                JSONObject postJSON = new JSONObject(aDocument.getProperties());
                postJSON.put("id", aDocument.getKey());
                result.put(postJSON);
            });
            return result;
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
            return null;
        }


    }

    public static void likePost(String postID, String userID) throws Exception {
        JSONObject post = getPost(postID);
        JSONArray likes = (JSONArray) post.get("likes");
        likes.put(userID);
        updatePost(postID, post);
    }

    public static void updatePost(String id, JSONObject postJSON) {
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.addAttribute("user_id", postJSON.get("user_id").toString());
            myObject.addAttribute("caption", postJSON.get("caption").toString());
            myObject.addAttribute("media", postJSON.get("media").toString());
            myObject.addAttribute("likes", postJSON.get("likes").toString());
            myObject.addAttribute("location", postJSON.get("location").toString());
            myObject.addAttribute("comments", postJSON.get("comments").toString());
            myObject.addAttribute("created_at", postJSON.get("created_at").toString());
            myObject.addAttribute("updated_at", new Timestamp(System.currentTimeMillis()));
            if (postJSON.has("blocked_at"))
                myObject.addAttribute("blocked_at", postJSON.get("blocked_at").toString());
            if (postJSON.has("deleted_at"))
                myObject.addAttribute("deleted_at", postJSON.get("deleted_at").toString());
            arangoDB.db(dbName).collection(postsCollectionName).updateDocument(id, postJSON.toString());
            System.out.println("Post Updated");
        } catch (ArangoDBException e) {
            System.err.println("Failed to Update Post. " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("JSON Post Incorrect format. " + e.getMessage());
        }
    }

    public static void deletePost(String id) {
        try {
            arangoDB.db(dbName).collection(postsCollectionName).deleteDocument(id);
            System.out.println("Post Deleted: " + id);
        } catch (ArangoDBException e) {
            System.err.println("Post ID does not exist:  " + id + ",  " + e.getMessage());
        }
    }


    //BOOKMARKS CRUD
    public static String insertBookmark(JSONObject bookmarkJSON) {
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.addAttribute("user_id", bookmarkJSON.get("user_id").toString());
            myObject.addAttribute("posts_ids", bookmarkJSON.get("posts_ids").toString());
            String id = arangoDB.db(dbName).collection(bookmarksCollectionName).insertDocument(bookmarkJSON.toString()).getKey();
            System.out.println("Bookmark inserted");
            return id;
        } catch (ArangoDBException e) {
            System.err.println("Failed to insert Bookmark. " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("JSON Bookmark Incorrect format. " + e.getMessage());
        }
        return null;
    }

    public static JSONObject getBookmark(String id) {
        try {
            BaseDocument bookmarkDoc = arangoDB.db(dbName).collection(bookmarksCollectionName).getDocument(id,
                    BaseDocument.class);
            if (bookmarkDoc == null) {
                throw new ArangoDBException("Bookmark with ID: " + id + " Not Found");
            }
            JSONObject bookmarkJSON = new JSONObject(bookmarkDoc.getProperties());
            return reformatJSON(bookmarkJSON);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get Bookmark: " + e.getMessage());
            return null;
        }
    }

    public static void updateBookmark(String id, JSONObject bookmarkJSON) {
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.addAttribute("user_id", bookmarkJSON.get("user_id").toString());
            myObject.addAttribute("posts_ids", bookmarkJSON.get("posts_ids").toString());
            arangoDB.db(dbName).collection(bookmarksCollectionName).updateDocument(id, bookmarkJSON.toString());
            System.out.println("Bookmark Updated");
        } catch (ArangoDBException e) {
            System.err.println("Failed to Update Bookmark. " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("JSON Bookmark Incorrect format. " + e.getMessage());
        }
    }

    public static void deleteBookmark(String id) {
        try {
            arangoDB.db(dbName).collection(bookmarksCollectionName).deleteDocument(id);
            System.out.println("Bookmark Deleted: " + id);
        } catch (ArangoDBException e) {
            System.err.println("Bookmark ID does not exist:  " + id + ",  " + e.getMessage());
        }
    }


    //COMMENTS CRUD
    public static void insertCommentReply(String commentID, JSONObject commentReply) {

        String dbQuery = "FOR post in Posts LET willUpdateDocument = ( FOR comment IN post.comments FILTER comment.id == '" + commentID + "' LIMIT 1 RETURN 1) FILTER LENGTH(willUpdateDocument) > 0 LET alteredList = ( FOR comment IN post.comments LET newItem = (comment.id == '" + commentID + "' ? merge(comment, { 'comments' : append(comment.comments," + commentReply.toString() + ")}) : comment) RETURN newItem) UPDATE post WITH { comments:  alteredList } IN Posts";
        arangoDB.db(dbName).query(dbQuery, null, null, null);
    }

    public static void insertCommentOnPost(String postID, JSONObject comment) throws Exception {
        JSONObject post = getPost(postID);
        ((JSONArray) post.get("comments")).put(comment);
        updatePost(postID, post);
    }

    public static void initializeGraphCollections() throws IOException {
        Iterator<GraphEntity> graphs = arangoDB.db(dbName).getGraphs().iterator();
        while (graphs.hasNext()) {
            if (graphs.next().getName().equals(graphName)) {
                return;
            }
        }

        openConnection();
        List<String> user_ids = getAllUsersIds();
        closeConnection();
        try {

            Collection<EdgeDefinition> edgeDefinitions = new ArrayList<>();
            EdgeDefinition edgeUserFollows = new EdgeDefinition();

            edgeUserFollows.collection(graphUserFollowsCollectionName);
            edgeUserFollows.from(userCollectionName);
            edgeUserFollows.to(userCollectionName);


            EdgeDefinition edgeUserInteracts = new EdgeDefinition();

            edgeUserInteracts.collection(graphUserInteractsCollectionName);
            edgeUserInteracts.from(userCollectionName);
            edgeUserInteracts.to(hashtagCollectionName);

            edgeDefinitions.add(edgeUserFollows);
            edgeDefinitions.add(edgeUserInteracts);

            GraphCreateOptions options = new GraphCreateOptions();
            options.orphanCollections("dummyOptions");

            arangoDB.db(dbName).createGraph(graphName, edgeDefinitions, options);


            for (int i = 0; i < user_ids.size(); i++) {
                BaseDocument userDocument = new BaseDocument();
                userDocument.setKey(user_ids.get(i));
                arangoDB.db(dbName).graph(graphName).vertexCollection(userCollectionName).insertVertex(userDocument, null);
            }
        } catch (ArangoDBException e) {
            System.err.println("Faild to intilize graph: " + e.getMessage());
            return;
        }


    }

    public static JSONArray getCommentsOnPost(String postID) {
        try {
            BaseDocument postDoc = arangoDB.db(dbName).collection(postsCollectionName).getDocument(postID,
                    BaseDocument.class);
            if (postDoc == null) {
                throw new ArangoDBException("Post with ID: " + postID + " Not Found");
            }
            JSONObject postJSON = new JSONObject(postDoc.getProperties());
            return (JSONArray) reformatJSON(postJSON).get("comments");
        } catch (ArangoDBException e) {
            System.err.println("Failed to get Post: " + e.getMessage());
            return null;
        }
    }


    //MESSAGES CRUD
    public static void insertMessageOnThread(String threadID, JSONObject message) {
        JSONObject post = getThread(threadID);
        ((JSONArray) post.get("messages")).put(message);
        updatePost(threadID, post);
    }

    public static boolean followUser(String followerID, String followedID) {


        BaseDocument followerDoc = new BaseDocument();
        followerDoc.setKey(followerID);

        BaseDocument followedDoc = new BaseDocument();
        followedDoc.setKey(followedID);

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String followerKey = followerID.split("/")[1];
        String followedKey = followedID.split("/")[1];
        edge.setKey(followerKey + followedKey);
        edge.setFrom(followerID);
        edge.setTo(followedID);

        try {
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserFollowsCollectionName);

            edgecollection.insertEdge(edge, null);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Edge Insertion Failed In Graph: " + e.getMessage());
            return false;
        }
    }


    public static boolean followHashtag(String userID, String hashtagName) {


        BaseDocument followerDoc = new BaseDocument();
        followerDoc.setKey(userID);

        BaseDocument followedDoc = new BaseDocument();
        followedDoc.setKey(hashtagName);

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String userIdKey = userID.split("/")[1];
        String hashtagNameKey = hashtagName.split("/")[1];
        edge.setKey(userIdKey + hashtagNameKey);
        edge.setFrom(userID);
        edge.setTo(hashtagName);

        try {
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserInteractsCollectionName);
            edgecollection.insertEdge(edge, null);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Edge Insertion Failed In Graph: " + e.getMessage());
            return false;
        }
    }

    public static boolean unFollowUser(String followerID, String followedID) {
        try {
            String followerKey = followerID.split("/")[1];
            String followedKey = followedID.split("/")[1];
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserFollowsCollectionName);
            edgecollection.deleteEdge(followerKey + followedKey);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Edge Deletion Failed In Graph: " + e.getMessage());
            return false;
        }


    }

    public static boolean unFolllowHashtag(String userID, String hashtagName) {
        try {
            String userIDKey = userID.split("/")[1];
            String hashtagNameKey = hashtagName.split("/")[1];
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserInteractsCollectionName);
            edgecollection.deleteEdge(userIDKey + hashtagNameKey);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Edge Deletion Failed In Graph: " + e.getMessage());
            return false;
        }


    }

    public static boolean makeUserNode(String userID) {
        try {
            BaseDocument userDocument = new BaseDocument();
            userDocument.setKey(userID);
            arangoDB.db(dbName).graph(graphName).vertexCollection(userCollectionName).insertVertex(userDocument, null);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Failed to initialize a node for user In Graph: " + e.getMessage());
            return false;
        }

    }


    public static boolean makeHashtagNode(String hashtagName) {
        try {
            BaseDocument hashtagDocument = new BaseDocument();
            hashtagDocument.setKey(hashtagName);
            arangoDB.db(dbName).graph(graphName).vertexCollection(hashtagCollectionName).insertVertex(hashtagDocument, null);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Failed to initialize a node for hashtag In Graph: " + e.getMessage());
            return false;
        }

    }


    public static boolean isFollowing(String userID, String followingID) {
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserFollowsCollectionName);
        String userKey = userID.split("/")[1];
        String followingKey = followingID.split("/")[1];
        BaseEdgeDocument edgeDoc = edgecollection.getEdge(userKey + followingKey, BaseEdgeDocument.class);
        if (edgeDoc == null) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean isInteracting(String userID, String hashtagName) {
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserInteractsCollectionName);
        String userKey = userID.split("/")[1];
        String hashtagKey = hashtagName.split("/")[1];
        BaseEdgeDocument edgeDoc = edgecollection.getEdge(userKey + hashtagKey, BaseEdgeDocument.class);
        if (edgeDoc == null) {
            return false;
        } else {
            return true;
        }

    }


    //    public static boolean removeUserNode(String userID){
//        try{
//            arangoDB.db(dbName).graph(graphName).vertexCollection(userCollectionName).deleteVertex(userID);
//            return true;
//        }
//        catch(ArangoDBException e){
//            System.err.println("Failed to delete a node for user In Graph: " + e.getMessage());
//            return false;
//        }
//
//    }

//    public static boolean removeHashtagNode(String hashtagName){
//        try{
//            arangoDB.db(dbName).graph(graphName).vertexCollection(hashtagCollectionName).deleteVertex(hashtagName);
//            return true;
//        }
//        catch(ArangoDBException e){
//            System.err.println("Failed to delete a node for hashtag In Graph: " + e.getMessage());
//            return false;
//        }
//
//    }

    public static ArrayList<String> getAllfollowingIDs(String userID) {
        try {
            ArrayList<String> IDs = new ArrayList<>();
            String query = "FOR vertex IN OUTBOUND \"" + userID + "\" " + graphUserFollowsCollectionName + " RETURN vertex ";
            System.out.println(query);
            Map<String, Object> bindVars = new MapBuilder().get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                IDs.add(aDocument.getKey());
                System.out.println("ID following: " + aDocument.getKey());
            });
            return IDs;
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
            return null;
        }


    }

    public static ArrayList<String> getAllFollowingHashtags(String userID) {
        try {
            ArrayList<String> HashtagNames = new ArrayList<>();
            String query = "FOR vertex IN OUTBOUND \"" + userID + "\" " + graphUserInteractsCollectionName + " RETURN vertex ";
            System.out.println(query);
            Map<String, Object> bindVars = new MapBuilder().get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                HashtagNames.add(aDocument.getKey());
                System.out.println("Hashtag following: " + aDocument.getKey());
            });
            return HashtagNames;
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
            return null;
        }


    }

    public static ArrayList<String> getAllfollowersIDs(String userID) {
        try {
            ArrayList<String> IDs = new ArrayList<>();
            String query = "FOR vertex IN INBOUND \"" + userID + "\" " + graphUserFollowsCollectionName + " RETURN vertex ";
            System.out.println(query);
            Map<String, Object> bindVars = new MapBuilder().get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                IDs.add(aDocument.getKey());
                System.out.println("ID follower: " + aDocument.getKey());
            });
            return IDs;
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
            return null;
        }

    }

    public static ArrayList<String> getAllHashtagFollowers(String hashtagName) {
        try {
            ArrayList<String> IDs = new ArrayList<>();
            String query = "FOR vertex IN INBOUND \"" + hashtagName + "\" " + graphUserInteractsCollectionName + " RETURN vertex ";
            System.out.println(query);
            Map<String, Object> bindVars = new MapBuilder().get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                IDs.add(aDocument.getKey());
                System.out.println("ID follower: " + aDocument.getKey());
            });
            return IDs;
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
            return null;
        }

    }


    private static JSONObject reformatJSON(JSONObject json) {
        String openingArray = "\"\\[";
        String closedArray = "]\"";
        String backslash = "\\\\";
        String jsonString = json.toString();
        jsonString = jsonString.replaceAll(openingArray, "\\[")
                .replaceAll(closedArray, "\\]")
                .replaceAll(backslash, "");

        return new JSONObject(jsonString);

    }


}
