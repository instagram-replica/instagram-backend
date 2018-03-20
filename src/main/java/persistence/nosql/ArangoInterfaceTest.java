package persistence.nosql;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.EdgeDefinition;
import com.arangodb.entity.GraphEntity;
import com.arangodb.model.GraphCreateOptions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static persistence.nosql.ArangoInterfaceMethods.*;
import static persistence.sql.Main.openConnection;
import static persistence.sql.users.Main.getAllUsersIds;
import static utilities.Main.readPropertiesFile;

public class ArangoInterfaceTest {

    private static ArangoDB arangoDB;
    static String dbName = "InstagramTestAQL";

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


    @BeforeClass
    public static void setUp() throws IOException {
        ArangoInterfaceMethods.dbName = ArangoInterfaceTest.dbName;
        Properties properties = readPropertiesFile("src/main/resources/arango.properties");
        arangoDB = new ArangoDB.Builder().host(properties.getProperty("host"), Integer.parseInt(properties.getProperty("port"))).build();
        if(arangoDB.getDatabases().contains(dbName)) {
            arangoDB.db(dbName).drop();
        }
        try {
            if (arangoDB.getDatabases().contains(dbName)) {
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

        Iterator<GraphEntity> graphs = arangoDB.db(dbName).getGraphs().iterator();
        while(graphs.hasNext()){
            if(graphs.next().getName().equals(graphName)){
                return;
            }
        }

        openConnection();
        List<String> user_ids = getAllUsersIds();
        closeConnection();
        try{

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


            user_ids.forEach(ArangoInterfaceMethods::makeUserNode);

        } catch (ArangoDBException e) {
            System.err.println("Faild to intilize graph: " + e.getMessage());
            return;
        }

    }

    @AfterClass
    public static void tearDown() {
        arangoDB.shutdown();
    }

    @Test
    public void insertAndGetThread() {
        JSONObject obj = new JSONObject();
        obj.put("creator_id", utilities.Main.generateUUID());
        obj.put("users_ids", new ArrayList<String>());
        obj.put("name", "Ahmed");
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));
        obj.put("messages", new ArrayList<String>());


        String id = ArangoInterfaceMethods.insertThread(obj);
        JSONObject readObj = ArangoInterfaceMethods.getThread(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value, obj.get(key).toString());

        }
    }

    @Test
    public void updateAndDeleteThread() {
        JSONObject obj = new JSONObject();
        obj.put("creator_id", utilities.Main.generateUUID());
        obj.put("users_ids", new ArrayList<String>());
        obj.put("name", "Abd El Rahman");
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));
        obj.put("messages", new ArrayList<String>());

        JSONObject updatedObj = new JSONObject();
        updatedObj.put("creator_id", utilities.Main.generateUUID());
        updatedObj.put("users_ids", new ArrayList<String>());
        updatedObj.put("name", "Mohamed");
        updatedObj.put("created_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("deleted_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("blocked_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("messages", new ArrayList<String>());


        String id = ArangoInterfaceMethods.insertThread(obj);
        ArangoInterfaceMethods.updateThread(id, updatedObj);
        JSONObject jsonThread = ArangoInterfaceMethods.getThread(id);
        Assert.assertEquals(Objects.requireNonNull(jsonThread).get("name"), "Mohamed");

        ArangoInterfaceMethods.deleteThread(id);
        Assert.assertEquals(ArangoInterfaceMethods.getThread(id), null);

    }

    @Test
    public void insertAndGetNotification() {
        JSONObject obj = new JSONObject();
        obj.put("activity_type", "{ type: follow, user_id: 2343-2342");
        obj.put("receiver_id", utilities.Main.generateUUID());
        obj.put("sender_id", utilities.Main.generateUUID());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));


        String id = ArangoInterfaceMethods.insertNotification(obj);
        JSONObject readObj = ArangoInterfaceMethods.getNotification(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value, obj.get(key).toString());

        }
    }

    @Test
    public void updateAndDeleteNotification() {
        JSONObject obj = new JSONObject();
        obj.put("activity_type", "{ type: follow, user_id: 2343-2342 }");
        obj.put("receiver_id", utilities.Main.generateUUID());
        obj.put("sender_id", utilities.Main.generateUUID());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));

        JSONObject updatedObj = new JSONObject();
        updatedObj.put("activity_type", "{ type: tag, user_id: 2343-2342 }");
        updatedObj.put("receiver_id", utilities.Main.generateUUID());
        updatedObj.put("sender_id", utilities.Main.generateUUID());
        updatedObj.put("created_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("blocked_at", new Timestamp(System.currentTimeMillis()));


        String id = ArangoInterfaceMethods.insertNotification(obj);
        ArangoInterfaceMethods.updateNotification(id, updatedObj);
        JSONObject jsonNotification = ArangoInterfaceMethods.getNotification(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonNotification).get("activity_type"),
                "{ type: tag, user_id: 2343-2342 }");

        ArangoInterfaceMethods.deleteNotification(id);
        Assert.assertEquals(ArangoInterfaceMethods.getNotification(id), null);
    }

    @Test
    public void insertAndGetActivity() {
        JSONObject obj = new JSONObject();
        obj.put("activity_type", "{ type: follow, user_id: 2343-2342");
        obj.put("receiver_id", utilities.Main.generateUUID());
        obj.put("sender_id", utilities.Main.generateUUID());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));


        String id = ArangoInterfaceMethods.insertActivity(obj);
        JSONObject readObj = ArangoInterfaceMethods.getActivity(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value, obj.get(key).toString());

        }
    }

    @Test
    public void updateAndDeleteActivity() {
        JSONObject obj = new JSONObject();
        obj.put("activity_type", "{ type: follow, user_id: 2343-2342 }");
        obj.put("receiver_id", utilities.Main.generateUUID());
        obj.put("sender_id", utilities.Main.generateUUID());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));

        JSONObject updatedObj = new JSONObject();
        updatedObj.put("activity_type", "{ type: tag, user_id: 2343-2342 }");
        updatedObj.put("receiver_id", utilities.Main.generateUUID());
        updatedObj.put("sender_id", utilities.Main.generateUUID());
        updatedObj.put("created_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("blocked_at", new Timestamp(System.currentTimeMillis()));


        String id = ArangoInterfaceMethods.insertActivity(obj);
        ArangoInterfaceMethods.updateActivity(id, updatedObj);
        JSONObject jsonNotification = ArangoInterfaceMethods.getActivity(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonNotification).get("activity_type"),
                "{ type: tag, user_id: 2343-2342 }");

        ArangoInterfaceMethods.deleteActivity(id);
        Assert.assertEquals(ArangoInterfaceMethods.getActivity(id), null);


    }

    @Test
    public void insertAndGetStory() {

        String UUID = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("user_id",UUID);
        obj.put("is_featured", false);
        obj.put("media_id", utilities.Main.generateUUID());
        obj.put("reports", new ArrayList<String>());
        obj.put("seen_by_users_ids", new ArrayList<String>());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at", new Timestamp(System.currentTimeMillis()));
        obj.put("expired_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));

        String id = ArangoInterfaceMethods.insertStory(obj);
        JSONObject readObj = ArangoInterfaceMethods.getStory(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value, obj.get(key).toString());

        }
    }

    @Test
    public void updateAndDeleteStory() {
        String UUID = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("user_id", UUID);
        obj.put("is_featured", false);
        obj.put("media_id", utilities.Main.generateUUID());
        obj.put("reports", new ArrayList<String>());
        obj.put("seen_by_users_ids", new ArrayList<String>());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at", new Timestamp(System.currentTimeMillis()));
        obj.put("expired_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));


        JSONObject updatedObj = new JSONObject();
        updatedObj.put("user_id", UUID);
        updatedObj.put("is_featured", true);
        updatedObj.put("media_id", utilities.Main.generateUUID());
        updatedObj.put("reports", new ArrayList<String>());
        updatedObj.put("seen_by_users_ids", new ArrayList<String>());
        updatedObj.put("created_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("deleted_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("expired_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("blocked_at", new Timestamp(System.currentTimeMillis()));


        String id = ArangoInterfaceMethods.insertStory(obj);
        ArangoInterfaceMethods.updateStory(id, updatedObj);
        JSONObject jsonNotification = ArangoInterfaceMethods.getStory(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonNotification).get("is_featured"),
                true);

        ArangoInterfaceMethods.deleteStory(id);
        Assert.assertEquals(ArangoInterfaceMethods.getStory(id), null);

    }

    @Test
    public void insertAndGetPost() throws Exception {

        String UUID = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("user_id", UUID);
        obj.put("caption", "Taken By MiSO EL Gen");
        obj.put("media", new ArrayList<String>());
        obj.put("likes", new ArrayList<String>());
        obj.put("tags", new ArrayList<String>());
        obj.put("location", "{ name: EspressoLab, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        obj.put("comments", new ArrayList<String>());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("updated_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at", new Timestamp(System.currentTimeMillis()));


        String id = ArangoInterfaceMethods.insertPost(obj, UUID);
        JSONObject readObj = ArangoInterfaceMethods.getPost(id);
        System.out.println(readObj);
        Assert.assertEquals(readObj.get("caption"), obj.get("caption").toString());
    }


    @Test
    public void updateAndDeletePost() throws Exception {

        String UUID1 = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("user_id", UUID1);
        obj.put("caption", "Taken By MiSO EL Gen");
        obj.put("media", new ArrayList<String>());
        obj.put("likes", new ArrayList<String>());
        obj.put("tags", new ArrayList<String>());
        obj.put("location", "{ name: EspressoLab, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        obj.put("comments", new ArrayList<String>());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("updated_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at", new Timestamp(System.currentTimeMillis()));

        JSONObject updatedObj = new JSONObject();
        updatedObj.put("user_id", UUID1);
        updatedObj.put("caption", "Friends");
        updatedObj.put("media", new ArrayList<String>());
        updatedObj.put("likes", new ArrayList<String>());
        updatedObj.put("tags", new ArrayList<String>());
        updatedObj.put("location", "{ name: EspressoLab, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        updatedObj.put("comments", new ArrayList<String>());
        updatedObj.put("created_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("updated_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("blocked_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("deleted_at", new Timestamp(System.currentTimeMillis()));


        String id = ArangoInterfaceMethods.insertPost(obj, UUID1);
        ArangoInterfaceMethods.updatePost(id, updatedObj);
        JSONObject jsonNotification = ArangoInterfaceMethods.getPost(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonNotification).get("caption"),
                "Friends");

        ArangoInterfaceMethods.deletePost(id);
        Assert.assertEquals(ArangoInterfaceMethods.getStory(id), null);
    }

    @Test
    public void insertAndGetBookmark() {

        JSONObject obj = new JSONObject();
        obj.put("posts_ids", new ArrayList<String>());
        obj.put("user_id", utilities.Main.generateUUID());

        String id = ArangoInterfaceMethods.insertBookmark(obj);
        JSONObject readObj = ArangoInterfaceMethods.getBookmark(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value, obj.get(key).toString());

        }

    }

    @Test
    public void updateAndDeleteBookmark() {

        JSONObject obj = new JSONObject();
        obj.put("user_id", utilities.Main.generateUUID());
        obj.put("posts_ids", new ArrayList<String>());

        JSONObject updatedObj = new JSONObject();
        ArrayList<String> post_ids = new ArrayList<>();
        post_ids.add(utilities.Main.generateUUID());
        post_ids.add(utilities.Main.generateUUID());


        updatedObj.put("user_id", utilities.Main.generateUUID());
        updatedObj.put("posts_ids", post_ids);


        String id = ArangoInterfaceMethods.insertBookmark(obj);
        ArangoInterfaceMethods.updateBookmark(id, updatedObj);
        JSONObject jsonBookmark = ArangoInterfaceMethods.getBookmark(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonBookmark).get("posts_ids").toString(),
                updatedObj.get("posts_ids").toString());


        ArangoInterfaceMethods.deleteBookmark(id);
        Assert.assertEquals(ArangoInterfaceMethods.getBookmark(id), null);


    }


    @Test
    public void insertCommentInPost() throws Exception {

        String UUID1 = utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("user_id", UUID1);
        obj.put("caption", "Taken By MiSO EL Gen");
        obj.put("media", new ArrayList<String>());
        obj.put("likes", new ArrayList<String>());
        obj.put("tags", new ArrayList<String>());
//        obj.put("location", "{ name: EspressoLab, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        obj.put("comments", new ArrayList<String>());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("updated_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at", new Timestamp(System.currentTimeMillis()));

        String id = ArangoInterfaceMethods.insertPost(obj, UUID1);
        JSONObject comment = new JSONObject();
        comment.put("content", "Hello");
        System.out.println("kiki");
        ArangoInterfaceMethods.insertCommentOnPost(id, comment);


        JSONObject fetchedPost = ArangoInterfaceMethods.getPost(id);



        Assert.assertTrue(fetchedPost.get("comments").toString().contains(comment.toString()));
    }

    @Test
    public void followTest() {
        followUser("9087b6df-b6f5-4de5-856b-a965c1e3d829", "2af9121b-89a1-4365-83e8-96be1a7f2847");
        followUser("9087b6df-b6f5-4de5-856b-a965c1e3d829", "302d0e85-91be-46c2-ac71-2a4991207d3b");

        followUser("768a9e00-3d8e-4274-8f21-de6a76c64456", "9087b6df-b6f5-4de5-856b-a965c1e3d829");
        followUser("20981745-ca25-483f-a831-edd6c1ffcade", "9087b6df-b6f5-4de5-856b-a965c1e3d829");
        followUser("2af9121b-89a1-4365-83e8-96be1a7f2847", "9087b6df-b6f5-4de5-856b-a965c1e3d829");
        followUser("302d0e85-91be-46c2-ac71-2a4991207d3b", "9087b6df-b6f5-4de5-856b-a965c1e3d829");

        ArrayList<String> following = getAllfollowingIDs("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(following.size(), 2);

        ArrayList<String> followers = getAllfollowersIDs("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(followers.size(), 4);

        unFollowUser("9087b6df-b6f5-4de5-856b-a965c1e3d829", "2af9121b-89a1-4365-83e8-96be1a7f2847");

        ArrayList<String> followingAfterUnfollow = getAllfollowingIDs("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(followingAfterUnfollow.size(), 1);

        String newUserUUID = UUID.randomUUID().toString();
        makeUserNode(newUserUUID);

        followUser("9087b6df-b6f5-4de5-856b-a965c1e3d829", "" + newUserUUID);
        ArrayList<String> newFollowing = getAllfollowingIDs("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(newFollowing.size(), 2);
        ;

//        removeUserNode("f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
//        ArrayList<String> emptyFollowing = getAllfollowingIDs("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
//        Assert.assertEquals(emptyFollowing.size(),0);

        Assert.assertTrue(isFollowing("9087b6df-b6f5-4de5-856b-a965c1e3d829", "302d0e85-91be-46c2-ac71-2a4991207d3b"));
        Assert.assertFalse(isFollowing("768a9e00-3d8e-4274-8f21-de6a76c64456", "20981745-ca25-483f-a831-edd6c1ffcade"));
    }

    @Test
    public void interactTest() {

        makeHashtagNode("manU");
        makeHashtagNode("pancakes");
        makeHashtagNode("3eesh_namlla_takol_sokar");

        followHashtag("9087b6df-b6f5-4de5-856b-a965c1e3d829", "manU");
        followHashtag("9087b6df-b6f5-4de5-856b-a965c1e3d829", "pancakes");
        followHashtag("9087b6df-b6f5-4de5-856b-a965c1e3d829", "3eesh_namlla_takol_sokar");

        followHashtag("768a9e00-3d8e-4274-8f21-de6a76c64456", "manU");

        ArrayList<String> myHashtags = getAllFollowingHashtags("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(myHashtags.size(), 3);

        ArrayList<String> hashtagFollowers = getAllHashtagFollowers("manU");
        Assert.assertEquals(hashtagFollowers.size(), 2);

        unFolllowHashtag("9087b6df-b6f5-4de5-856b-a965c1e3d829", "manU");
        ArrayList<String> myHashtagsUpdated = getAllFollowingHashtags("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(myHashtagsUpdated.size(), 2);


        Assert.assertTrue(isInteracting("9087b6df-b6f5-4de5-856b-a965c1e3d829", "pancakes"));
        Assert.assertFalse(isInteracting("768a9e00-3d8e-4274-8f21-de6a76c64456", "pancakes"));
    }

    @Test
    public void tagUserTest() throws Exception {
        String UUID1 = utilities.Main.generateUUID() ;
        String UUID2 = utilities.Main.generateUUID() ;
        JSONObject obj1= new JSONObject();
        obj1.put("user_id",UUID1);
        obj1.put("caption","Taken By MiSO EL Gen");
        obj1.put("media", new ArrayList<String>());
        obj1.put("likes", new ArrayList<String>());
        obj1.put("tags",new ArrayList<String>());
        obj1.put("location","{ name: EspressoLab, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        obj1.put("comments", new ArrayList<String>());
        obj1.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj1.put("updated_at",new Timestamp(System.currentTimeMillis()));
        obj1.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj1.put("deleted_at",new Timestamp(System.currentTimeMillis()));

        JSONObject obj2= new JSONObject();
        obj2.put("user_id",UUID2);
        obj2.put("caption","Taken By Mohamed ABouzeid");
        obj2.put("media", new ArrayList<String>());
        obj2.put("likes", new ArrayList<String>());
        obj2.put("tags",new ArrayList<String>());
        obj2.put("location","{ name: C1, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        obj2.put("comments", new ArrayList<String>());
        obj2.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj2.put("updated_at",new Timestamp(System.currentTimeMillis()));
        obj2.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj2.put("deleted_at",new Timestamp(System.currentTimeMillis()));

        String id1 = ArangoInterfaceMethods.insertPost(obj1,UUID1);
        String id2 = ArangoInterfaceMethods.insertPost(obj2,UUID2);

        tagUserInPost("9087b6df-b6f5-4de5-856b-a965c1e3d829", ""+id1);
        tagUserInPost("768a9e00-3d8e-4274-8f21-de6a76c64456", ""+id1);
        tagUserInPost("2af9121b-89a1-4365-83e8-96be1a7f2847", ""+id1);

        tagUserInPost("9087b6df-b6f5-4de5-856b-a965c1e3d829", ""+id2);

        ArrayList<String> posts = getAllTaggedPosts("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(posts.size(),2);

        ArrayList<String> ids = getAllUsersTaggedInAPost(id1);
        Assert.assertEquals(ids.size(),3);

        untagUser("768a9e00-3d8e-4274-8f21-de6a76c64456",id1);
        ArrayList<String> idsAfterUntagging = getAllUsersTaggedInAPost(id1);
        Assert.assertEquals(idsAfterUntagging.size(),2);

        Assert.assertTrue(isTaggedUser("9087b6df-b6f5-4de5-856b-a965c1e3d829",""+id1));
        Assert.assertFalse(isTaggedUser("2af9121b-89a1-4365-83e8-96be1a7f2847",""+id2));

    }

    @Test
    public void tagPostTest() throws Exception {

        String UUID1 = utilities.Main.generateUUID() ;
        String UUID2 = utilities.Main.generateUUID() ;

        JSONObject obj1= new JSONObject();
        obj1.put("user_id",UUID1);
        obj1.put("caption","Taken By MiSO EL Gen");
        obj1.put("media", new ArrayList<String>());
        obj1.put("likes", new ArrayList<String>());
        obj1.put("tags",new ArrayList<String>());
        obj1.put("location","{ name: EspressoLab, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        obj1.put("comments", new ArrayList<String>());
        obj1.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj1.put("updated_at",new Timestamp(System.currentTimeMillis()));
        obj1.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj1.put("deleted_at",new Timestamp(System.currentTimeMillis()));

        JSONObject obj2= new JSONObject();
        obj2.put("user_id",UUID2);
        obj2.put("caption","Taken By Mohamed ABouzeid");
        obj2.put("media", new ArrayList<String>());
        obj2.put("likes", new ArrayList<String>());
        obj2.put("tags",new ArrayList<String>());
        obj2.put("location","{ name: C1, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        obj2.put("comments", new ArrayList<String>());
        obj2.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj2.put("updated_at",new Timestamp(System.currentTimeMillis()));
        obj2.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj2.put("deleted_at",new Timestamp(System.currentTimeMillis()));

        String id1 = ArangoInterfaceMethods.insertPost(obj1,UUID1);
        String id2 = ArangoInterfaceMethods.insertPost(obj2,UUID2);

        makeHashtagNode("Chelsea");
        makeHashtagNode("Liverpool");
        makeHashtagNode("from_Russia_with_love");

        tagPostInHashtag(""+id1, "Chelsea");
        tagPostInHashtag(""+id1, "Liverpool");
        tagPostInHashtag(""+id1, "from_Russia_with_love");


        tagPostInHashtag(""+id2, "Chelsea");

        ArrayList<String> hashtags = getAllHashtagsTaggedInPost(""+id1);
        Assert.assertEquals(hashtags.size(),3);

        ArrayList<String> posts = getAllPostsTaggedInHashtag("Chelsea");
        Assert.assertEquals(posts.size(),2);

        untagPost(""+id1,"from_Russia_with_love");
        ArrayList<String> hashtagsAfterUntagging = getAllHashtagsTaggedInPost(""+id1);
        Assert.assertEquals(hashtagsAfterUntagging.size(),2);

        Assert.assertTrue(isTaggedPost(""+id1, "Liverpool"));
        Assert.assertFalse(isTaggedPost(""+id1, "from_Russia_with_love"));

    }

    @Test
    public void getFeedTest() throws Exception {
        followUser("3a1eeb08-db5c-4a85-8e44-655165a916d4", "11650791-defe-49fe-b2ca-fdfd86e614bf");
        followUser("3a1eeb08-db5c-4a85-8e44-655165a916d4", "cd3d4b90-1834-49a3-afcb-39360a6bdaeb");
        followUser("3a1eeb08-db5c-4a85-8e44-655165a916d4", "42686b65-3ed2-4082-990a-fb4cc3573f73");
        followUser("3a1eeb08-db5c-4a85-8e44-655165a916d4", "b4469b50-43a6-419b-a5fc-66eb53a77897");
        followUser("3a1eeb08-db5c-4a85-8e44-655165a916d4", "70b8ebdf-7b70-4534-addc-5fe05a2c6112");
        followUser("3a1eeb08-db5c-4a85-8e44-655165a916d4", "a4f21310-30b6-4003-9535-8eeaab968f21");

        JSONObject obj1= new JSONObject();
        obj1.put("caption","sawa7el EL Shaba7 AWIII AWIII Ba7ebak w ba7eb 3id miladak ya soso el shaba7");
        obj1.put("media", new ArrayList<String>());
        obj1.put("likes", new ArrayList<String>());
        obj1.put("tags",new ArrayList<String>());
        obj1.put("location","{ name: EspressoLab, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        obj1.put("comments", new ArrayList<String>());
        obj1.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj1.put("updated_at",new Timestamp(System.currentTimeMillis()));
        obj1.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj1.put("deleted_at",new Timestamp(System.currentTimeMillis()));

        JSONObject obj2= new JSONObject();
        obj2.put("caption","LINA w Messiry w ABouzeid a3deen byshtaghalo m3 b3d");
        obj2.put("media", new ArrayList<String>());
        obj2.put("likes", new ArrayList<String>());
        obj2.put("tags",new ArrayList<String>());
        obj2.put("location","{ name: D, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        obj2.put("comments", new ArrayList<String>());
        obj2.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj2.put("updated_at",new Timestamp(System.currentTimeMillis()));
        obj2.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj2.put("deleted_at",new Timestamp(System.currentTimeMillis()));

        JSONObject obj3= new JSONObject();
        obj3.put("caption","hopaa hopa hopaa");
        obj3.put("media", new ArrayList<String>());
        obj3.put("likes", new ArrayList<String>());
        obj3.put("tags",new ArrayList<String>());
        obj3.put("location","{ name: hopa, coordinates:{long: 1.22.01.01, lat: 2.1.0.10} }");
        obj3.put("comments", new ArrayList<String>());
        obj3.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj3.put("updated_at",new Timestamp(System.currentTimeMillis()));
        obj3.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj3.put("deleted_at",new Timestamp(System.currentTimeMillis()));


        ArangoInterfaceMethods.insertPost(obj1,"11650791-defe-49fe-b2ca-fdfd86e614bf");
        ArangoInterfaceMethods.insertPost(obj1,"42686b65-3ed2-4082-990a-fb4cc3573f73");
        ArangoInterfaceMethods.insertPost(obj1,"cd3d4b90-1834-49a3-afcb-39360a6bdaeb");
        ArangoInterfaceMethods.insertPost(obj1,"70b8ebdf-7b70-4534-addc-5fe05a2c6112");
        ArangoInterfaceMethods.insertPost(obj1,"3a1eeb08-db5c-4a85-8e44-655165a916d4");

        ArangoInterfaceMethods.insertPost(obj2,"11650791-defe-49fe-b2ca-fdfd86e614bf");
        ArangoInterfaceMethods.insertPost(obj2,"42686b65-3ed2-4082-990a-fb4cc3573f73");
        ArangoInterfaceMethods.insertPost(obj2,"cd3d4b90-1834-49a3-afcb-39360a6bdaeb");
        ArangoInterfaceMethods.insertPost(obj2,"70b8ebdf-7b70-4534-addc-5fe05a2c6112");
        ArangoInterfaceMethods.insertPost(obj2,"3a1eeb08-db5c-4a85-8e44-655165a916d4");

        ArangoInterfaceMethods.insertPost(obj3,"cd3d4b90-1834-49a3-afcb-39360a6bdaeb");

        Assert.assertEquals(9,getFeed("3a1eeb08-db5c-4a85-8e44-655165a916d4", 10,0).size());
        Assert.assertEquals(3,getFeed("3a1eeb08-db5c-4a85-8e44-655165a916d4", 3,0).size()) ;
        Assert.assertEquals(8,getFeed("3a1eeb08-db5c-4a85-8e44-655165a916d4", 10,1).size());

    }

    @Test
    public void getStoriesTest(){
        JSONObject obj = new JSONObject();
        obj.put("user_id", "11650791-defe-49fe-b2ca-fdfd86e614bf");
        obj.put("is_featured", false);
        obj.put("media_id", utilities.Main.generateUUID());
        obj.put("reports", new ArrayList<String>());
        obj.put("seen_by_users_ids", new ArrayList<String>());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at", new Timestamp(System.currentTimeMillis()));
        obj.put("expired_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));

        String id1 = ArangoInterfaceMethods.insertStory(obj);

        obj.put("user_id","42686b65-3ed2-4082-990a-fb4cc3573f73");
        String id2 = ArangoInterfaceMethods.insertStory(obj);

        obj.put("user_id","cd3d4b90-1834-49a3-afcb-39360a6bdaeb");
        String id3 = ArangoInterfaceMethods.insertStory(obj);

        obj.put("user_id","70b8ebdf-7b70-4534-addc-5fe05a2c6112");
        String id4 = ArangoInterfaceMethods.insertStory(obj);

        JSONArray friendsStories = getFriendsStories("3a1eeb08-db5c-4a85-8e44-655165a916d4");
        Assert.assertEquals(4,friendsStories.length());
        Assert.assertEquals(1, ((JSONArray) ((JSONObject) friendsStories.get(0)).get("stories")).length());
    }


    @Test
    public void getDiscoveryFeedTests() throws Exception {
        followUser("f7d59c0a-b9c9-4cc9-ba46-0d79d09eea7b", "12564536-142f-47a3-95b8-02e02269eb7c");

        followUser("12564536-142f-47a3-95b8-02e02269eb7c", "9040bff3-9d59-4c5c-a37d-a53f648b15f7");
        followUser("12564536-142f-47a3-95b8-02e02269eb7c", "cfeef461-85ec-4468-be2b-a50de09c7b5a");
        followUser("12564536-142f-47a3-95b8-02e02269eb7c", "fb4c8107-3e96-42e9-be49-13dec0fb2107");
        followUser("12564536-142f-47a3-95b8-02e02269eb7c", "4198441c-aa1f-4d97-809b-b1ec950c294d");
        followUser("12564536-142f-47a3-95b8-02e02269eb7c", "c84a6580-6d99-49db-b034-599c03026e04");
        followUser("12564536-142f-47a3-95b8-02e02269eb7c", "b452dd80-9801-457c-b574-4cecc5045340");
        followUser("12564536-142f-47a3-95b8-02e02269eb7c", "f7d59c0a-b9c9-4cc9-ba46-0d79d09eea7b");
        followUser("12564536-142f-47a3-95b8-02e02269eb7c", "9087b6df-b6f5-4de5-856b-a965c1e3d829");

        JSONObject obj1= new JSONObject();
        obj1.put("caption","Ya raye2 ya ray2");
        obj1.put("media", new ArrayList<String>());
        obj1.put("likes", new ArrayList<String>());
        obj1.put("tags",new ArrayList<String>());
        obj1.put("location","{ name: EspressoLab, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        obj1.put("comments", new ArrayList<String>());
        obj1.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj1.put("updated_at",new Timestamp(System.currentTimeMillis()));
        obj1.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj1.put("deleted_at",new Timestamp(System.currentTimeMillis()));

        JSONObject obj2= new JSONObject();
        obj2.put("caption","Piza La2");
        obj2.put("media", new ArrayList<String>());
        obj2.put("likes", new ArrayList<String>());
        obj2.put("tags",new ArrayList<String>());
        obj2.put("location","{ name: D, coordinates:{long: 1.0.01.01, lat: 2.1.0.10} }");
        obj2.put("comments", new ArrayList<String>());
        obj2.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj2.put("updated_at",new Timestamp(System.currentTimeMillis()));
        obj2.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj2.put("deleted_at",new Timestamp(System.currentTimeMillis()));

        JSONObject obj3= new JSONObject();
        obj3.put("caption","lo2lo2a");
        obj3.put("media", new ArrayList<String>());
        obj3.put("likes", new ArrayList<String>());
        obj3.put("tags",new ArrayList<String>());
        obj3.put("location","{ name: hopa, coordinates:{long: 1.22.01.01, lat: 2.1.0.10} }");
        obj3.put("comments", new ArrayList<String>());
        obj3.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj3.put("updated_at",new Timestamp(System.currentTimeMillis()));
        obj3.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj3.put("deleted_at",new Timestamp(System.currentTimeMillis()));

        ArangoInterfaceMethods.insertPost(obj1,"9040bff3-9d59-4c5c-a37d-a53f648b15f7");
        ArangoInterfaceMethods.insertPost(obj2,"9040bff3-9d59-4c5c-a37d-a53f648b15f7");
        ArangoInterfaceMethods.insertPost(obj3,"9040bff3-9d59-4c5c-a37d-a53f648b15f7");

        Assert.assertEquals(3,getDiscoveryFeed("f7d59c0a-b9c9-4cc9-ba46-0d79d09eea7b",100,0).size());

    }

    @Test
    public void getDiscoverStoriesTest(){
        followUser("e03168b3-226a-415d-9838-524f104f6348", "4441c5b9-a459-48f0-ab39-afec995746a3");

        followUser("4441c5b9-a459-48f0-ab39-afec995746a3", "4c22c88e-69b3-46a3-b159-c394856a5355");
        followUser("4441c5b9-a459-48f0-ab39-afec995746a3", "9235f108-fc46-4308-870d-bb0b1542bdab");
        followUser("4441c5b9-a459-48f0-ab39-afec995746a3", "da1f5e4a-98bf-4873-8327-aadaf3d73ad4");
        followUser("4441c5b9-a459-48f0-ab39-afec995746a3", "1c139068-1ffe-4c5c-896c-09bba1e8ce90");

        JSONObject obj = new JSONObject();
        obj.put("user_id", "4c22c88e-69b3-46a3-b159-c394856a5355");
        obj.put("is_featured", false);
        obj.put("media_id", utilities.Main.generateUUID());
        obj.put("reports", new ArrayList<String>());
        obj.put("seen_by_users_ids", new ArrayList<String>());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at", new Timestamp(System.currentTimeMillis()));
        obj.put("expired_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));

        String id1 = ArangoInterfaceMethods.insertStory(obj);

        obj.put("user_id","9235f108-fc46-4308-870d-bb0b1542bdab");
        String id2 = ArangoInterfaceMethods.insertStory(obj);

        obj.put("user_id","da1f5e4a-98bf-4873-8327-aadaf3d73ad4");
        String id3 = ArangoInterfaceMethods.insertStory(obj);

        obj.put("user_id","1c139068-1ffe-4c5c-896c-09bba1e8ce90");
        String id4 = ArangoInterfaceMethods.insertStory(obj);

        JSONArray friendsStories = getDiscoverStories("e03168b3-226a-415d-9838-524f104f6348");
        Assert.assertEquals(4,friendsStories.length());

    }

    @Test
    public void notificationsTest() throws Exception {
        String userId1 = utilities.Main.generateUUID() ;
        String userId2 = utilities.Main.generateUUID() ;

        JSONObject post = new JSONObject();

        post.put("user_id",userId1);
        post.put("caption","hello");
        post.put("media",new ArrayList<String>());
        post.put("comments",new ArrayList<String>());
        String postID = insertPost(post,userId1);
        followUser(userId2,userId1);
        likePost(postID,userId2);
        String receiverId = userId1;
        JSONObject notificationJSON = new JSONObject();
        JSONObject innerJSON = new JSONObject();
        innerJSON.put("type", "liking_post");
        innerJSON.put("post_id",postID);
        notificationJSON.put("activity_type",innerJSON);
        notificationJSON.put("sender_id", userId2);
        notificationJSON.put("receiver_id", receiverId);
        notificationJSON.put("created_at",new java.util.Date());
        notificationJSON.put("blocked_at","null");
        notificationJSON.put("id",utilities.Main.generateUUID());
        insertNotification(notificationJSON);
        insertActivity(notificationJSON);

        Assert.assertTrue(getNotifications(userId1,0,5).length()==1);
    }

    @Test
    public void ActivitiesTest() throws Exception {
        String userId1 = utilities.Main.generateUUID() ;
        String userId2 = utilities.Main.generateUUID() ;
        String userId3 = utilities.Main.generateUUID() ;

        makeUserNode(userId1);
        makeUserNode(userId2);
        makeUserNode(userId3);

        JSONObject post = new JSONObject();

        post.put("user_id",userId1);
        post.put("caption","hello");
        post.put("media",new ArrayList<String>());
        post.put("comments",new ArrayList<String>());
        String postID = insertPost(post,userId1);
        followUser(userId2,userId1);
        followUser(userId3,userId2);
        likePost(postID,userId2);
        JSONObject comment = new JSONObject();
        comment.put("content","hello");
        comment.put("user_id",userId2);
        insertCommentOnPost(postID,comment);
        String receiverId = userId1;
        JSONObject notificationJSON = new JSONObject();
        JSONObject innerJSON = new JSONObject();
        innerJSON.put("type", "liking_post");
        innerJSON.put("post_id",postID);
        notificationJSON.put("activity_type",innerJSON);
        notificationJSON.put("sender_id", userId2);
        notificationJSON.put("receiver_id", receiverId);
        notificationJSON.put("created_at",new java.util.Date());
        notificationJSON.put("blocked_at","null");
        notificationJSON.put("id",utilities.Main.generateUUID());
        insertNotification(notificationJSON);
        insertActivity(notificationJSON);

        Assert.assertTrue(getNotifications(userId1,0,5).length()==1);

        ArrayList<String> followings = getAllfollowingIDs(userId3);
        Assert.assertEquals(getActivities(followings,0,5).length(), 1);

    }


}