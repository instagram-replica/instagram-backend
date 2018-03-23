package persistence.nosql;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.util.MapBuilder;
import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

public class PostMethods {
    static ArangoDB arangoDB= ArangoInterfaceMethods.arangoDB;
    static String dbName = ArangoInterfaceMethods.dbName;


    public static final String postsCollectionName = "Posts";
    //POSTS CRUD

    public static String insertPost(JSONObject postJSON, String userId) throws JSONException {


        JSONObject myObject = new JSONObject();
        myObject.put("user_id", userId);
        myObject.put("caption", postJSON.get("caption").toString());
        myObject.put("media", postJSON.get("media"));
        //TODO: @MAGDY location gets inserted in a wrong way (with key "map")
//            myObject.addAttribute("location", postJSON.getJSONObject("location"));
        myObject.put("comments", new ArrayList<>());
        myObject.put("likes", new ArrayList<>());
        myObject.put("created_at", new Timestamp(System.currentTimeMillis()));
        myObject.put("updated_at", JSONObject.NULL);
        myObject.put("blocked_at", JSONObject.NULL);
        myObject.put("deleted_at", JSONObject.NULL);

        String id = arangoDB.db(dbName).collection(postsCollectionName).insertDocument(myObject.toString()).getKey();
        System.out.println("Post inserted");
        return id;
    }

    public static JSONObject getPost(String id) throws CustomException, ArangoDBException {

        BaseDocument postDoc = arangoDB.db(dbName).collection(postsCollectionName).getDocument(id,
                BaseDocument.class);
        if (postDoc == null) {
            throw new CustomException("Post with ID: " + id + " Not Found");
        }
        JSONObject postJSON = new JSONObject(postDoc.getProperties());
        postJSON.put("id", postDoc.getKey());
        return ArangoInterfaceMethods.reformatJSON(postJSON);

    }

    public static JSONArray getPosts(String userId) throws ArangoDBException{
        String query = "FOR t IN " + postsCollectionName + " FILTER t.user_id == @id RETURN t";
        Map<String, Object> bindVars = new MapBuilder().put("id", userId).get();
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                BaseDocument.class);
        JSONArray result = new JSONArray();
        cursor.forEachRemaining(aDocument -> {
            JSONObject postJSON = new JSONObject(aDocument.getProperties());
            postJSON.put("id", aDocument.getKey());
            result.put(postJSON);
        });
        return result;
    }

    public static void likePost(String postID, String userID) throws Exception {
        JSONObject post = getPost(postID);
        JSONArray likes = (JSONArray) post.get("likes");
        likes.put(userID);
        updatePost(postID, post);
    }

    public static void updatePost(String id, JSONObject postJSON) {
        BaseDocument myObject = new BaseDocument();
        myObject.addAttribute("user_id", postJSON.get("user_id").toString());
        myObject.addAttribute("caption", postJSON.get("caption").toString());
        myObject.addAttribute("media", postJSON.get("media").toString());
        myObject.addAttribute("likes", postJSON.get("likes").toString());
        // @Magdy
//            myObject.addAttribute("location", postJSON.get("location").toString());
        myObject.addAttribute("comments", postJSON.get("comments").toString());
        myObject.addAttribute("created_at", postJSON.get("created_at").toString());
        myObject.addAttribute("updated_at", new Timestamp(System.currentTimeMillis()));
        if (postJSON.has("blocked_at"))
            myObject.addAttribute("blocked_at", postJSON.get("blocked_at").toString());
        if (postJSON.has("deleted_at"))
            myObject.addAttribute("deleted_at", postJSON.get("deleted_at").toString());
        arangoDB.db(dbName).collection(postsCollectionName).updateDocument(id, postJSON.toString());
        System.out.println("Post Updated");
    }

    public static void deletePost(String id) throws CustomException{
        try {
            arangoDB.db(dbName).collection(postsCollectionName).deleteDocument(id);
            System.out.println("Post Deleted: " + id);
        } catch (ArangoDBException e) {
            throw new CustomException("Post ID does not exist:  " + id);
        }
    }

    public static void insertCommentOnPost(String postID, JSONObject comment) throws Exception {
        JSONObject post = getPost(postID);
        ((JSONArray) post.get("comments")).put(comment);
        updatePost(postID, post);
    }

    public static JSONArray getCommentsOnPost(String postID) throws CustomException{

        BaseDocument postDoc = arangoDB.db(dbName).collection(postsCollectionName).getDocument(postID,
                BaseDocument.class);
        if(postDoc == null){
            throw new CustomException("Post with ID: " + postID +" Not Found");
        }
        JSONObject postJSON  = new JSONObject(postDoc.getProperties());
        return (JSONArray) ArangoInterfaceMethods.reformatJSON(postJSON).get("comments");

    }


    //COMMENTS CRUD
    public static void insertCommentReply(String commentID, JSONObject commentReply) {

        String dbQuery = "FOR post in Posts LET willUpdateDocument = ( FOR comment IN post.comments FILTER comment.id == '" + commentID + "' LIMIT 1 RETURN 1) FILTER LENGTH(willUpdateDocument) > 0 LET alteredList = ( FOR comment IN post.comments LET newItem = (comment.id == '" + commentID + "' ? merge(comment, { 'comments' : append(comment.comments," + commentReply.toString() + ")}) : comment) RETURN newItem) UPDATE post WITH { comments:  alteredList } IN Posts";
        arangoDB.db(dbName).query(dbQuery, null, null, null);
    }


}
