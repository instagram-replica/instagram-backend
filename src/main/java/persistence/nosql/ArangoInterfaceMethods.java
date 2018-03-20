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
import exceptions.CustomException;
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
    private static final String graphUserBlockedCollectionName = "UserBlocked";
    private static final String graphUserReportedCollectionName = "UserReported";
    private static final String graphUserConnectedToThreadCollectionName = "UserConnectedToThread";


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
    }

    public static JSONObject getThread(String id) throws CustomException{
            BaseDocument threadDoc = arangoDB.db(dbName).collection(threadsCollectionName).getDocument(id,
                    BaseDocument.class);
            if (threadDoc == null) {
                throw new CustomException("Thread with ID: " + id + " Not Found");
            }
            JSONObject threadJSON = new JSONObject(threadDoc.getProperties());
            return reformatJSON(threadJSON);

    }

    public static void updateThread(String id, JSONObject threadJSON) {
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
    }

    public static void deleteThread(String id) throws CustomException{
        try {
            arangoDB.db(dbName).collection(threadsCollectionName).deleteDocument(id);
            System.out.println("Thread Deleted: " + id);
        } catch (ArangoDBException e) {
            throw new CustomException("Thread ID does not exist:  " + id );
        }
    }


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

    public static JSONObject getNotification(String id) throws CustomException{
            BaseDocument notificationDoc = arangoDB.db(dbName).collection(notificationsCollectionName).getDocument(id,
                    BaseDocument.class);
            if (notificationDoc == null) {
                throw new CustomException("Notification with ID: " + id + " Not Found");
            }
            JSONObject notificationJSON = new JSONObject(notificationDoc.getProperties());
            return reformatJSON(notificationJSON);

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
            return reformatJSON(activityJSON);

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


    //STORY CRUD
    public static String insertStory(JSONObject storyJSON) {

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

    }

    public static JSONObject getStory(String id) throws CustomException{

            BaseDocument storyDoc = arangoDB.db(dbName).collection(storiesCollectionName).getDocument(id,
                    BaseDocument.class);
            if (storyDoc == null) {
                throw new CustomException("Story with ID: " + id + " Not Found");
            }
            JSONObject storyJSON = new JSONObject(storyDoc.getProperties());
            return reformatJSON(storyJSON);
    }

    public static boolean updateStory(String id, JSONObject storyJSON) {
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

    }

    public static void deleteStory(String id) {
            arangoDB.db(dbName).collection(storiesCollectionName).deleteDocument(id);
            System.out.println("Story Deleted: " + id);
    }

    public static JSONArray getStoriesForUser(String user_id) {

            String dbQuery = "For story in " + storiesCollectionName + " FILTER story.user_id == \""+ user_id + "\" RETURN story";
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(dbQuery, null, null, BaseDocument.class);
            JSONArray result = new JSONArray();
            cursor.forEachRemaining(aDocument -> {
                JSONObject postJSON = new JSONObject(aDocument.getProperties());
                result.put(reformatJSON(postJSON));
            });
            return result;

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

    public static JSONArray getDiscoverStories(String userID){
        JSONArray resultStories = new JSONArray();
        ArrayList<String> publicFriendOfFriends = getAllfollowingPublicIDsSecondDegree(""+ userID);

        Collections.shuffle(publicFriendOfFriends);
        JSONArray friendStories;
        int min = Math.min(10,publicFriendOfFriends.size());
        for(int  i =0 ; i< min;i++){
            friendStories = getStoriesForUser(publicFriendOfFriends.get(i));
            JSONObject JSONUserStories = new JSONObject();
            JSONUserStories.put("user_id",publicFriendOfFriends.get(i));
            JSONUserStories.put("stories",friendStories);
            if(friendStories.length() != 0){
                resultStories.put(JSONUserStories);
            }
        }
        return resultStories;
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
                result.put(reformatJSON(postJSON));
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
                result.put(reformatJSON(postJSON));
            });
            return result;
    }


    public static ArrayList<JSONObject> getFeed(String userID,int limit, int offset){
        ArrayList<String> friends = getAllfollowingIDs(""+ userID);
        JSONArray jsonFollowersArray = new JSONArray(friends);
        return getFeedForFriends(jsonFollowersArray,limit,offset);
    }

    public static ArrayList<JSONObject> getDiscoveryFeed(String userID,int limit, int offset){
        ArrayList<String> friends = getAllfollowingPublicIDsSecondDegree(""+ userID);
        JSONArray jsonFollowersArray = new JSONArray(friends);
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

    public static String insertPost(JSONObject postJSON, String userId) throws JSONException {


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
    }

    public static JSONObject getPost(String id) throws CustomException, ArangoDBException {

            BaseDocument postDoc = arangoDB.db(dbName).collection(postsCollectionName).getDocument(id,
                    BaseDocument.class);
            if (postDoc == null) {
                throw new CustomException("Post with ID: " + id + " Not Found");
            }
            JSONObject postJSON = new JSONObject(postDoc.getProperties());
            postJSON.put("id", postDoc.getKey());
            return reformatJSON(postJSON);

    }

    public static JSONArray getPosts(String userId) throws ArangoDBException{
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
    }

    public static void likePost(String postID, String userID) throws Exception {
        JSONObject post = getPost(postID);
        JSONArray likes = (JSONArray) post.get("likes");
        likes.put(userID);
        updatePost(postID, post);
    }

    public static void unlikePost(String postID, String userID) throws Exception {
        String dbQuery = "FOR post IN Posts FILTER post._key == '"+postID+"' UPDATE post WITH { likes: REMOVE_VALUE( post.likes, '"+userID+"' ) } IN Posts";
        arangoDB.db(dbName).query(dbQuery, null, null, null);
    }

    public static void updatePost(String id, JSONObject postJSON) {
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
    }

    public static void deletePost(String id) throws CustomException{
        try {
            arangoDB.db(dbName).collection(postsCollectionName).deleteDocument(id);
            System.out.println("Post Deleted: " + id);
        } catch (ArangoDBException e) {
            throw new CustomException("Post ID does not exist:  " + id);
        }
    }


    //BOOKMARKS CRUD
    public static String insertBookmark(JSONObject bookmarkJSON) {

            BaseDocument myObject = new BaseDocument();
            myObject.addAttribute("user_id", bookmarkJSON.get("user_id").toString());
            myObject.addAttribute("posts_ids", bookmarkJSON.get("posts_ids").toString());
            String id = arangoDB.db(dbName).collection(bookmarksCollectionName).insertDocument(bookmarkJSON.toString()).getKey();
            System.out.println("Bookmark inserted");
            return id;
    }

    public static JSONObject getBookmark(String id) throws CustomException, ArangoDBException {

            BaseDocument bookmarkDoc = arangoDB.db(dbName).collection(bookmarksCollectionName).getDocument(id,
                    BaseDocument.class);
            if (bookmarkDoc == null) {
                throw new CustomException("Bookmark with ID: " + id + " Not Found");
            }
            JSONObject bookmarkJSON = new JSONObject(bookmarkDoc.getProperties());
            return reformatJSON(bookmarkJSON);
    }

    public static void updateBookmark(String id, JSONObject bookmarkJSON) {

            BaseDocument myObject = new BaseDocument();
            myObject.addAttribute("user_id", bookmarkJSON.get("user_id").toString());
            myObject.addAttribute("posts_ids", bookmarkJSON.get("posts_ids").toString());
            arangoDB.db(dbName).collection(bookmarksCollectionName).updateDocument(id, bookmarkJSON.toString());
            System.out.println("Bookmark Updated");

    }

    public static void deleteBookmark(String id) throws CustomException{
        try {
            arangoDB.db(dbName).collection(bookmarksCollectionName).deleteDocument(id);
            System.out.println("Bookmark Deleted: " + id);
        } catch (ArangoDBException e) {
            throw new CustomException("Bookmark ID does not exist:  " + id);
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

    public static JSONArray getCommentsOnPost(String postID) throws CustomException{

            BaseDocument postDoc = arangoDB.db(dbName).collection(postsCollectionName).getDocument(postID,
                    BaseDocument.class);
            if(postDoc == null){
                throw new CustomException("Post with ID: " + postID +" Not Found");
            }
            JSONObject postJSON  = new JSONObject(postDoc.getProperties());
            return (JSONArray) reformatJSON(postJSON).get("comments");

    }


    public static void insertMessageOnThread(String threadID, JSONObject message) throws CustomException{
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

            EdgeDefinition edgeUserBlocked = new EdgeDefinition();

            edgeUserBlocked.collection(graphUserBlockedCollectionName);
            edgeUserBlocked.from(userCollectionName);
            edgeUserBlocked.to(userCollectionName);

            EdgeDefinition edgeUserReported = new EdgeDefinition();

            edgeUserReported.collection(graphUserReportedCollectionName);
            edgeUserReported.from(userCollectionName);
            edgeUserReported.to(userCollectionName);

            EdgeDefinition edgeUserThread = new EdgeDefinition();

            edgeUserThread.collection(graphUserConnectedToThreadCollectionName);
            edgeUserThread.from(userCollectionName);
            edgeUserThread.to(threadsCollectionName);


            edgeDefinitions.add(edgeUserFollows);
            edgeDefinitions.add(edgeUserInteracts);
            edgeDefinitions.add(edgeUserTagged);
            edgeDefinitions.add(edgePostTagged);
            edgeDefinitions.add(edgeUserBlocked);
            edgeDefinitions.add(edgeUserReported);
            edgeDefinitions.add(edgeUserThread);

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

        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserFollowsCollectionName);
        edgecollection.insertEdge(edge, null);
        return true;

    }

    public static boolean blockUser(String blockerKey, String blockedKey) {

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String blockerID = "Users/"+blockerKey;
        String blockedID = "Users/"+blockedKey;
        edge.setKey(blockerKey + blockedKey);
        edge.setFrom(blockerID);
        edge.setTo(blockedID);

        try {
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserBlockedCollectionName);
            edgecollection.insertEdge(edge, null);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Edge Insertion Failed In Graph: " + e.getMessage());
            return false;
        }
    }

    public static boolean reportUser(String reporterKey, String reportedKey) {

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String reporterID = "Users/"+reporterKey;
        String reportedID = "Users/"+reportedKey;
        edge.setKey(reporterKey + reportedKey);
        edge.setFrom(reporterID);
        edge.setTo(reportedID);

        try {
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserReportedCollectionName);
            edgecollection.insertEdge(edge, null);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Edge Insertion Failed In Graph: " + e.getMessage());
            return false;
        }
    }

    public static boolean joinThread(String userKey, String threadKey) {

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String userID = "Users/"+userKey;
        String threadID = "Threads/"+threadKey;
        edge.setKey(userKey + threadKey);
        edge.setFrom(userID);
        edge.setTo(threadID);

        try {
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserConnectedToThreadCollectionName);
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
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserInteractsCollectionName);
        edgecollection.insertEdge(edge, null);
        return true;

    }

    public static boolean tagUserInPost(String userIdKey, String postIDKey){

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String userID = "Users/"+userIdKey;
        String postID = "Posts/"+postIDKey;
        edge.setKey(userIdKey+postIDKey);
        edge.setFrom(userID);
        edge.setTo(postID);

        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserTaggedCollectionName);
        edgecollection.insertEdge(edge,null);
        System.out.println("User Tag Edge Inserted");
        return true;

    }

    public static boolean tagPostInHashtag(String postIDKey, String hashtagNameKey){

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String postID = "Posts/"+postIDKey;
        String hashtagName = "Hashtags/"+hashtagNameKey;
        edge.setKey(postIDKey + hashtagNameKey);
        edge.setFrom(postID);
        edge.setTo(hashtagName);

        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphPostTaggedCollectionName);
        edgecollection.insertEdge(edge,null);
        System.out.println("Post Tag Edge Inserted");
        return true;

    }


    public static boolean unFollowUser(String followerKey, String followedKey){
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserFollowsCollectionName);
            edgecollection.deleteEdge(followerKey + followedKey);
            return true;

    }

    public static boolean unblockUser(String blockerKey, String blockedKey){
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserBlockedCollectionName);
            edgecollection.deleteEdge(blockerKey + blockedKey);
            return true;

    }

    public static boolean unFolllowHashtag(String userIDKey, String hashtagNameKey) {
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserInteractsCollectionName);
            edgecollection.deleteEdge(userIDKey + hashtagNameKey);
            return true;
    }

    public static boolean untagUser(String userIDKey, String postIDKey){
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserTaggedCollectionName);
            edgecollection.deleteEdge(userIDKey + postIDKey);
            return true;
    }

    public static boolean untagPost(String postIDKey, String hashtagNameKey){
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphPostTaggedCollectionName);
            edgecollection.deleteEdge(postIDKey + hashtagNameKey);
            return true;

    }


    public static boolean makeUserNode(String userID){
            BaseDocument userDocument = new BaseDocument();
            userDocument.setKey(userID);
            arangoDB.db(dbName).graph(graphName).vertexCollection(userCollectionName).insertVertex(userDocument, null);
            return true;

    }


    public static boolean makeHashtagNode(String hashtagName){
            BaseDocument hashtagDocument = new BaseDocument();
            hashtagDocument.setKey(hashtagName);
            arangoDB.db(dbName).graph(graphName).vertexCollection(hashtagCollectionName).insertVertex(hashtagDocument, null);
            return true;
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

    public static boolean isBlocked(String blockerKey, String blockedKey) {
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserBlockedCollectionName);
        BaseEdgeDocument edgeDoc = edgecollection.getEdge(blockerKey + blockedKey, BaseEdgeDocument.class);
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

    public static boolean isReported(String reporterKey, String reportedKey){
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserReportedCollectionName);
        BaseEdgeDocument edgeDoc = edgecollection.getEdge(reporterKey+reportedKey,BaseEdgeDocument.class);
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
            String userID = "Users/"+userKey;
            ArrayList<String> IDs = new ArrayList<>();
            String query = "FOR vertex IN OUTBOUND \""  + userID+"\" "+ graphUserFollowsCollectionName + " RETURN vertex " ;
            System.out.println(query);
            Map<String, Object> bindVars = new MapBuilder().get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                IDs.add(aDocument.getKey());
            });
            return IDs;
    }

    public static ArrayList<String> getAllfollowersIDs(String userKey){
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
    }

    public static ArrayList<String> getAllfollowingPublicIDsSecondDegree(String userKey){
            String userID = "Users/"+userKey;
            ArrayList<String> IDs = new ArrayList<>();
            String query = "FOR vertex IN 2..2 OUTBOUND  \""  + userID+"\" "+ graphUserFollowsCollectionName + " RETURN vertex " ;
            System.out.println(query);
            Map<String, Object> bindVars = new MapBuilder().get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                IDs.add(aDocument.getKey());
            });
            IDs.remove(userKey);
            //TODO Mohamed Abouzeid uncomment this line when done :D
//            IDs = removePrivateIDs(IDs);
            return IDs;
    }

    public static ArrayList<String> getAllThreadsForUser(String userKey) {
        String userID = "Users/"+userKey;
        ArrayList<String> IDs = new ArrayList<>();
        String query = "FOR vertex IN OUTBOUND \""  + userID+"\" "+ graphUserConnectedToThreadCollectionName + " RETURN vertex " ;
        System.out.println(query);
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            IDs.add(aDocument.getKey());
        });
        return IDs;
    }

    public static ArrayList<String> getAllUsersInThread(String threadKey){
        String threadID = "Threads/"+threadKey;
        ArrayList<String> IDs = new ArrayList<>();
        String query = "FOR vertex IN INBOUND \""  + threadID+"\" "+ graphUserConnectedToThreadCollectionName + " RETURN vertex " ;
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            IDs.add(aDocument.getKey());
            System.out.println("ID follower: "+ aDocument.getKey());
        });
        return IDs;
    }

    public static ArrayList<String> getAllBlockedIDs(String userKey) {
        try {
            String userID = "Users/"+userKey;
            ArrayList<String> IDs = new ArrayList<>();
            String query = "FOR vertex IN OUTBOUND \""  + userID+"\" "+ graphUserBlockedCollectionName + " RETURN vertex " ;
            System.out.println(query);
            Map<String, Object> bindVars = new MapBuilder().get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                IDs.add(aDocument.getKey());
            });
            return IDs;
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
            return null;
        }

    }

    public static ArrayList<String> getAllFollowingHashtags(String userKey){

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

    }


    public static ArrayList<String> getAllHashtagFollowers(String hashtagNameKey){
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
    }


    public static ArrayList<String> getAllUsersTaggedInAPost(String postKey){

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

    }

    public static ArrayList<String> getAllTaggedPosts(String userKey){

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
    }


    public static ArrayList<String> getAllHashtagsTaggedInPost(String postKey){

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
    }

    public static ArrayList<String> getAllPostsTaggedInHashtag(String hashtagNameKey){

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
