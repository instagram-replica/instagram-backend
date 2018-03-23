package persistence.nosql;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import exceptions.CustomException;
import org.json.JSONObject;

public class BookmarkPosts {
    static ArangoDB arangoDB= ArangoInterfaceMethods.arangoDB;
    static String dbName = ArangoInterfaceMethods.dbName;

    public static final String bookmarksCollectionName = ArangoInterfaceMethods.bookmarksCollectionName;

    //BOOKMARKS CRUD
    public static String insertBookmark(JSONObject bookmarkJSON) {

        BaseDocument myObject = new BaseDocument();
        myObject.addAttribute("user_id", bookmarkJSON.get("user_id").toString());
        myObject.addAttribute("posts_ids", bookmarkJSON.get("posts_ids").toString());
        String id = arangoDB.db(dbName).collection(bookmarksCollectionName).insertDocument(bookmarkJSON.toString()).getKey();
        System.out.println("Bookmark inserted");
        return id;
    }

    public static JSONObject getBookmark(String id) throws CustomException, ArangoDBException {

        BaseDocument bookmarkDoc = arangoDB.db(dbName).collection(bookmarksCollectionName).getDocument(id,
                BaseDocument.class);
        if (bookmarkDoc == null) {
            throw new CustomException("Bookmark with ID: " + id + " Not Found");
        }
        JSONObject bookmarkJSON = new JSONObject(bookmarkDoc.getProperties());
        return ArangoInterfaceMethods.reformatJSON(bookmarkJSON);
    }

    public static void updateBookmark(String id, JSONObject bookmarkJSON) {

        BaseDocument myObject = new BaseDocument();
        myObject.addAttribute("user_id", bookmarkJSON.get("user_id").toString());
        myObject.addAttribute("posts_ids", bookmarkJSON.get("posts_ids").toString());
        arangoDB.db(dbName).collection(bookmarksCollectionName).updateDocument(id, bookmarkJSON.toString());
        System.out.println("Bookmark Updated");

    }

    public static void deleteBookmark(String id) throws CustomException{
        try {
            arangoDB.db(dbName).collection(bookmarksCollectionName).deleteDocument(id);
            System.out.println("Bookmark Deleted: " + id);
        } catch (ArangoDBException e) {
            throw new CustomException("Bookmark ID does not exist:  " + id);
        }
    }


}
