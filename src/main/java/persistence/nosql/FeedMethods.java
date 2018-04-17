package persistence.nosql;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.arangodb.util.MapBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class FeedMethods {
        static ArangoDB arangoDB= ArangoInterfaceMethods.arangoDB;
        static String dbName = ArangoInterfaceMethods.dbName;


    public static ArrayList<JSONObject> getFeed(String userID, int limit, int offset){
        ArrayList<String> friends = GraphMethods.getAllfollowingIDs(""+ userID);
        JSONArray jsonFollowersArray = new JSONArray(friends);
        return getFeedForFriends(jsonFollowersArray,limit,offset);
    }

    public static ArrayList<JSONObject> getDiscoveryFeed(String userID,int limit, int offset){
        ArrayList<String> friends = GraphMethods.getAllfollowingPublicIDsSecondDegree(""+ userID);
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
}
