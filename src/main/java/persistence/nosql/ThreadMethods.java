package persistence.nosql;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import exceptions.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;

public class ThreadMethods {
    static ArangoDB arangoDB= ArangoInterfaceMethods.arangoDB;
    static String dbName = ArangoInterfaceMethods.dbName;

    public static final String threadsCollectionName = "Threads";

    //Thread CRUD
    public static String insertThread(JSONObject threadJSON) {

        BaseDocument myObject = new BaseDocument();
        myObject.addAttribute("creator_id", threadJSON.get("creator_id").toString());
        myObject.addAttribute("name", threadJSON.get("name").toString());
        myObject.addAttribute("created_at", threadJSON.get("created_at").toString());
        myObject.addAttribute("deleted_at", threadJSON.get("deleted_at").toString());
        myObject.addAttribute("blocked_at", threadJSON.get("blocked_at").toString());
        myObject.addAttribute("messages", threadJSON.get("messages").toString());
        String id = arangoDB.db(dbName).collection(threadsCollectionName).insertDocument(threadJSON.toString()).getKey();
        System.out.println("Thread inserted");
        return id;
    }

    public static JSONObject getThread(String id) throws CustomException {
        BaseDocument threadDoc = arangoDB.db(dbName).collection(threadsCollectionName).getDocument(id,
                BaseDocument.class);
        if (threadDoc == null) {
            throw new CustomException("Thread with ID: " + id + " Not Found");
        }
        JSONObject threadJSON = new JSONObject(threadDoc.getProperties());
        return ArangoInterfaceMethods.reformatJSON(threadJSON);

    }

    public static void updateThread(String id, JSONObject threadJSON) {
        BaseDocument myObject = new BaseDocument();
        myObject.addAttribute("creator_id", threadJSON.get("creator_id").toString());
        myObject.addAttribute("name", threadJSON.get("name").toString());
        myObject.addAttribute("created_at", threadJSON.get("created_at").toString());
        myObject.addAttribute("deleted_at", threadJSON.get("deleted_at").toString());
        myObject.addAttribute("blocked_at", threadJSON.get("blocked_at").toString());
        myObject.addAttribute("messages", threadJSON.get("messages").toString());
        arangoDB.db(dbName).collection(threadsCollectionName).updateDocument(id, threadJSON.toString());
        System.out.println("Thread Updated");
    }

    public static void deleteThread(String id) throws CustomException{
        try {
            arangoDB.db(dbName).collection(threadsCollectionName).deleteDocument(id);
            System.out.println("Thread Deleted: " + id);
        } catch (ArangoDBException e) {
            throw new CustomException("Thread ID does not exist:  " + id );
        }
    }

    public static void insertMessageOnThread(String threadID, JSONObject message) throws CustomException {
        JSONObject thread = getThread(threadID);
        ((JSONArray) thread.get("messages")).put(message);
        updateThread(threadID, thread);
    }


}
