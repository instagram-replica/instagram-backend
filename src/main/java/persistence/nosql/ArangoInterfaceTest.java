package persistence.nosql;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.EdgeDefinition;
import com.arangodb.entity.GraphEntity;
import com.arangodb.model.GraphCreateOptions;
import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;
import persistence.sql.users.Database;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static persistence.nosql.ArangoInterfaceMethods.*;
import static persistence.sql.users.Database.getAllUsersIds;
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
    private static final String graphUserBlockedCollectionName = "UserBlocked";
    private static final String graphUserReportedCollectionName = "UserReported";
    private static final String graphUserConnectedToThreadCollectionName = "UserConnectedToThread";

    private static final String graphName = "InstagramGraph";


    @BeforeClass
    public static void setUp() throws IOException, SQLException {
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

        List<String> user_ids = Database.getAllUsersIds();
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


            user_ids.forEach(GraphMethods::makeUserNode);
            user_ids.forEach(System.out::println);

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
    public void insertAndGetThread() throws Exception{
        JSONObject obj = new JSONObject();
        obj.put("creator_id", utilities.Main.generateUUID());
        obj.put("users_ids", new ArrayList<String>());
        obj.put("name", "Ahmed");
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));
        obj.put("messages", new ArrayList<String>());


        String id = ThreadMethods.insertThread(obj);
        JSONObject readObj = ThreadMethods.getThread(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value, obj.get(key).toString());

        }
    }

    @Test
    public void updateAndDeleteThread() throws Exception{
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


        String id = ThreadMethods.insertThread(obj);
        ThreadMethods.updateThread(id, updatedObj);
        JSONObject jsonThread = ThreadMethods.getThread(id);
        Assert.assertEquals(Objects.requireNonNull(jsonThread).get("name"), "Mohamed");

        ThreadMethods.deleteThread(id);
        try{
            ThreadMethods.getThread(id);
            Assert.assertTrue(false);
        }catch (CustomException e){
            Assert.assertTrue(true);
        }


    }

    @Test
    public void insertAndGetNotification() throws Exception{
        JSONObject obj = new JSONObject();
        obj.put("activity_type", "{ type: follow, user_id: 2343-2342");
        obj.put("receiver_id", utilities.Main.generateUUID());
        obj.put("sender_id", utilities.Main.generateUUID());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));


        String id = ActivityMethods.insertNotification(obj);
        JSONObject readObj = ActivityMethods.getNotification(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value, obj.get(key).toString());

        }
    }

    @Test
    public void updateAndDeleteNotification() throws Exception{
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


        String id = ActivityMethods.insertNotification(obj);
        ActivityMethods.updateNotification(id, updatedObj);
        JSONObject jsonNotification = ActivityMethods.getNotification(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonNotification).get("activity_type"),
                "{ type: tag, user_id: 2343-2342 }");

        ActivityMethods.deleteNotification(id);
        try{
            ActivityMethods.getNotification(id);
            Assert.assertTrue(false);
        }catch (CustomException e){
            Assert.assertTrue(true);
        }

    }

    @Test
    public void insertAndGetActivity() throws Exception{
        JSONObject obj = new JSONObject();
        obj.put("activity_type", "{ type: follow, user_id: 2343-2342");
        obj.put("receiver_id", utilities.Main.generateUUID());
        obj.put("sender_id", utilities.Main.generateUUID());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));


        String id = ActivityMethods.insertActivity(obj);
        JSONObject readObj = ActivityMethods.getActivity(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value, obj.get(key).toString());

        }
    }

    @Test
    public void updateAndDeleteActivity() throws Exception{
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


        String id = ActivityMethods.insertActivity(obj);
        ActivityMethods.updateActivity(id, updatedObj);
        JSONObject jsonNotification = ActivityMethods.getActivity(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonNotification).get("activity_type"),
                "{ type: tag, user_id: 2343-2342 }");

        ActivityMethods.deleteActivity(id);
        try{
            ActivityMethods.getActivity(id);
            Assert.assertTrue(false);
        }catch (CustomException e){
            Assert.assertTrue(true);
        }

    }

    @Test
    public void insertAndGetStory() throws Exception{

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

        String id = StoriesMethods.insertStory(obj);
        JSONObject readObj = StoriesMethods.getStory(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value, obj.get(key).toString());

        }
    }

    @Test
    public void updateAndDeleteStory() throws Exception{
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


        String id = StoriesMethods.insertStory(obj);
        StoriesMethods.updateStory(id, updatedObj);
        JSONObject jsonNotification = StoriesMethods.getStory(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonNotification).get("is_featured"),
                true);

        StoriesMethods.deleteStory(id);
        try{
            StoriesMethods.getStory(id);
            Assert.assertTrue(false);
        }catch (CustomException e){
            Assert.assertTrue(true);
        }

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


        String id = PostMethods.insertPost(obj, UUID);
        JSONObject readObj = PostMethods.getPost(id);
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


        String id = PostMethods.insertPost(obj, UUID1);
        PostMethods.updatePost(id, updatedObj);
        JSONObject jsonNotification = PostMethods.getPost(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonNotification).get("caption"),
                "Friends");

        PostMethods.deletePost(id);
        try{
            PostMethods.getPost(id);
            Assert.assertTrue(false);
        }catch (CustomException e){
            Assert.assertTrue(true);
        }

    }

    @Test
    public void insertAndGetBookmark() throws Exception{

        JSONObject obj = new JSONObject();
        obj.put("posts_ids", new ArrayList<String>());
        obj.put("user_id", utilities.Main.generateUUID());

        String id = BookmarkPosts.insertBookmark(obj);
        JSONObject readObj = BookmarkPosts.getBookmark(id);
        Iterator iterator = Objects.requireNonNull(readObj).keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = readObj.get(key).toString();
            Assert.assertEquals(value, obj.get(key).toString());

        }

    }

    @Test
    public void updateAndDeleteBookmark() throws Exception{

        JSONObject obj = new JSONObject();
        obj.put("user_id", utilities.Main.generateUUID());
        obj.put("posts_ids", new ArrayList<String>());

        JSONObject updatedObj = new JSONObject();
        ArrayList<String> post_ids = new ArrayList<>();
        post_ids.add(utilities.Main.generateUUID());
        post_ids.add(utilities.Main.generateUUID());


        updatedObj.put("user_id", utilities.Main.generateUUID());
        updatedObj.put("posts_ids", post_ids);


        String id = BookmarkPosts.insertBookmark(obj);
        BookmarkPosts.updateBookmark(id, updatedObj);
        JSONObject jsonBookmark = BookmarkPosts.getBookmark(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonBookmark).get("posts_ids").toString(),
                updatedObj.get("posts_ids").toString());

        BookmarkPosts.deleteBookmark(id);
        try{
            BookmarkPosts.getBookmark(id);
            Assert.assertTrue(false);
        }catch (CustomException e){
            Assert.assertTrue(true);
        }
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

        String id = PostMethods.insertPost(obj, UUID1);
        JSONObject comment = new JSONObject();
        comment.put("content", "Hello");
        System.out.println("kiki");
        PostMethods.insertCommentOnPost(id, comment);


        JSONObject fetchedPost = PostMethods.getPost(id);



        Assert.assertTrue(fetchedPost.get("comments").toString().contains(comment.toString()));
    }

    @Test
    public void followTest() {
        GraphMethods.followUser("9087b6df-b6f5-4de5-856b-a965c1e3d829", "2af9121b-89a1-4365-83e8-96be1a7f2847");
        GraphMethods.followUser("9087b6df-b6f5-4de5-856b-a965c1e3d829", "302d0e85-91be-46c2-ac71-2a4991207d3b");

        GraphMethods.followUser("768a9e00-3d8e-4274-8f21-de6a76c64456", "9087b6df-b6f5-4de5-856b-a965c1e3d829");
        GraphMethods.followUser("20981745-ca25-483f-a831-edd6c1ffcade", "9087b6df-b6f5-4de5-856b-a965c1e3d829");
        GraphMethods.followUser("2af9121b-89a1-4365-83e8-96be1a7f2847", "9087b6df-b6f5-4de5-856b-a965c1e3d829");
        GraphMethods.followUser("302d0e85-91be-46c2-ac71-2a4991207d3b", "9087b6df-b6f5-4de5-856b-a965c1e3d829");

        ArrayList<String> following = GraphMethods.getAllfollowingIDs("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(following.size(), 2);

        ArrayList<String> followers = GraphMethods.getAllfollowersIDs("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(followers.size(), 4);

        GraphMethods.unFollowUser("9087b6df-b6f5-4de5-856b-a965c1e3d829", "2af9121b-89a1-4365-83e8-96be1a7f2847");

        ArrayList<String> followingAfterUnfollow = GraphMethods.getAllfollowingIDs("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(followingAfterUnfollow.size(), 1);

        String newUserUUID = UUID.randomUUID().toString();
        GraphMethods.makeUserNode(newUserUUID);

        GraphMethods.followUser("9087b6df-b6f5-4de5-856b-a965c1e3d829", "" + newUserUUID);
        ArrayList<String> newFollowing = GraphMethods.getAllfollowingIDs("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(newFollowing.size(), 2);
        ;

//        removeUserNode("f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
//        ArrayList<String> emptyFollowing = getAllfollowingIDs("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
//        Assert.assertEquals(emptyFollowing.size(),0);

        Assert.assertTrue(GraphMethods.isFollowing("9087b6df-b6f5-4de5-856b-a965c1e3d829", "302d0e85-91be-46c2-ac71-2a4991207d3b"));
        Assert.assertFalse(GraphMethods.isFollowing("768a9e00-3d8e-4274-8f21-de6a76c64456", "20981745-ca25-483f-a831-edd6c1ffcade"));
    }


    @Test
    public void blockTest() {
        GraphMethods.blockUser("9087b6df-b6f5-4de5-856b-a965c1e3d829", "20981745-ca25-483f-a831-edd6c1ffcade");
        GraphMethods.blockUser("9087b6df-b6f5-4de5-856b-a965c1e3d829", "768a9e00-3d8e-4274-8f21-de6a76c64456");

        ArrayList<String> blocked = GraphMethods.getAllBlockedIDs("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(blocked.size(), 2);

        GraphMethods.unblockUser("9087b6df-b6f5-4de5-856b-a965c1e3d829", "20981745-ca25-483f-a831-edd6c1ffcade");

        ArrayList<String> blockedAfterUblocking = GraphMethods.getAllBlockedIDs("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(blockedAfterUblocking.size(), 1);

        String newUserUUID = UUID.randomUUID().toString();
        GraphMethods.makeUserNode(newUserUUID);

        GraphMethods.blockUser("9087b6df-b6f5-4de5-856b-a965c1e3d829", "" + newUserUUID);
        ArrayList<String> newFollowing = GraphMethods.getAllBlockedIDs("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(newFollowing.size(), 2);

        Assert.assertTrue(GraphMethods.isBlocked("9087b6df-b6f5-4de5-856b-a965c1e3d829", "768a9e00-3d8e-4274-8f21-de6a76c64456"));
        Assert.assertFalse(GraphMethods.isBlocked("768a9e00-3d8e-4274-8f21-de6a76c64456", "20981745-ca25-483f-a831-edd6c1ffcade"));
    }

    @Test
    public void reportTest() {
        GraphMethods.reportUser("9087b6df-b6f5-4de5-856b-a965c1e3d829", "768a9e00-3d8e-4274-8f21-de6a76c64456");

        Assert.assertTrue(GraphMethods.isReported("9087b6df-b6f5-4de5-856b-a965c1e3d829", "768a9e00-3d8e-4274-8f21-de6a76c64456"));
        Assert.assertFalse(GraphMethods.isReported("768a9e00-3d8e-4274-8f21-de6a76c64456", "20981745-ca25-483f-a831-edd6c1ffcade"));
    }

    @Test
    public void userConnectedToThreadTest() {
        JSONObject obj = new JSONObject();
        obj.put("creator_id", utilities.Main.generateUUID());
        obj.put("users_ids", new ArrayList<String>());
        obj.put("name", "Ahmed");
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));
        obj.put("messages", new ArrayList<String>());


        String id = ThreadMethods.insertThread(obj);

        GraphMethods.joinThread("9087b6df-b6f5-4de5-856b-a965c1e3d829", ""+id);
        GraphMethods.joinThread("20981745-ca25-483f-a831-edd6c1ffcade", ""+id);

        ArrayList<String> threadsForUser = GraphMethods.getAllThreadsForUser("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(threadsForUser.size(), 1);

        ArrayList<String> usersInThreads = GraphMethods.getAllUsersInThread(""+id);
        Assert.assertEquals(usersInThreads.size(), 2);
    }

    @Test
    public void interactTest() throws CustomException {

        GraphMethods.makeHashtagNode("manU");
        GraphMethods.makeHashtagNode("pancakes");
        GraphMethods.makeHashtagNode("3eesh_namlla_takol_sokar");

        GraphMethods.followHashtag("9087b6df-b6f5-4de5-856b-a965c1e3d829", "manU");
        GraphMethods.followHashtag("9087b6df-b6f5-4de5-856b-a965c1e3d829", "pancakes");
        GraphMethods.followHashtag("9087b6df-b6f5-4de5-856b-a965c1e3d829", "3eesh_namlla_takol_sokar");

        GraphMethods.followHashtag("768a9e00-3d8e-4274-8f21-de6a76c64456", "manU");

        ArrayList<String> myHashtags = GraphMethods.getAllFollowingHashtags("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(myHashtags.size(), 3);

        ArrayList<String> hashtagFollowers = GraphMethods.getAllHashtagFollowers("manU");
        Assert.assertEquals(hashtagFollowers.size(), 2);

        GraphMethods.unFolllowHashtag("9087b6df-b6f5-4de5-856b-a965c1e3d829", "manU");
        ArrayList<String> myHashtagsUpdated = GraphMethods.getAllFollowingHashtags("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(myHashtagsUpdated.size(), 2);


        Assert.assertTrue(GraphMethods.isInteracting("9087b6df-b6f5-4de5-856b-a965c1e3d829", "pancakes"));
        Assert.assertFalse(GraphMethods.isInteracting("768a9e00-3d8e-4274-8f21-de6a76c64456", "pancakes"));
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

        String id1 = PostMethods.insertPost(obj1,UUID1);
        String id2 = PostMethods.insertPost(obj2,UUID2);

        GraphMethods.tagUserInPost("9087b6df-b6f5-4de5-856b-a965c1e3d829", ""+id1);
        GraphMethods.tagUserInPost("768a9e00-3d8e-4274-8f21-de6a76c64456", ""+id1);
        GraphMethods.tagUserInPost("2af9121b-89a1-4365-83e8-96be1a7f2847", ""+id1);

        GraphMethods.tagUserInPost("9087b6df-b6f5-4de5-856b-a965c1e3d829", ""+id2);

        ArrayList<String> posts = GraphMethods.getAllTaggedPosts("9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(posts.size(),2);

        ArrayList<String> ids = GraphMethods.getAllUsersTaggedInAPost(id1);
        Assert.assertEquals(ids.size(),3);

        GraphMethods.untagUser("768a9e00-3d8e-4274-8f21-de6a76c64456",id1);
        ArrayList<String> idsAfterUntagging = GraphMethods.getAllUsersTaggedInAPost(id1);
        Assert.assertEquals(idsAfterUntagging.size(),2);

        Assert.assertTrue(GraphMethods.isTaggedUser("9087b6df-b6f5-4de5-856b-a965c1e3d829",""+id1));
        Assert.assertFalse(GraphMethods.isTaggedUser("2af9121b-89a1-4365-83e8-96be1a7f2847",""+id2));

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

        String id1 = PostMethods.insertPost(obj1,UUID1);
        String id2 = PostMethods.insertPost(obj2,UUID2);

        GraphMethods.makeHashtagNode("Chelsea");
        GraphMethods.makeHashtagNode("Liverpool");
        GraphMethods.makeHashtagNode("from_Russia_with_love");

        GraphMethods.tagPostInHashtag(""+id1, "Chelsea");
        GraphMethods.tagPostInHashtag(""+id1, "Liverpool");
        GraphMethods.tagPostInHashtag(""+id1, "from_Russia_with_love");


        GraphMethods.tagPostInHashtag(""+id2, "Chelsea");

        ArrayList<String> hashtags = GraphMethods.getAllHashtagsTaggedInPost(""+id1);
        Assert.assertEquals(hashtags.size(),3);

        ArrayList<String> posts = GraphMethods.getAllPostsTaggedInHashtag("Chelsea");
        Assert.assertEquals(posts.size(),2);

        GraphMethods.untagPost(""+id1,"from_Russia_with_love");
        ArrayList<String> hashtagsAfterUntagging = GraphMethods.getAllHashtagsTaggedInPost(""+id1);
        Assert.assertEquals(hashtagsAfterUntagging.size(),2);

        Assert.assertTrue(GraphMethods.isTaggedPost(""+id1, "Liverpool"));
        Assert.assertFalse(GraphMethods.isTaggedPost(""+id1, "from_Russia_with_love"));

    }

    @Test
    public void getFeedTest() throws Exception {
        GraphMethods.followUser("3a1eeb08-db5c-4a85-8e44-655165a916d4", "11650791-defe-49fe-b2ca-fdfd86e614bf");
        GraphMethods.followUser("3a1eeb08-db5c-4a85-8e44-655165a916d4", "cd3d4b90-1834-49a3-afcb-39360a6bdaeb");
        GraphMethods.followUser("3a1eeb08-db5c-4a85-8e44-655165a916d4", "42686b65-3ed2-4082-990a-fb4cc3573f73");
        GraphMethods.followUser("3a1eeb08-db5c-4a85-8e44-655165a916d4", "b4469b50-43a6-419b-a5fc-66eb53a77897");
        GraphMethods.followUser("3a1eeb08-db5c-4a85-8e44-655165a916d4", "70b8ebdf-7b70-4534-addc-5fe05a2c6112");
        GraphMethods.followUser("3a1eeb08-db5c-4a85-8e44-655165a916d4", "a4f21310-30b6-4003-9535-8eeaab968f21");

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


        PostMethods.insertPost(obj1,"11650791-defe-49fe-b2ca-fdfd86e614bf");
        PostMethods.insertPost(obj1,"42686b65-3ed2-4082-990a-fb4cc3573f73");
        PostMethods.insertPost(obj1,"cd3d4b90-1834-49a3-afcb-39360a6bdaeb");
        PostMethods.insertPost(obj1,"70b8ebdf-7b70-4534-addc-5fe05a2c6112");
        PostMethods.insertPost(obj1,"3a1eeb08-db5c-4a85-8e44-655165a916d4");

        PostMethods.insertPost(obj2,"11650791-defe-49fe-b2ca-fdfd86e614bf");
        PostMethods.insertPost(obj2,"42686b65-3ed2-4082-990a-fb4cc3573f73");
        PostMethods.insertPost(obj2,"cd3d4b90-1834-49a3-afcb-39360a6bdaeb");
        PostMethods.insertPost(obj2,"70b8ebdf-7b70-4534-addc-5fe05a2c6112");
        PostMethods.insertPost(obj2,"3a1eeb08-db5c-4a85-8e44-655165a916d4");

        PostMethods.insertPost(obj3,"cd3d4b90-1834-49a3-afcb-39360a6bdaeb");

        Assert.assertEquals(9,FeedMethods.getFeed("3a1eeb08-db5c-4a85-8e44-655165a916d4", 10,0).size());
        Assert.assertEquals(3,FeedMethods.getFeed("3a1eeb08-db5c-4a85-8e44-655165a916d4", 3,0).size()) ;
        Assert.assertEquals(8,FeedMethods.getFeed("3a1eeb08-db5c-4a85-8e44-655165a916d4", 10,1).size());

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
        obj.put("expired_at", new Timestamp(System.currentTimeMillis()+86400000));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));

        String id1 = StoriesMethods.insertStory(obj);

        obj.put("user_id","42686b65-3ed2-4082-990a-fb4cc3573f73");
        String id2 = StoriesMethods.insertStory(obj);

        obj.put("user_id","cd3d4b90-1834-49a3-afcb-39360a6bdaeb");
        String id3 = StoriesMethods.insertStory(obj);

        obj.put("user_id","70b8ebdf-7b70-4534-addc-5fe05a2c6112");
        String id4 = StoriesMethods.insertStory(obj);

        JSONArray friendsStories = StoriesMethods.getFriendsStories("3a1eeb08-db5c-4a85-8e44-655165a916d4");
        Assert.assertEquals(4,friendsStories.length());
        Assert.assertEquals(1, ((JSONArray) ((JSONObject) friendsStories.get(0)).get("stories")).length());
    }


    @Test
    public void getDiscoveryFeedTests() throws Exception {
        GraphMethods.followUser("f7d59c0a-b9c9-4cc9-ba46-0d79d09eea7b", "12564536-142f-47a3-95b8-02e02269eb7c");

        GraphMethods.followUser("12564536-142f-47a3-95b8-02e02269eb7c", "9040bff3-9d59-4c5c-a37d-a53f648b15f7");
        GraphMethods.followUser("12564536-142f-47a3-95b8-02e02269eb7c", "cfeef461-85ec-4468-be2b-a50de09c7b5a");
        GraphMethods.followUser("12564536-142f-47a3-95b8-02e02269eb7c", "fb4c8107-3e96-42e9-be49-13dec0fb2107");
        GraphMethods.followUser("12564536-142f-47a3-95b8-02e02269eb7c", "4198441c-aa1f-4d97-809b-b1ec950c294d");
        GraphMethods.followUser("12564536-142f-47a3-95b8-02e02269eb7c", "c84a6580-6d99-49db-b034-599c03026e04");
        GraphMethods.followUser("12564536-142f-47a3-95b8-02e02269eb7c", "b452dd80-9801-457c-b574-4cecc5045340");
        GraphMethods.followUser("12564536-142f-47a3-95b8-02e02269eb7c", "f7d59c0a-b9c9-4cc9-ba46-0d79d09eea7b");
        GraphMethods.followUser("12564536-142f-47a3-95b8-02e02269eb7c", "9087b6df-b6f5-4de5-856b-a965c1e3d829");

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

        PostMethods.insertPost(obj1,"9040bff3-9d59-4c5c-a37d-a53f648b15f7");
        PostMethods.insertPost(obj2,"9040bff3-9d59-4c5c-a37d-a53f648b15f7");
        PostMethods.insertPost(obj3,"9040bff3-9d59-4c5c-a37d-a53f648b15f7");

        Assert.assertEquals(3,FeedMethods.getDiscoveryFeed("f7d59c0a-b9c9-4cc9-ba46-0d79d09eea7b",100,0).size());

    }

    @Test
    public void getDiscoverStoriesTest(){
        GraphMethods.followUser("e03168b3-226a-415d-9838-524f104f6348", "4441c5b9-a459-48f0-ab39-afec995746a3");

        GraphMethods.followUser("4441c5b9-a459-48f0-ab39-afec995746a3", "4c22c88e-69b3-46a3-b159-c394856a5355");
        GraphMethods.followUser("4441c5b9-a459-48f0-ab39-afec995746a3", "9235f108-fc46-4308-870d-bb0b1542bdab");
        GraphMethods.followUser("4441c5b9-a459-48f0-ab39-afec995746a3", "da1f5e4a-98bf-4873-8327-aadaf3d73ad4");
        GraphMethods.followUser("4441c5b9-a459-48f0-ab39-afec995746a3", "1c139068-1ffe-4c5c-896c-09bba1e8ce90");

        JSONObject obj = new JSONObject();
        obj.put("user_id", "4c22c88e-69b3-46a3-b159-c394856a5355");
        obj.put("is_featured", false);
        obj.put("media_id", utilities.Main.generateUUID());
        obj.put("reports", new ArrayList<String>());
        obj.put("seen_by_users_ids", new ArrayList<String>());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at", new Timestamp(System.currentTimeMillis()));
        obj.put("expired_at", new Timestamp(System.currentTimeMillis()+86400000));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));

        String id1 = StoriesMethods.insertStory(obj);

        obj.put("user_id","9235f108-fc46-4308-870d-bb0b1542bdab");
        String id2 = StoriesMethods.insertStory(obj);

        obj.put("user_id","da1f5e4a-98bf-4873-8327-aadaf3d73ad4");
        String id3 = StoriesMethods.insertStory(obj);

        obj.put("user_id","1c139068-1ffe-4c5c-896c-09bba1e8ce90");
        String id4 = StoriesMethods.insertStory(obj);

        JSONArray friendsStories = StoriesMethods.getDiscoverStories("e03168b3-226a-415d-9838-524f104f6348");
        Assert.assertEquals(4,friendsStories.length());
        Assert.assertEquals(1, ((JSONArray) ((JSONObject) friendsStories.get(0)).get("stories")).length());

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
        post.put("likes",new ArrayList<String>());
        String postID = PostMethods.insertPost(post,userId1);
        GraphMethods.followUser(userId2,userId1);
        PostMethods.likePost(postID,userId2);
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

        ActivityMethods.insertNotification(notificationJSON);
        ActivityMethods.insertActivity(notificationJSON);

        Assert.assertTrue(ActivityMethods.getNotifications(userId1,0,5).length()==1);
    }
    @Test
    public void trendingPostsTest() throws Exception {
        String userId1 = utilities.Main.generateUUID() ;

        JSONObject post = new JSONObject();

        post.put("user_id",userId1);
        post.put("caption","hello");
        post.put("media",new ArrayList<String>());
        post.put("comments",new ArrayList<String>());
        ArrayList<String> likes = new ArrayList<String>();
        for(int i =0;i<51;i++){
            likes.add(utilities.Main.generateUUID());
        }
        post.put("likes",likes);
        String postID = PostMethods.insertPost(post,userId1);
        Assert.assertEquals(1,PostMethods.getTrendingPosts().length());
        System.out.println(PostMethods.getTrendingPosts());
    }

    @Test
    public void ActivitiesTest() throws Exception {
        String userId1 = utilities.Main.generateUUID() ;
        String userId2 = utilities.Main.generateUUID() ;
        String userId3 = utilities.Main.generateUUID() ;

        GraphMethods.makeUserNode(userId1);
        GraphMethods.makeUserNode(userId2);
        GraphMethods.makeUserNode(userId3);

        JSONObject post = new JSONObject();

        post.put("user_id",userId1);
        post.put("caption","hello");
        post.put("media",new ArrayList<String>());
        post.put("comments",new ArrayList<String>());
        post.put("likes",new ArrayList<String>());
        String postID = PostMethods.insertPost(post,userId1);
        GraphMethods.followUser(userId2,userId1);
        GraphMethods.followUser(userId3,userId2);
        PostMethods.likePost(postID,userId2);
        JSONObject comment = new JSONObject();
        comment.put("content","hello");
        comment.put("user_id",userId2);
        PostMethods.insertCommentOnPost(postID,comment);
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

        ActivityMethods.insertNotification(notificationJSON);
        ActivityMethods.insertActivity(notificationJSON);

        Assert.assertTrue(ActivityMethods.getNotifications(userId1,0,5).length()==1);

        ArrayList<String> followings = GraphMethods.getAllfollowingIDs(userId3);
        Assert.assertEquals(ActivityMethods.getActivities(followings,0,5).length(), 1);

    }

    @Test
    public void trendingHashtagsTest() throws CustomException {
        GraphMethods.makeHashtagNode("MoSalah");
        GraphMethods.makeHashtagNode("FIFA");
        GraphMethods.makeHashtagNode("Gedo");

        GraphMethods.followHashtag("9087b6df-b6f5-4de5-856b-a965c1e3d829", "MoSalah");
        GraphMethods.followHashtag("2af9121b-89a1-4365-83e8-96be1a7f2847", "MoSalah");
        GraphMethods.followHashtag("302d0e85-91be-46c2-ac71-2a4991207d3b", "MoSalah");
        GraphMethods.followHashtag("20981745-ca25-483f-a831-edd6c1ffcade", "MoSalah");
        GraphMethods.followHashtag("768a9e00-3d8e-4274-8f21-de6a76c64456", "MoSalah");
        GraphMethods.followHashtag("aaa106a4-42f7-4eb2-b99f-f6de6863e005", "MoSalah");
        GraphMethods.followHashtag("9d1d757a-6f35-4302-8e88-eb14e952af23", "MoSalah");
        GraphMethods.followHashtag("38ce150a-64c0-4859-9542-334b4757061a", "MoSalah");
        GraphMethods.followHashtag("4a4df3f7-8802-4d0a-a52f-4776ead2e12a", "MoSalah");
        GraphMethods.followHashtag("ab1bcadd-d4de-4fd9-aea5-aa0920eba624", "MoSalah");
        GraphMethods.followHashtag("ef58c348-1195-4d83-a4bc-8bd68214c6f6", "MoSalah");

        ArrayList<String> trending = HashtagMethods.getAllTrendingHashtags(0,10);
        Assert.assertEquals(1, trending.size());



    }

}