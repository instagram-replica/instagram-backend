package services.stories;

import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import org.json.JSONObject;

import java.util.ArrayList;

public class Controller extends shared.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject newJsonObj = new JSONObject();

        String methodName = jsonObject.getString("method");
//        JSONObject paramsObject = jsonObject.getJSONObject("params");

//        System.out.println(methodName);

        switch(methodName){
            case "createStory":createStory();break;
            case "deleteStory":deleteStory();break;
//            case "getMyStory":getMyStory(userId);break;
            case "getMyStories":getStories();break;
            case "getDiscoverStories":getDiscoverStories();break;
        }

        newJsonObj.put("application", methodName);
        return newJsonObj;
    }


    public static void createStory(){
        BaseDocument myObject = new BaseDocument();
        myObject.setKey("myKey");
        myObject.addAttribute("a", "Foo");
        myObject.addAttribute("b", 42);
        try {
//            arangoDB.db(dbName).collection(collectionName).insertDocument(myObject);
        } catch (ArangoDBException e) {
            System.err.println("Failed to create document. " + e.getMessage());
        }
    }

    public static void deleteStory(){
        try {
//            arangoDB.db(dbName).collection(collectionName).deleteDocument(idUser);
        } catch (ArangoDBException e) {
            System.err.println("Failed to delete document. " + e.getMessage());
        }
    }

    public static void getMyStory(int userId){
        try {
//            BaseDocument myStory = arangoDB.db(dbName).collection(collectionName).getDocument(userId,
//                    BaseDocument.class);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get document: myKey; " + e.getMessage());
        }
    }

    public static void getStories(){
       // ArrayList <int> friendsIds = get followers
       // for(int i=0;i<friendsIds;i++)
        try {
//            BaseDocument myDocument = arangoDB.db(dbName).collection(collectionName).getDocument(friendsIds[i],
//                    BaseDocument.class);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get document: myKey; " + e.getMessage());
        }
    }

    public static void getDiscoverStories(){
        // ArrayList <int> locationBasedPeople = get people that are near the one requesting
        // for(int i=0;i<locationBasedPeople;i++)
        try {
//            BaseDocument myDocument = arangoDB.db(dbName).collection(collectionName).getDocument(locationBasedPeople[i],
//                    BaseDocument.class);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get document: myKey; " + e.getMessage());
        }
    }

}
