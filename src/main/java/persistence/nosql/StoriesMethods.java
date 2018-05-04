package persistence.nosql;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class StoriesMethods {
    static ArangoDB arangoDB= ArangoInterfaceMethods.arangoDB;
    static String dbName = ArangoInterfaceMethods.dbName;

    static final String storiesCollectionName = ArangoInterfaceMethods.storiesCollectionName;
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

    public static JSONObject getStory(String id) throws CustomException {

        BaseDocument storyDoc = arangoDB.db(dbName).collection(storiesCollectionName).getDocument(id,
                BaseDocument.class);
        if (storyDoc == null) {
            throw new CustomException("Story with ID: " + id + " Not Found");
        }
        JSONObject storyJSON = new JSONObject(storyDoc.getProperties());
        return ArangoInterfaceMethods.reformatJSON(storyJSON);
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
            result.put(ArangoInterfaceMethods.reformatJSON(postJSON));
        });
        return result;

    }

    public static JSONArray getFriendsStories(String userID){
        JSONArray resultStories = new JSONArray();
        ArrayList<String> friends = GraphMethods.getAllfollowingIDs(""+ userID);
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
        ArrayList<String> publicFriendOfFriends = GraphMethods.getAllfollowingPublicIDsSecondDegree(""+ userID);

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

}
