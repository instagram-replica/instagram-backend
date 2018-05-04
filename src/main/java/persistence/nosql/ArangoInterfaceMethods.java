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
import persistence.sql.users.Database;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import java.io.IOException;
import java.util.*;

import static persistence.sql.Database.openConnection;
import static persistence.sql.users.Database.getAllUsersIds;
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

    public static ArangoDB arangoDB= new ArangoDB.Builder().host(properties.getProperty("host"), Integer.parseInt(properties.getProperty("port"))).build();
    public static String dbName = "InstagramAQL";

    public static final String threadsCollectionName = "Threads";
    public static final String notificationsCollectionName = "Notifications";
    public static final String activitiesCollectionName = "Activities";
    public static final String storiesCollectionName = "Stories";
    public static final String postsCollectionName = "Posts";
    public static final String bookmarksCollectionName = "Bookmarks";
    public static final String userCollectionName = "Users";
    public static final String hashtagCollectionName = "Hashtags";

    public static final String graphUserFollowsCollectionName = "UserFollows";
    public static final String graphUserInteractsCollectionName = "UserInteracts";
    public static final String graphUserTaggedCollectionName = "UserTagged";
    public static final String graphPostTaggedCollectionName = "PostTagged";
    public static final String graphUserBlockedCollectionName = "UserBlocked";
    public static final String graphUserReportedCollectionName = "UserReported";
    public static final String graphUserConnectedToThreadCollectionName = "UserConnectedToThread";


    private static final String graphName = "InstagramGraph";

    public ArangoInterfaceMethods() throws IOException {
    }


    public static void main(String[] args) throws Exception {
        //  arangoDB.db(dbName).drop();
        initializeDB();
        initializeGraphCollections();
        arangoDB.shutdown();
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

                CollectionEntity hashtagsCollection = arangoDB.db(dbName).createCollection(hashtagCollectionName);
                System.out.println("Collection created: " + hashtagsCollection.getName());
            }


        } catch (ArangoDBException e) {
            System.err.println("Failed to create database and collections: " + dbName + "; " + e.getMessage());
        }


    }


    public static void initializeGraphCollections() throws IOException, SQLException {
        for (GraphEntity graphEntity : arangoDB.db(dbName).getGraphs()) {
            if (graphEntity.getName().equals(graphName)) {

                return;
            }
        }
        
        List<String> user_ids = Database.getAllUsersIds();
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

    public static JSONObject reformatJSON(JSONObject json) {
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
