package persistence.nosql;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.EdgeDefinition;
import com.arangodb.entity.GraphEntity;
import com.arangodb.model.GraphCreateOptions;
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
        arangoDB.db(dbName).drop();
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


            for(int i =0 ;i< user_ids.size();i++){
                BaseDocument userDocument = new BaseDocument();
                userDocument.setKey(user_ids.get(i));
                System.out.println(user_ids.get(i));
                arangoDB.db(dbName).graph(graphName).vertexCollection(userCollectionName).insertVertex(userDocument, null);
            }
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


        String id = ArangoInterfaceMethods.insertThread(obj);
        ArangoInterfaceMethods.updateThread(id, updatedObj);
        JSONObject jsonThread = ArangoInterfaceMethods.getThread(id);
        Assert.assertEquals(Objects.requireNonNull(jsonThread).get("name"), "Mohamed");

        ArangoInterfaceMethods.deleteThread(id);
        Assert.assertEquals(ArangoInterfaceMethods.getThread(id), null);

    }

    @Test
    public void insertAndGetNotification() throws Exception{
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
    public void insertAndGetActivity() throws Exception{
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
    public void insertAndGetStory() throws Exception{

        utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("user_id", utilities.Main.generateUUID());
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
    public void updateAndDeleteStory() throws Exception{
        utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("user_id", utilities.Main.generateUUID());
        obj.put("is_featured", false);
        obj.put("media_id", utilities.Main.generateUUID());
        obj.put("reports", new ArrayList<String>());
        obj.put("seen_by_users_ids", new ArrayList<String>());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at", new Timestamp(System.currentTimeMillis()));
        obj.put("expired_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at", new Timestamp(System.currentTimeMillis()));

        JSONObject updatedObj = new JSONObject();
        updatedObj.put("user_id", utilities.Main.generateUUID());
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
    public void insertAndGetBookmark() throws Exception{

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
        followUser("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829", "Users/2af9121b-89a1-4365-83e8-96be1a7f2847");
        followUser("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829", "Users/302d0e85-91be-46c2-ac71-2a4991207d3b");

        followUser("Users/768a9e00-3d8e-4274-8f21-de6a76c64456", "Users/9087b6df-b6f5-4de5-856b-a965c1e3d829");
        followUser("Users/20981745-ca25-483f-a831-edd6c1ffcade", "Users/9087b6df-b6f5-4de5-856b-a965c1e3d829");
        followUser("Users/2af9121b-89a1-4365-83e8-96be1a7f2847", "Users/9087b6df-b6f5-4de5-856b-a965c1e3d829");
        followUser("Users/302d0e85-91be-46c2-ac71-2a4991207d3b", "Users/9087b6df-b6f5-4de5-856b-a965c1e3d829");

        ArrayList<String> following = getAllfollowingIDs("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(following.size(), 2);

        ArrayList<String> followers = getAllfollowersIDs("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(followers.size(), 4);

        unFollowUser("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829", "Users/2af9121b-89a1-4365-83e8-96be1a7f2847");

        ArrayList<String> followingAfterUnfollow = getAllfollowingIDs("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(followingAfterUnfollow.size(), 1);

        String newUserUUID = UUID.randomUUID().toString();
        makeUserNode(newUserUUID);

        followUser("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829", "Users/" + newUserUUID);
        ArrayList<String> newFollowing = getAllfollowingIDs("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(newFollowing.size(), 2);
        ;

//        removeUserNode("f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
//        ArrayList<String> emptyFollowing = getAllfollowingIDs("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
//        Assert.assertEquals(emptyFollowing.size(),0);

        Assert.assertTrue(isFollowing("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829", "Users/302d0e85-91be-46c2-ac71-2a4991207d3b"));
        Assert.assertFalse(isFollowing("Users/768a9e00-3d8e-4274-8f21-de6a76c64456", "Users/20981745-ca25-483f-a831-edd6c1ffcade"));
    }

    @Test
    public void interactTest() {

        makeHashtagNode("manU");
        makeHashtagNode("pancakes");
        makeHashtagNode("3eesh_namlla_takol_sokar");

        followHashtag("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829", "Hashtags/manU");
        followHashtag("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829", "Hashtags/pancakes");
        followHashtag("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829", "Hashtags/3eesh_namlla_takol_sokar");

        followHashtag("Users/768a9e00-3d8e-4274-8f21-de6a76c64456", "Hashtags/manU");

        ArrayList<String> myHashtags = getAllFollowingHashtags("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(myHashtags.size(), 3);

        ArrayList<String> hashtagFollowers = getAllHashtagFollowers("Hashtags/manU");
        Assert.assertEquals(hashtagFollowers.size(), 2);

        unFolllowHashtag("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829", "Hashtags/manU");
        ArrayList<String> myHashtagsUpdated = getAllFollowingHashtags("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(myHashtagsUpdated.size(), 2);


        Assert.assertTrue(isInteracting("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829", "Hashtags/pancakes"));
        Assert.assertFalse(isInteracting("Users/768a9e00-3d8e-4274-8f21-de6a76c64456", "Hashtags/pancakes"));
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

        tagUserInPost("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829", "Posts/"+id1);
        tagUserInPost("Users/768a9e00-3d8e-4274-8f21-de6a76c64456", "Posts/"+id1);
        tagUserInPost("Users/2af9121b-89a1-4365-83e8-96be1a7f2847","Posts/"+id1);

        tagUserInPost("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829", "Posts/"+id2);

        ArrayList<String> posts = getAllTaggedPosts("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829");
        Assert.assertEquals(posts.size(),2);

        ArrayList<String> ids = getAllUsersTaggedInAPost("Posts/"+id1);
        Assert.assertEquals(ids.size(),3);

        untagUser("Users/768a9e00-3d8e-4274-8f21-de6a76c64456","Posts/"+id1);
        ArrayList<String> idsAfterUntagging = getAllUsersTaggedInAPost("Posts/"+id1);
        Assert.assertEquals(idsAfterUntagging.size(),2);

        Assert.assertTrue(isTaggedUser("Users/9087b6df-b6f5-4de5-856b-a965c1e3d829","Posts/"+id1));
        Assert.assertFalse(isTaggedUser("Users/2af9121b-89a1-4365-83e8-96be1a7f2847","Posts/"+id2));

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

        tagPostInHashtag("Posts/"+id1, "Hashtags/Chelsea");
        tagPostInHashtag("Posts/"+id1, "Hashtags/Liverpool");
        tagPostInHashtag("Posts/"+id1, "Hashtags/from_Russia_with_love");


        tagPostInHashtag("Posts/"+id2, "Hashtags/Chelsea");

        ArrayList<String> hashtags = getAllHashtagsTaggedInPost("Posts/"+id1);
        Assert.assertEquals(hashtags.size(),3);

        ArrayList<String> posts = getAllPostsTaggedInHashtag("Hashtags/Chelsea");
        Assert.assertEquals(posts.size(),2);

        untagPost("Posts/"+id1,"Hashtags/from_Russia_with_love");
        ArrayList<String> hashtagsAfterUntagging = getAllHashtagsTaggedInPost("Posts/"+id1);
        Assert.assertEquals(hashtagsAfterUntagging.size(),2);

        Assert.assertTrue(isTaggedPost("Posts/"+id1, "Hashtags/Liverpool"));
        Assert.assertFalse(isTaggedPost("Posts/"+id1, "Hashtags/from_Russia_with_love"));

    }

}