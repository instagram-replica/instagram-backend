package persistence.nosql;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.CollectionEntity;
import org.json.JSONObject;
import org.junit.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

public class ArangoInterfaceTest {

    protected static ArangoDB arangoDB;
    static String dbName =  "InstagramTestAQL";

    static final String threadsCollectionName = "Threads";
    static final String notificationsCollectionName = "Notifications";
    static final String activitiesCollectionName = "Activities";
    static final String storiesCollectionName = "Stories";
    static final String postsCollectionName = "Posts";
    static final String bookmarksCollectionName = "Bookmarks";
    @BeforeClass
    public static void setUp() throws Exception {

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
            System.out.println("TESTDB:  "+dbName);
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
    public static void tearDown() throws Exception {
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
        Assert.assertEquals(obj.toString(),readObj.toString());
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
        Assert.assertEquals(jsonThread.get("name"),"Mohamed");

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
        Assert.assertEquals(obj.toString(),readObj.toString());
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
                jsonNotification.get("activity_type"),
                "{ type: tag, user_id: 2343-2342 }");

        ArangoInterfaceMethods.deleteNotification(id);
        Assert.assertEquals(ArangoInterfaceMethods.getThread(id),null);
    }

    @Test
    public void insertNotification() {
    }

    @Test
    public void getNotification() {
    }

    @Test
    public void updateNotification() {
    }

    @Test
    public void deleteNotification() {
    }

    @Test
    public void insertActivity() {
    }

    @Test
    public void getActivity() {
    }

    @Test
    public void updateActivity() {
    }

    @Test
    public void deleteActivity() {
    }

    @Test
    public void insertStory() {
    }

    @Test
    public void getStory() {
    }

    @Test
    public void updateStory() {
    }

    @Test
    public void deleteStory() {
    }

    @Test
    public void insertPost() {
    }

    @Test
    public void getPost() {
    }

    @Test
    public void updatePost() {
    }

    @Test
    public void deletePost() {
    }

    @Test
    public void insertBookmark() {
    }

    @Test
    public void getBookmark() {
    }

    @Test
    public void updateBookmark() {
    }

    @Test
    public void deleteBookmark() {
    }
}