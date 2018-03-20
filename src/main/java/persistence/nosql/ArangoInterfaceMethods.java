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
import static utilities.Main.readPropertiesFile;

public class ArangoInterfaceMethods {
    private static Properties properties;

    static {
        try {
            properties = readPropertiesFile("src/main/resources/arango.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArangoDB arangoDB= new ArangoDB.Builder().host(properties.getProperty("host"), Integer.parseInt(properties.getProperty("port"))).build();
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
    private static final String graphUserTaggedCollectionName = "UserTagged";
    private static final String graphPostTaggedCollectionName = "PostTagged";


    private static final String graphName = "InstagramGraph";

    public ArangoInterfaceMethods() throws IOException {
    }


    public static void main(String[] args) throws Exception {
        //  arangoDB.db(dbName).drop();
        initializeDB();
        initializeGraphCollections();
        closeConnection();

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
            myObject.addAttribute("user_id", storyJSON.getString("user_id").toString());
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

    public static JSONArray getStoriesForUser(String user_id) {
        try {
            String dbQuery = "For story in " + storiesCollectionName + " FILTER story.user_id == \""+ user_id + "\" RETURN story";
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

    public static JSONArray getFriendsStories(String userID){
        JSONArray resultStories = new JSONArray();
        ArrayList<String> friends = getAllfollowingIDs(""+ userID);
        JSONArray friendStories;
        for(int  i =0 ; i< friends.size();i++){
            friendStories = getStoriesForUser(friends.get(i));
            JSONObject userStories = new JSONObject();
            userStories.put("user_id",friends.get(i));
            userStories.put("stories",friendStories);
            if(friendStories.length() != 0){
                resultStories.put(userStories);
            }
        }
        return resultStories;
    }

    public static JSONArray getNotifications(String user_id, int start, int limit) {

        try {
            String dbQuery = "For notification in " + notificationsCollectionName
                    + " FILTER notification.receiver_id == " + "'"+user_id+"'"
                    + " SORT notification.created_at"
                    + " Limit "+ start + ", " + limit
                    + " RETURN notification";
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

    public static JSONArray getActivities(ArrayList<String> followings, int start, int limit) {

        try {
            String dbQuery = "For activity in " + activitiesCollectionName
                    + " FILTER activity.sender_id IN " + new JSONArray(followings)
                    + " SORT activity.created_at"
                    + " Limit "+ start + ", " + limit
                    + " RETURN activity";
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


    public static ArrayList<JSONObject> getFeed(String userID,int limit, int offset){
        ArrayList<String> followers = getAllfollowingIDs(""+ userID);
        JSONArray jsonFollowersArray = new JSONArray(followers);
        System.out.println(jsonFollowersArray);
        return getFeedForFriends(jsonFollowersArray,limit,offset);
    }


    public static ArrayList<JSONObject> getFeedForFriends(JSONArray friends,int limit, int offset){
        ArrayList<JSONObject> results = new ArrayList<JSONObject>();
        String dbQuery ="for post in Posts FILTER post.user_id in "+ friends.toString() +" SORT post.created_at DESC   LIMIT "+offset+" , "+limit+ " return post";
        Map<String, Object> bindVars = new MapBuilder().get();
        System.out.println(dbQuery);
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(dbQuery, bindVars, null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            JSONObject jsonObject = new JSONObject(aDocument.getProperties());
            results.add(jsonObject);
            System.out.println("Post : " + jsonObject);
        });
        return results;
    }

    //POSTS CRUD
    public static String insertPost(JSONObject postJSON, String userId) throws Exception {
        try {

            JSONObject myObject = new JSONObject();
            myObject.put("user_id", userId);
            myObject.put("caption", postJSON.get("caption").toString());
            myObject.put("media", postJSON.get("media"));
            //TODO: @MAGDY location gets inserted in a wrong way (with key "map")
//            myObject.addAttribute("location", postJSON.getJSONObject("location"));
            myObject.put("comments", new ArrayList<>());
            myObject.put("likes", new ArrayList<>());
            myObject.put("created_at", new Timestamp(System.currentTimeMillis()));
            myObject.put("updated_at", JSONObject.NULL);
            myObject.put("blocked_at", JSONObject.NULL);
            myObject.put("deleted_at", JSONObject.NULL);

            String id = arangoDB.db(dbName).collection(postsCollectionName).insertDocument(myObject.toString()).getKey();
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
        System.out.println(postJSON);
        try {
            BaseDocument myObject = new BaseDocument();
            myObject.addAttribute("user_id", postJSON.get("user_id").toString());
            myObject.addAttribute("caption", postJSON.get("caption").toString());
            myObject.addAttribute("media", postJSON.get("media").toString());
            myObject.addAttribute("likes", postJSON.get("likes").toString());
            // @Magdy
//            myObject.addAttribute("location", postJSON.get("location").toString());
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

    public static JSONArray getCommentsOnPost(String postID){
        try {
            BaseDocument postDoc = arangoDB.db(dbName).collection(postsCollectionName).getDocument(postID,
                    BaseDocument.class);
            if(postDoc == null){
                throw new ArangoDBException("Post with ID: " + postID +" Not Found");
            }
            JSONObject postJSON  = new JSONObject(postDoc.getProperties());
            return (JSONArray) reformatJSON(postJSON).get("comments");
        } catch (ArangoDBException e) {
            System.err.println("Failed to get Post: " + e.getMessage());
            return null;
        }
    }


    public static void insertMessageOnThread(String threadID, JSONObject message){
        JSONObject post = getThread(threadID);
        ((JSONArray) post.get("messages")).put(message);
        updatePost(threadID,post);
    }


    public static void initializeGraphCollections() throws IOException {
        for (GraphEntity graphEntity : arangoDB.db(dbName).getGraphs()) {
            if (graphEntity.getName().equals(graphName)) {

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


            EdgeDefinition edgeUserTagged = new EdgeDefinition();

            edgeUserTagged.collection(graphUserTaggedCollectionName);
            edgeUserTagged.from(userCollectionName);
            edgeUserTagged.to(postsCollectionName);

            EdgeDefinition edgePostTagged = new EdgeDefinition();

            edgePostTagged.collection(graphPostTaggedCollectionName);
            edgePostTagged.from(postsCollectionName);
            edgePostTagged.to(hashtagCollectionName);

            edgeDefinitions.add(edgeUserFollows);
            edgeDefinitions.add(edgeUserInteracts);
            edgeDefinitions.add(edgeUserTagged);
            edgeDefinitions.add(edgePostTagged);

            GraphCreateOptions options = new GraphCreateOptions();
            options.orphanCollections("dummyOptions");

            arangoDB.db(dbName).createGraph(graphName, edgeDefinitions, options);


            for (String user_id : user_ids) {
                BaseDocument userDocument = new BaseDocument();
                userDocument.setKey(user_id);
                arangoDB.db(dbName).graph(graphName).vertexCollection(userCollectionName).insertVertex(userDocument, null);
            }
            System.out.println("GraphDB was created");
        } catch (ArangoDBException e) {
            System.err.println("Faild to intilize graph: " + e.getMessage());
            return;
        }
    }


    public static boolean followUser(String followerKey, String followedKey) {

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String followerID = "Users/"+followerKey;
        String followedID = "Users/"+followedKey;
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

    public static boolean followHashtag(String userIdKey, String hashtagNameKey){

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String userID = "Users/"+userIdKey;
        String hashtagName = "Hashtags/"+hashtagNameKey;
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

    public static boolean tagUserInPost(String userIdKey, String postIDKey){

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String userID = "Users/"+userIdKey;
        String postID = "Posts/"+postIDKey;
        edge.setKey(userIdKey+postIDKey);
        edge.setFrom(userID);
        edge.setTo(postID);

        try{
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserTaggedCollectionName);
            edgecollection.insertEdge(edge,null);
            System.out.println("User Tag Edge Inserted");
            return true;
        }
        catch (ArangoDBException e){
            System.err.println("Edge Insertion Failed In Graph: " + e.getMessage());
            return false;
        }
    }

    public static boolean tagPostInHashtag(String postIDKey, String hashtagNameKey){

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String postID = "Posts/"+postIDKey;
        String hashtagName = "Hashtags/"+hashtagNameKey;
        edge.setKey(postIDKey + hashtagNameKey);
        edge.setFrom(postID);
        edge.setTo(hashtagName);

        try{
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphPostTaggedCollectionName);
            edgecollection.insertEdge(edge,null);
            System.out.println("Post Tag Edge Inserted");
            return true;
        }
        catch (ArangoDBException e){
            System.err.println("Edge Insertion Failed In Graph: " + e.getMessage());
            return false;
        }
    }


    public static boolean unFollowUser(String followerKey, String followedKey){
        try {
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserFollowsCollectionName);
            edgecollection.deleteEdge(followerKey + followedKey);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Edge Deletion Failed In Graph: " + e.getMessage());
            return false;
        }


    }

    public static boolean unFolllowHashtag(String userIDKey, String hashtagNameKey) {
        try {
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserInteractsCollectionName);
            edgecollection.deleteEdge(userIDKey + hashtagNameKey);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Edge Deletion Failed In Graph: " + e.getMessage());
            return false;
        }
    }

    public static boolean untagUser(String userIDKey, String postIDKey){
        try {
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserTaggedCollectionName);
            edgecollection.deleteEdge(userIDKey + postIDKey);
            return true;
        }
        catch (ArangoDBException e){
            System.err.println("Edge Deletion Failed In Graph: " + e.getMessage());
            return false;
        }
    }

    public static boolean untagPost(String postIDKey, String hashtagNameKey){
        try {
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphPostTaggedCollectionName);
            edgecollection.deleteEdge(postIDKey + hashtagNameKey);
            return true;
        }
        catch (ArangoDBException e){
            System.err.println("Edge Deletion Failed In Graph: " + e.getMessage());
            return false;
        }
    }


    public static boolean makeUserNode(String userID){
        try{
            BaseDocument userDocument = new BaseDocument();
            userDocument.setKey(userID);
            arangoDB.db(dbName).graph(graphName).vertexCollection(userCollectionName).insertVertex(userDocument, null);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Failed to initialize a node for user In Graph: " + e.getMessage());
            return false;
        }

    }


    public static boolean makeHashtagNode(String hashtagName){
        try{
            BaseDocument hashtagDocument = new BaseDocument();
            hashtagDocument.setKey(hashtagName);
            arangoDB.db(dbName).graph(graphName).vertexCollection(hashtagCollectionName).insertVertex(hashtagDocument, null);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Failed to initialize a node for hashtag In Graph: " + e.getMessage());
            return false;
        }

    }

//    public static boolean makePostNode(String postID){
//        try{
//            BaseDocument postDoc = new BaseDocument();
//            postDoc.setKey(postID);
//            arangoDB.db(dbName).graph(graphName).vertexCollection(postsCollectionName).insertVertex(postDoc, null);
//            return true;
//        }
//        catch(ArangoDBException e){
//            System.err.println("Failed to initialize a node for post In Graph: " + e.getMessage());
//            return false;
//        }
//    }


    public static boolean isFollowing(String userKey, String followingKey) {
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserFollowsCollectionName);
        BaseEdgeDocument edgeDoc = edgecollection.getEdge(userKey + followingKey, BaseEdgeDocument.class);
        if (edgeDoc == null) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean isInteracting(String userKey, String hashtagKey) {
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserInteractsCollectionName);
        BaseEdgeDocument edgeDoc = edgecollection.getEdge(userKey + hashtagKey, BaseEdgeDocument.class);
        if (edgeDoc == null) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean isTaggedUser(String userKey, String postIdKey){
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserTaggedCollectionName);
        BaseEdgeDocument edgeDoc = edgecollection.getEdge(userKey+postIdKey,BaseEdgeDocument.class);
        if(edgeDoc == null){
            return false;
        }
        else{
            return true;
        }

    }

    public static boolean isTaggedPost(String postIDKey, String hashtagNameKey){
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphPostTaggedCollectionName);
        BaseEdgeDocument edgeDoc = edgecollection.getEdge(postIDKey+hashtagNameKey,BaseEdgeDocument.class);
        if(edgeDoc == null){
            return false;
        }
        else{
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

    public static ArrayList<String> getAllfollowingIDs(String userKey) {
        try {
            String userID = "Users/"+userKey;
            ArrayList<String> IDs = new ArrayList<>();
            String query = "FOR vertex IN OUTBOUND \""  + userID+"\" "+ graphUserFollowsCollectionName + " RETURN vertex " ;
            System.out.println(query);
            Map<String, Object> bindVars = new MapBuilder().get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            System.out.println(cursor.getCount());
            cursor.forEachRemaining(aDocument -> {
                IDs.add(aDocument.getKey());
            });
            return IDs;
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
            return null;
        }

    }

    public static ArrayList<String> getAllfollowersIDs(String userKey){
        try{
            String userID = "Users/"+userKey;
            ArrayList<String> IDs = new ArrayList<>();
            String query = "FOR vertex IN INBOUND \""  + userID+"\" "+ graphUserFollowsCollectionName + " RETURN vertex " ;
            Map<String, Object> bindVars = new MapBuilder().get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                IDs.add(aDocument.getKey());
                System.out.println("ID follower: "+ aDocument.getKey());
            });
            return IDs;
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
            return null;
        }

    }


    public static ArrayList<String> getAllFollowingHashtags(String userKey){
        try{
            String userID = "Users/"+userKey;
            ArrayList<String> HashtagNames = new ArrayList<>();
            String query = "FOR vertex IN OUTBOUND \""  + userID+"\" "+ graphUserInteractsCollectionName + " RETURN vertex " ;
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


    public static ArrayList<String> getAllHashtagFollowers(String hashtagNameKey){
        try{
            String hashtagName = "Hashtags/"+hashtagNameKey;
            ArrayList<String> IDs = new ArrayList<>();
            String query = "FOR vertex IN INBOUND \""  + hashtagName+"\" "+ graphUserInteractsCollectionName + " RETURN vertex " ;
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


    public static ArrayList<String> getAllUsersTaggedInAPost(String postKey){
        try{
            String postId = "Posts/"+postKey;
            ArrayList<String> IDs = new ArrayList<>();
            String query = "FOR vertex IN INBOUND \""  + postId+"\" "+ graphUserTaggedCollectionName + " RETURN vertex " ;
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

    public static ArrayList<String> getAllTaggedPosts(String userKey){
        try{
            String userID = "Users/"+userKey;
            ArrayList<String> HashtagNames = new ArrayList<>();
            String query = "FOR vertex IN OUTBOUND \""  + userID+"\" "+ graphUserTaggedCollectionName + " RETURN vertex " ;
            Map<String, Object> bindVars = new MapBuilder().get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                HashtagNames.add(aDocument.getKey());
                System.out.println("Hashtag : "+ aDocument.getKey());
            });
            return HashtagNames;
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
            return null;
        }
    }


    public static ArrayList<String> getAllHashtagsTaggedInPost(String postKey){
        try{
            String postID = "Posts/"+postKey;
            ArrayList<String> HashtagNames = new ArrayList<>();
            String query = "FOR vertex IN OUTBOUND \""  + postID+"\" "+ graphPostTaggedCollectionName + " RETURN vertex " ;
            System.out.println(query);
            Map<String, Object> bindVars = new MapBuilder().get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                HashtagNames.add(aDocument.getKey());
                System.out.println("Hashtag : "+ aDocument.getKey());
            });
            return HashtagNames;
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
            return null;
        }
    }

    public static ArrayList<String> getAllPostsTaggedInHashtag(String hashtagNameKey){
        try{
            String hashtagName = "Hashtags/"+hashtagNameKey;
            ArrayList<String> posts = new ArrayList<>();
            String query = "FOR vertex IN INBOUND \""  + hashtagName+"\" "+ graphPostTaggedCollectionName + " RETURN vertex " ;
            Map<String, Object> bindVars = new MapBuilder().get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                posts.add(aDocument.getKey());
                System.out.println("Post : "+ aDocument.getKey());
            });
            return posts;
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
