package persistence.nosql;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.util.MapBuilder;
import exceptions.CustomException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class HashtagMethods {
    static ArangoDB arangoDB= ArangoInterfaceMethods.arangoDB;
    static String dbName = ArangoInterfaceMethods.dbName;

    public static final String hashtagCollectionName = ArangoInterfaceMethods.hashtagCollectionName;

    //Hashtags CRUD
    public static String insertHashtag(String hashtagName) {

        JSONObject hashtagJSON = new JSONObject();
        hashtagJSON.put("name", hashtagName);
        hashtagJSON.put("followers_number", 0);

        String id = arangoDB.db(dbName).collection(hashtagCollectionName).insertDocument(hashtagJSON.toString()).getKey();
        System.out.println(id);
        System.out.println("Hashtag inserted");
        return id;
    }

    public static JSONObject getHashtag(String hashtagName) throws CustomException, ArangoDBException {

        String dbQuery ="for hashtag in Hashtags FILTER hashtag.name == \""+ hashtagName +"\" return hashtag";
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(dbQuery, bindVars, null,
                BaseDocument.class);
        BaseDocument hashtagDoc = cursor.next();
        JSONObject hashtagJSON  = new JSONObject(hashtagDoc.getProperties());
        return ArangoInterfaceMethods.reformatJSON(hashtagJSON);
    }

    public static void updateHashtag(String hashtagName,JSONObject hashtagJSON) {
        BaseDocument myObject = new BaseDocument();
        myObject.addAttribute("name", hashtagJSON.get("name").toString());
        myObject.addAttribute("followers_number", hashtagJSON.get("followers_number").toString());

        arangoDB.db(dbName).collection(hashtagCollectionName).updateDocument(hashtagName, hashtagJSON.toString());
        System.out.println("Hashtag Updated");

    }

    public static void deleteHashtag(String hashtagName) throws CustomException{
        try {
            arangoDB.db(dbName).collection(hashtagCollectionName).deleteDocument(hashtagName);
            System.out.println("Hashtag Deleted: " + hashtagName);
        } catch (ArangoDBException e) {
            throw new CustomException("hashtag Name does not exist:  " + hashtagName);
        }
    }
    public static ArrayList<String> getAllTrendingHashtags(int offset, int limit){
        ArrayList<String> results = new ArrayList<String>();
        String dbQuery ="for hashtag in Hashtags FILTER hashtag.followers_number >= 10 LIMIT "+offset+" , "+limit+ " return hashtag";
        Map<String, Object> bindVars = new MapBuilder().get();
        System.out.println(dbQuery);
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(dbQuery, bindVars, null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            results.add(aDocument.getKey());
            System.out.println("Hashtag : " + aDocument.getKey());
        });
        return results;
    }


}
