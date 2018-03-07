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

public class ArangoInterfaceTest {

    private static ArangoDB arangoDB;
    static String dbName =  "InstagramTestAQL";

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
        arangoDB = new ArangoDB.Builder().build();
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
                arangoDB.db(dbName).graph(graphName).vertexCollection(userCollectionName).insertVertex(userDocument, null);
            }
        }
        catch (ArangoDBException e ){
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
        obj.put("creator_id",utilities.Main.generateUUID());
        obj.put("users_ids",new ArrayList<String>());
        obj.put("name","Ahmed");
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at",new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj.put("messages",new ArrayList<String>());


        String id = ArangoInterfaceMethods.insertThread(obj);
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
        JSONObject obj = new JSONObject();
        obj.put("creator_id",utilities.Main.generateUUID());
        obj.put("users_ids",new ArrayList<String>());
        obj.put("name","Abd El Rahman");
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at",new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        obj.put("messages",new ArrayList<String>());

        JSONObject updatedObj = new JSONObject();
        updatedObj.put("creator_id",utilities.Main.generateUUID());
        updatedObj.put("users_ids",new ArrayList<String>());
        updatedObj.put("name","Mohamed");
        updatedObj.put("created_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("deleted_at",new Timestamp(System.currentTimeMillis()));
        updatedObj.put("blocked_at",new Timestamp(System.currentTimeMillis()));
        updatedObj.put("messages",new ArrayList<String>());


        String id = ArangoInterfaceMethods.insertThread(obj);
        ArangoInterfaceMethods.updateThread(id,updatedObj);
        JSONObject jsonThread = ArangoInterfaceMethods.getThread(id);
        Assert.assertEquals(Objects.requireNonNull(jsonThread).get("name"),"Mohamed");

        ArangoInterfaceMethods.deleteThread(id);
        Assert.assertEquals(ArangoInterfaceMethods.getThread(id),null);

    }

    @Test
    public void insertAndGetNotification() {
        JSONObject obj = new JSONObject();
        obj.put("activity_type","{ type: follow, user_id: 2343-2342");
        obj.put("receiver_id", utilities.Main.generateUUID());
        obj.put("sender_id", utilities.Main.generateUUID());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));


        String id = ArangoInterfaceMethods.insertNotification(obj);
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
        JSONObject obj = new JSONObject();
        obj.put("activity_type","{ type: follow, user_id: 2343-2342 }");
        obj.put("receiver_id", utilities.Main.generateUUID());
        obj.put("sender_id", utilities.Main.generateUUID());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));

        JSONObject updatedObj = new JSONObject();
        updatedObj.put("activity_type","{ type: tag, user_id: 2343-2342 }");
        updatedObj.put("receiver_id", utilities.Main.generateUUID());
        updatedObj.put("sender_id", utilities.Main.generateUUID());
        updatedObj.put("created_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("blocked_at",new Timestamp(System.currentTimeMillis()));


        String id = ArangoInterfaceMethods.insertNotification(obj);
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
        JSONObject obj = new JSONObject();
        obj.put("activity_type","{ type: follow, user_id: 2343-2342");
        obj.put("receiver_id", utilities.Main.generateUUID());
        obj.put("sender_id", utilities.Main.generateUUID());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));


        String id = ArangoInterfaceMethods.insertActivity(obj);
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
        JSONObject obj = new JSONObject();
        obj.put("activity_type","{ type: follow, user_id: 2343-2342 }");
        obj.put("receiver_id", utilities.Main.generateUUID());
        obj.put("sender_id", utilities.Main.generateUUID());
        obj.put("created_at", new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));

        JSONObject updatedObj = new JSONObject();
        updatedObj.put("activity_type","{ type: tag, user_id: 2343-2342 }");
        updatedObj.put("receiver_id", utilities.Main.generateUUID());
        updatedObj.put("sender_id", utilities.Main.generateUUID());
        updatedObj.put("created_at", new Timestamp(System.currentTimeMillis()));
        updatedObj.put("blocked_at",new Timestamp(System.currentTimeMillis()));


        String id = ArangoInterfaceMethods.insertActivity(obj);
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

        utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
        obj.put("user_id",utilities.Main.generateUUID());
        obj.put("is_featured",false);
        obj.put("media_id", utilities.Main.generateUUID());
        obj.put("reports", new ArrayList<String>());
        obj.put("seen_by_users_ids",new ArrayList<String>());
        obj.put("created_at",new Timestamp(System.currentTimeMillis()));
        obj.put("deleted_at",new Timestamp(System.currentTimeMillis()));
        obj.put("expired_at",new Timestamp(System.currentTimeMillis()));
        obj.put("blocked_at",new Timestamp(System.currentTimeMillis()));

        String id = ArangoInterfaceMethods.insertStory(obj);
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
         utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
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
        updatedObj.put("user_id",utilities.Main.generateUUID());
        updatedObj.put("is_featured",true);
        updatedObj.put("media_id", utilities.Main.generateUUID());
        updatedObj.put("reports", new ArrayList<String>());
        updatedObj.put("seen_by_users_ids",new ArrayList<String>());
        updatedObj.put("created_at",new Timestamp(System.currentTimeMillis()));
        updatedObj.put("deleted_at",new Timestamp(System.currentTimeMillis()));
        updatedObj.put("expired_at",new Timestamp(System.currentTimeMillis()));
        updatedObj.put("blocked_at",new Timestamp(System.currentTimeMillis()));


        String id =ArangoInterfaceMethods.insertStory(obj);
        ArangoInterfaceMethods.updateStory(id,updatedObj);
        JSONObject jsonNotification = ArangoInterfaceMethods.getStory(id);

        Assert.assertEquals(
                Objects.requireNonNull(jsonNotification).get("is_featured"),
                true);

        ArangoInterfaceMethods.deleteStory(id);
        Assert.assertEquals(ArangoInterfaceMethods.getStory(id),null);

    }
    @Test
    public void insertAndGetPost() {

        JSONObject obj = new JSONObject();
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

        String id = ArangoInterfaceMethods.insertPost(obj);
        System.out.println("_____________________ "+id);
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

        utilities.Main.generateUUID();
        JSONObject obj = new JSONObject();
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


        String id = ArangoInterfaceMethods.insertPost(obj);
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

        JSONObject obj = new JSONObject();
        obj.put("posts_ids",new ArrayList<String>());
        obj.put("user_id",utilities.Main.generateUUID());

        String id = ArangoInterfaceMethods.insertBookmark(obj);
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

        JSONObject obj = new JSONObject();
        obj.put("user_id", utilities.Main.generateUUID());
        obj.put("posts_ids",new ArrayList<String>());

        JSONObject updatedObj = new JSONObject();
        ArrayList<String> post_ids = new ArrayList<>();
        post_ids.add(utilities.Main.generateUUID());
        post_ids.add(utilities.Main.generateUUID());


        updatedObj.put("user_id", utilities.Main.generateUUID());
        updatedObj.put("posts_ids", post_ids);



        String id = ArangoInterfaceMethods.insertBookmark(obj);
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

        JSONObject obj = new JSONObject();
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

        String id =ArangoInterfaceMethods.insertPost(obj);
        JSONObject comment = new JSONObject();
        comment.put("content","Hello");

        ArangoInterfaceMethods.insertCommentOnPost(id,comment);

        JSONObject fetchedPost = ArangoInterfaceMethods.getPost(id);

        Assert.assertTrue(fetchedPost.get("comments").toString().contains(comment.toString()));
    }

    @Test
    public void followTest(){
        followUser("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c","Users/3d9c043c-7608-8afa-8e09-1f62bb84427b");
        followUser("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c","Users/040ea46c-fb03-5ea8-dcae-7b42a06909e8");

        followUser("Users/a10d47bf-7c9c-8193-381f-79db326cc8dd","Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
        followUser("Users/c38233c6-cddd-4e04-5b8b-7d667854b61a","Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
        followUser("Users/f1099115-5201-7e6a-34c3-b61591a37b84","Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
        followUser("Users/040ea46c-fb03-5ea8-dcae-7b42a06909e8","Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c");

        ArrayList<String> following = getAllfollowingIDs("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
        following.size();
        Assert.assertEquals(following.size(),2);

        ArrayList<String> followers = getAllfollowersIDs("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
        Assert.assertEquals(followers.size(),4);

        unFollowUser("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c", "Users/3d9c043c-7608-8afa-8e09-1f62bb84427b");

        ArrayList<String> followingAfterUnfollow = getAllfollowingIDs("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
        Assert.assertEquals(followingAfterUnfollow.size(),1);

        String newUserUUID = UUID.randomUUID().toString();
        makeUserNode(newUserUUID);

        followUser("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c","Users/"+newUserUUID);
        ArrayList<String> newFollowing = getAllfollowingIDs("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
        Assert.assertEquals(newFollowing.size(),2);;

//        removeUserNode("f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
//        ArrayList<String> emptyFollowing = getAllfollowingIDs("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
//        Assert.assertEquals(emptyFollowing.size(),0);

        Assert.assertTrue(isFollowing("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c","Users/040ea46c-fb03-5ea8-dcae-7b42a06909e8"));
        Assert.assertFalse(isFollowing("Users/a10d47bf-7c9c-8193-381f-79db326cc8dd", "Users/c38233c6-cddd-4e04-5b8b-7d667854b61a"));
    }

    @Test
    public void interactTest(){

        makeHashtagNode("manU");
        makeHashtagNode("pancakes");
        makeHashtagNode("3eesh_namlla_takol_sokar");

        followHashtag("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c", "Hashtags/manU");
        followHashtag("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c", "Hashtags/pancakes");
        followHashtag("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c", "Hashtags/3eesh_namlla_takol_sokar");

        followHashtag("Users/a10d47bf-7c9c-8193-381f-79db326cc8dd", "Hashtags/manU");

        ArrayList<String> myHashtags = getAllFollowingHashtags("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
        Assert.assertEquals(myHashtags.size(),3);

        ArrayList<String> hashtagFollowers = getAllHashtagFollowers("Hashtags/manU");
        Assert.assertEquals(hashtagFollowers.size(),2);

        unFolllowHashtag("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c","Hashtags/manU");
        ArrayList<String> myHashtagsUpdated = getAllFollowingHashtags("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
        Assert.assertEquals(myHashtagsUpdated.size(),2);


        Assert.assertTrue(isInteracting("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c","Hashtags/pancakes"));
        Assert.assertFalse(isInteracting("Users/a10d47bf-7c9c-8193-381f-79db326cc8dd", "Hashtags/pancakes"));
    }

    @Test
    public void tagUserTest(){

        JSONObject obj1= new JSONObject();
        obj1.put("user_id",utilities.Main.generateUUID());
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
        obj2.put("user_id",utilities.Main.generateUUID());
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

        String id1 = ArangoInterfaceMethods.insertPost(obj1);
        String id2 = ArangoInterfaceMethods.insertPost(obj2);

        tagUserInPost("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c", "Posts/"+id1);
        tagUserInPost("Users/a10d47bf-7c9c-8193-381f-79db326cc8dd", "Posts/"+id1);
        tagUserInPost("Users/040ea46c-fb03-5ea8-dcae-7b42a06909e8","Posts/"+id1);

        tagUserInPost("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c", "Posts/"+id2);

        ArrayList<String> posts = getAllTaggedPosts("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c");
        Assert.assertEquals(posts.size(),2);

        ArrayList<String> ids = getAllUsersTaggedInAPost("Posts/"+id1);
        Assert.assertEquals(ids.size(),3);

        untagUser("Users/a10d47bf-7c9c-8193-381f-79db326cc8dd","Posts/"+id1);
        ArrayList<String> idsAfterUntagging = getAllUsersTaggedInAPost("Posts/"+id1);
        Assert.assertEquals(idsAfterUntagging.size(),2);

        Assert.assertTrue(isTaggedUser("Users/f5e1008c-6157-e05d-c01c-5f5c7e055b2c","Posts/"+id1));
        Assert.assertFalse(isTaggedUser("Users/040ea46c-fb03-5ea8-dcae-7b42a06909e8","Posts/"+id2));

    }

    @Test
    public void tagPostTest(){

        JSONObject obj1= new JSONObject();
        obj1.put("user_id",utilities.Main.generateUUID());
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
        obj2.put("user_id",utilities.Main.generateUUID());
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

        String id1 = ArangoInterfaceMethods.insertPost(obj1);
        String id2 = ArangoInterfaceMethods.insertPost(obj2);

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