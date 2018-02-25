package persistence.nosql;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.CollectionEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class ArangoInterfaceTest {

    private static ArangoDB arangoDB;
    private static String dbName =  "InstagramTestAQL";

    private static final String threadsCollectionName = "Threads";
    private static final String notificationsCollectionName = "Notifications";
    private static final String activitiesCollectionName = "Activities";
    private static final String storiesCollectionName = "Stories";
    private static final String postsCollectionName = "Posts";
    private static final String bookmarksCollectionName = "Bookmarks";
    @BeforeClass
    public static void setUp() {

        ArangoInterfaceMethods.dbName = ArangoInterfaceTest.dbName;
        dbName =  "InstagramTestAQL";
        arangoDB = new ArangoDB.Builder().build();
        try {
            if(arangoDB.getDatabases().contains(dbName)){
                arangoDB.db(dbName).drop();
            }
            arangoDB.createDatabase(dbName);
            System.out.println("Database created: " + dbName);

        } catch (ArangoDBException e) {
            System.err.println("Failed to create database: " + dbName + "; " + e.getMessage());
            arangoDB.createDatabase(dbName);
        }

        try {
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


        } catch (ArangoDBException e) {
            System.err.println("Failed to create collections: " + e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        arangoDB.shutdown();
    }

    @Test
    public void insertAndGetThread() {
        String id  = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("creator_id",utilities.Main.generateUUID());
        obj.put("users_ids",new ArrayList<String>());
        obj.put("name","Ahmed");
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at",new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj.put("messages",new ArrayList<String>());


        ArangoInterfaceMethods.insertThread(obj);
        JSONObject readObj = ArangoInterfaceMethods.getThread(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while(iterator.hasNext()){
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value,obj.get(key).toString());

        }
    }

    @Test
    public void updateAndDeleteThread() {
        String id  = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("creator_id",utilities.Main.generateUUID());
        obj.put("users_ids",new ArrayList<String>());
        obj.put("name","Abd El Rahman");
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at",new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj.put("messages",new ArrayList<String>());

        JSONObject updatedObj = new JSONObject();
        updatedObj.put("id", id);
        updatedObj.put("creator_id",utilities.Main.generateUUID());
        updatedObj.put("users_ids",new ArrayList<String>());
        updatedObj.put("name","Mohamed");
        updatedObj.put("created_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("deleted_at",new Timestamp(System.currentTimeMillis()));
        updatedObj.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        updatedObj.put("messages",new ArrayList<String>());


        ArangoInterfaceMethods.insertThread(obj);
        ArangoInterfaceMethods.updateThread(id,updatedObj);
        JSONObject jsonThread = ArangoInterfaceMethods.getThread(id);
        Assert.assertEquals(Objects.requireNonNull(jsonThread).get("name"),"Mohamed");

        ArangoInterfaceMethods.deleteThread(id);
        Assert.assertEquals(ArangoInterfaceMethods.getThread(id),null);

    }

    @Test
    public void insertAndGetNotification() {
        String id  = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("activity_type","{ type: follow, user_id: 2343-2342");
        obj.put("receiver_id", utilities.Main.generateUUID());
        obj.put("sender_id", utilities.Main.generateUUID());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));


        ArangoInterfaceMethods.insertNotification(obj);
        JSONObject readObj = ArangoInterfaceMethods.getNotification(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while(iterator.hasNext()){
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value,obj.get(key).toString());

        }
    }

    @Test
    public void updateAndDeleteNotification() {
        String id  = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("activity_type","{ type: follow, user_id: 2343-2342 }");
        obj.put("receiver_id", utilities.Main.generateUUID());
        obj.put("sender_id", utilities.Main.generateUUID());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));

        JSONObject updatedObj = new JSONObject();
        updatedObj.put("id", id);
        updatedObj.put("activity_type","{ type: tag, user_id: 2343-2342 }");
        updatedObj.put("receiver_id", utilities.Main.generateUUID());
        updatedObj.put("sender_id", utilities.Main.generateUUID());
        updatedObj.put("created_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("blocked_at",new Timestamp(System.currentTimeMillis()));


        ArangoInterfaceMethods.insertNotification(obj);
        ArangoInterfaceMethods.updateNotification(id,updatedObj);
        JSONObject jsonNotification = ArangoInterfaceMethods.getNotification(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonNotification).get("activity_type"),
                "{ type: tag, user_id: 2343-2342 }");

        ArangoInterfaceMethods.deleteNotification(id);
        Assert.assertEquals(ArangoInterfaceMethods.getNotification(id),null);
    }

    @Test
    public void insertAndGetActivity() {
        String id  = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("activity_type","{ type: follow, user_id: 2343-2342");
        obj.put("receiver_id", utilities.Main.generateUUID());
        obj.put("sender_id", utilities.Main.generateUUID());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));


        ArangoInterfaceMethods.insertActivity(obj);
        JSONObject readObj = ArangoInterfaceMethods.getActivity(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while(iterator.hasNext()){
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value,obj.get(key).toString());

        }
    }

    @Test
    public void updateAndDeleteActivity() {
        String id  = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("activity_type","{ type: follow, user_id: 2343-2342 }");
        obj.put("receiver_id", utilities.Main.generateUUID());
        obj.put("sender_id", utilities.Main.generateUUID());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));

        JSONObject updatedObj = new JSONObject();
        updatedObj.put("id", id);
        updatedObj.put("activity_type","{ type: tag, user_id: 2343-2342 }");
        updatedObj.put("receiver_id", utilities.Main.generateUUID());
        updatedObj.put("sender_id", utilities.Main.generateUUID());
        updatedObj.put("created_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("blocked_at",new Timestamp(System.currentTimeMillis()));


        ArangoInterfaceMethods.insertActivity(obj);
        ArangoInterfaceMethods.updateActivity(id,updatedObj);
        JSONObject jsonNotification = ArangoInterfaceMethods.getActivity(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonNotification).get("activity_type"),
                "{ type: tag, user_id: 2343-2342 }");

        ArangoInterfaceMethods.deleteActivity(id);
        Assert.assertEquals(ArangoInterfaceMethods.getActivity(id),null);


    }

    @Test
    public void insertAndGetStory() {

        String id  = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("user_id",utilities.Main.generateUUID());
        obj.put("is_featured",false);
        obj.put("media_id", utilities.Main.generateUUID());
        obj.put("reports", new ArrayList<String>());
        obj.put("seen_by_users_ids",new ArrayList<String>());
        obj.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at",new Timestamp(System.currentTimeMillis()));
        obj.put("expired_at",new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));

        ArangoInterfaceMethods.insertStory(obj);
        JSONObject readObj = ArangoInterfaceMethods.getStory(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while(iterator.hasNext()){
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value,obj.get(key).toString());

        }
    }

    @Test
    public void updateAndDeleteStory() {
        String id  = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("user_id",utilities.Main.generateUUID());
        obj.put("is_featured",false);
        obj.put("media_id", utilities.Main.generateUUID());
        obj.put("reports", new ArrayList<String>());
        obj.put("seen_by_users_ids",new ArrayList<String>());
        obj.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at",new Timestamp(System.currentTimeMillis()));
        obj.put("expired_at",new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));

        JSONObject updatedObj = new JSONObject();
        updatedObj.put("id", id);
        updatedObj.put("user_id",utilities.Main.generateUUID());
        updatedObj.put("is_featured",true);
        updatedObj.put("media_id", utilities.Main.generateUUID());
        updatedObj.put("reports", new ArrayList<String>());
        updatedObj.put("seen_by_users_ids",new ArrayList<String>());
        updatedObj.put("created_at",new Timestamp(System.currentTimeMillis()));
        updatedObj.put("deleted_at",new Timestamp(System.currentTimeMillis()));
        updatedObj.put("expired_at",new Timestamp(System.currentTimeMillis()));
        updatedObj.put("blocked_at",new Timestamp(System.currentTimeMillis()));


        ArangoInterfaceMethods.insertStory(obj);
        ArangoInterfaceMethods.updateStory(id,updatedObj);
        JSONObject jsonNotification = ArangoInterfaceMethods.getStory(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonNotification).get("is_featured"),
                "true");

        ArangoInterfaceMethods.deleteStory(id);
        Assert.assertEquals(ArangoInterfaceMethods.getStory(id),null);

    }
    @Test
    public void insertAndGetPost() {

        String id  = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("user_id",utilities.Main.generateUUID());
        obj.put("caption","Taken By MiSO EL Gen");
        obj.put("media", new ArrayList<String>());
        obj.put("likes", new ArrayList<String>());
        obj.put("tags",new ArrayList<String>());
        obj.put("location","{ name: EspressoLab, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        obj.put("comments", new ArrayList<String>());
        obj.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj.put("updated_at",new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at",new Timestamp(System.currentTimeMillis()));

        ArangoInterfaceMethods.insertPost(obj);
        JSONObject readObj = ArangoInterfaceMethods.getPost(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while(iterator.hasNext()){
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value,obj.get(key).toString());

        }
    }



    @Test
    public void updateAndDeletePost() {

        String id  = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("user_id",utilities.Main.generateUUID());
        obj.put("caption","Taken By MiSO EL Gen");
        obj.put("media", new ArrayList<String>());
        obj.put("likes", new ArrayList<String>());
        obj.put("tags",new ArrayList<String>());
        obj.put("location","{ name: EspressoLab, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        obj.put("comments", new ArrayList<String>());
        obj.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj.put("updated_at",new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at",new Timestamp(System.currentTimeMillis()));

        JSONObject updatedObj = new JSONObject();
        updatedObj.put("id", id);
        updatedObj.put("user_id",utilities.Main.generateUUID());
        updatedObj.put("caption","Friends");
        updatedObj.put("media", new ArrayList<String>());
        updatedObj.put("likes", new ArrayList<String>());
        updatedObj.put("tags",new ArrayList<String>());
        updatedObj.put("location","{ name: EspressoLab, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        updatedObj.put("comments", new ArrayList<String>());
        updatedObj.put("created_at",new Timestamp(System.currentTimeMillis()));
        updatedObj.put("updated_at",new Timestamp(System.currentTimeMillis()));
        updatedObj.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        updatedObj.put("deleted_at",new Timestamp(System.currentTimeMillis()));


        ArangoInterfaceMethods.insertPost(obj);
        ArangoInterfaceMethods.updatePost(id,updatedObj);
        JSONObject jsonNotification = ArangoInterfaceMethods.getPost(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonNotification).get("caption"),
                "Friends");

        ArangoInterfaceMethods.deletePost(id);
        Assert.assertEquals(ArangoInterfaceMethods.getStory(id),null);
    }

    @Test
    public void insertAndGetBookmark() {

        String id  = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("user_id", id);
        obj.put("posts_ids",new ArrayList<String>());

        ArangoInterfaceMethods.insertBookmark(obj);
        JSONObject readObj = ArangoInterfaceMethods.getBookmark(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while(iterator.hasNext()){
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value,obj.get(key).toString());

        }

    }

    @Test
    public void updateAndDeleteBookmark() {

        String id  = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("user_id", id);
        obj.put("posts_ids",new ArrayList<String>());

        JSONObject updatedObj = new JSONObject();
        ArrayList<String> post_ids = new ArrayList<>();
        post_ids.add(utilities.Main.generateUUID());
        post_ids.add(utilities.Main.generateUUID());


        updatedObj.put("user_id", id);
        updatedObj.put("posts_ids", post_ids);



        ArangoInterfaceMethods.insertBookmark(obj);
        ArangoInterfaceMethods.updateBookmark(id,updatedObj);
        JSONObject jsonBookmark = ArangoInterfaceMethods.getBookmark(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonBookmark).get("posts_ids").toString(),
                updatedObj.get("posts_ids").toString());


        ArangoInterfaceMethods.deleteBookmark(id);
        Assert.assertEquals(ArangoInterfaceMethods.getBookmark(id),null);


    }


    @Test
    public void insertCommentInPost(){

        String id  = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("user_id",utilities.Main.generateUUID());
        obj.put("caption","Taken By MiSO EL Gen");
        obj.put("media", new ArrayList<String>());
        obj.put("likes", new ArrayList<String>());
        obj.put("tags",new ArrayList<String>());
        obj.put("location","{ name: EspressoLab, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        obj.put("comments", new ArrayList<String>());
        obj.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj.put("updated_at",new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at",new Timestamp(System.currentTimeMillis()));

        ArangoInterfaceMethods.insertPost(obj);
        JSONObject comment = new JSONObject();
        comment.put("content","Hello");

        ArangoInterfaceMethods.insertCommentOnPost(id,comment);

        JSONObject fetchedPost = ArangoInterfaceMethods.getPost(id);
        System.out.println("POSSST:  "+fetchedPost);

//        JSONArray comments = (JSONArray)fetchedPost.get("comments"));

        Assert.assertTrue(fetchedPost.get("comments").toString().contains(comment.toString()));
    }


}