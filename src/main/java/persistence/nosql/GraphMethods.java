package persistence.nosql;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoEdgeCollection;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.BaseEdgeDocument;
import com.arangodb.util.MapBuilder;
import exceptions.CustomException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class GraphMethods {
    static ArangoDB arangoDB= ArangoInterfaceMethods.arangoDB;
    static String dbName = ArangoInterfaceMethods.dbName;

    public static final String userCollectionName = "Users";
    public static final String hashtagCollectionName = "Hashtags";

    public static final String graphUserFollowsCollectionName = ArangoInterfaceMethods.graphUserFollowsCollectionName;
    public static final String graphUserInteractsCollectionName = ArangoInterfaceMethods.graphUserInteractsCollectionName;
    public static final String graphUserTaggedCollectionName = ArangoInterfaceMethods.graphUserTaggedCollectionName;
    public static final String graphPostTaggedCollectionName = ArangoInterfaceMethods.graphPostTaggedCollectionName;
    public static final String graphUserBlockedCollectionName = ArangoInterfaceMethods.graphUserBlockedCollectionName;
    public static final String graphUserReportedCollectionName = ArangoInterfaceMethods.graphUserReportedCollectionName;
    public static final String graphUserConnectedToThreadCollectionName = ArangoInterfaceMethods.graphUserConnectedToThreadCollectionName;

    private static final String graphName = "InstagramGraph";

    public static boolean followUser(String followerKey, String followedKey) {

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String followerID = "Users/"+followerKey;
        String followedID = "Users/"+followedKey;
        edge.setKey(followerKey + followedKey);
        edge.setFrom(followerID);
        edge.setTo(followedID);

        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserFollowsCollectionName);
        edgecollection.insertEdge(edge, null);
        return true;

    }

    public static boolean blockUser(String blockerKey, String blockedKey) {

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String blockerID = "Users/"+blockerKey;
        String blockedID = "Users/"+blockedKey;
        edge.setKey(blockerKey + blockedKey);
        edge.setFrom(blockerID);
        edge.setTo(blockedID);

        try {
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserBlockedCollectionName);
            edgecollection.insertEdge(edge, null);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Edge Insertion Failed In Graph: " + e.getMessage());
            return false;
        }
    }

    public static boolean reportUser(String reporterKey, String reportedKey) {

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String reporterID = "Users/"+reporterKey;
        String reportedID = "Users/"+reportedKey;
        edge.setKey(reporterKey + reportedKey);
        edge.setFrom(reporterID);
        edge.setTo(reportedID);

        try {
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserReportedCollectionName);
            edgecollection.insertEdge(edge, null);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Edge Insertion Failed In Graph: " + e.getMessage());
            return false;
        }
    }

    public static boolean joinThread(String userKey, String threadKey) {

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String userID = "Users/"+userKey;
        String threadID = "Threads/"+threadKey;
        edge.setKey(userKey + threadKey);
        edge.setFrom(userID);
        edge.setTo(threadID);

        try {
            ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserConnectedToThreadCollectionName);
            edgecollection.insertEdge(edge, null);
            return true;
        } catch (ArangoDBException e) {
            System.err.println("Edge Insertion Failed In Graph: " + e.getMessage());
            return false;
        }
    }

    public static boolean followHashtag(String userIdKey, String hashtagNameKey) throws CustomException {

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String userID = "Users/"+userIdKey;
        String hashtagName = "Hashtags/"+hashtagNameKey;
        edge.setKey(userIdKey + hashtagNameKey);
        edge.setFrom(userID);
        edge.setTo(hashtagName);
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserInteractsCollectionName);
        edgecollection.insertEdge(edge, null);

        JSONObject hashtagJSON = HashtagMethods.getHashtag(hashtagNameKey);
        int new_followers_number = hashtagJSON.getInt("followers_number") + 1;
        hashtagJSON.put("followers_number", new_followers_number);
        HashtagMethods.updateHashtag(hashtagNameKey,hashtagJSON);

        return true;

    }

    public static boolean tagUserInPost(String userIdKey, String postIDKey){

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String userID = "Users/"+userIdKey;
        String postID = "Posts/"+postIDKey;
        edge.setKey(userIdKey+postIDKey);
        edge.setFrom(userID);
        edge.setTo(postID);

        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserTaggedCollectionName);
        edgecollection.insertEdge(edge,null);
        System.out.println("User Tag Edge Inserted");
        return true;

    }

    public static boolean tagPostInHashtag(String postIDKey, String hashtagNameKey){

        BaseEdgeDocument edge = new BaseEdgeDocument();
        String postID = "Posts/"+postIDKey;
        String hashtagName = "Hashtags/"+hashtagNameKey;
        edge.setKey(postIDKey + hashtagNameKey);
        edge.setFrom(postID);
        edge.setTo(hashtagName);

        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphPostTaggedCollectionName);
        edgecollection.insertEdge(edge,null);
        System.out.println("Post Tag Edge Inserted");
        return true;

    }


    public static boolean unFollowUser(String followerKey, String followedKey){
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserFollowsCollectionName);
        edgecollection.deleteEdge(followerKey + followedKey);
        return true;

    }

    public static boolean unblockUser(String blockerKey, String blockedKey){
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserBlockedCollectionName);
        edgecollection.deleteEdge(blockerKey + blockedKey);
        return true;

    }

    public static boolean unFolllowHashtag(String userIDKey, String hashtagNameKey) {
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserInteractsCollectionName);
        edgecollection.deleteEdge(userIDKey + hashtagNameKey);
        return true;
    }

    public static boolean untagUser(String userIDKey, String postIDKey){
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserTaggedCollectionName);
        edgecollection.deleteEdge(userIDKey + postIDKey);
        return true;
    }

    public static boolean untagPost(String postIDKey, String hashtagNameKey){
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphPostTaggedCollectionName);
        edgecollection.deleteEdge(postIDKey + hashtagNameKey);
        return true;

    }


    public static boolean makeUserNode(String userID){
        BaseDocument userDocument = new BaseDocument();
        userDocument.setKey(userID);
        arangoDB.db(dbName).graph(graphName).vertexCollection(userCollectionName).insertVertex(userDocument, null);
        return true;

    }


    public static boolean makeHashtagNode(String hashtagName){
        BaseDocument hashtagDocument = new BaseDocument();
        hashtagDocument.setKey(hashtagName);
        arangoDB.db(dbName).graph(graphName).vertexCollection(hashtagCollectionName).insertVertex(hashtagDocument, null);

        HashtagMethods.insertHashtag(hashtagName);
        return true;
    }

//    public static boolean makePostNode(String postID){
//        try{
//            BaseDocument postDoc = new BaseDocument();
//            postDoc.setKey(postID);
//            arangoDB.db(dbName).graph(graphName).vertexCollection(postsCollectionName).insertVertex(postDoc, null);
//            return true;
//        }
//        catch(ArangoDBException e){
//            System.err.println("Failed to initialize a node for post In Graph: " + e.getMessage());
//            return false;
//        }
//    }


    public static boolean isFollowing(String userKey, String followingKey) {
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserFollowsCollectionName);
        BaseEdgeDocument edgeDoc = edgecollection.getEdge(userKey + followingKey, BaseEdgeDocument.class);
        if (edgeDoc == null) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean isBlocked(String blockerKey, String blockedKey) {
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserBlockedCollectionName);
        BaseEdgeDocument edgeDoc = edgecollection.getEdge(blockerKey + blockedKey, BaseEdgeDocument.class);
        if (edgeDoc == null) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean isInteracting(String userKey, String hashtagKey) {
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserInteractsCollectionName);
        BaseEdgeDocument edgeDoc = edgecollection.getEdge(userKey + hashtagKey, BaseEdgeDocument.class);
        if (edgeDoc == null) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean isTaggedUser(String userKey, String postIdKey){
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserTaggedCollectionName);
        BaseEdgeDocument edgeDoc = edgecollection.getEdge(userKey+postIdKey,BaseEdgeDocument.class);
        if(edgeDoc == null){
            return false;
        }
        else{
            return true;
        }

    }

    public static boolean isTaggedPost(String postIDKey, String hashtagNameKey){
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphPostTaggedCollectionName);
        BaseEdgeDocument edgeDoc = edgecollection.getEdge(postIDKey+hashtagNameKey,BaseEdgeDocument.class);
        if(edgeDoc == null){
            return false;
        }
        else{
            return true;
        }

    }

    public static boolean isReported(String reporterKey, String reportedKey){
        ArangoEdgeCollection edgecollection = arangoDB.db(dbName).graph(graphName).edgeCollection(graphUserReportedCollectionName);
        BaseEdgeDocument edgeDoc = edgecollection.getEdge(reporterKey+reportedKey,BaseEdgeDocument.class);
        if(edgeDoc == null){
            return false;
        }
        else{
            return true;
        }

    }

    //    public static boolean removeUserNode(String userID){
//        try{
//            arangoDB.db(dbName).graph(graphName).vertexCollection(userCollectionName).deleteVertex(userID);
//            return true;
//        }
//        catch(ArangoDBException e){
//            System.err.println("Failed to delete a node for user In Graph: " + e.getMessage());
//            return false;
//        }
//
//    }

//    public static boolean removeHashtagNode(String hashtagName){
//        try{
//            arangoDB.db(dbName).graph(graphName).vertexCollection(hashtagCollectionName).deleteVertex(hashtagName);
//            return true;
//        }
//        catch(ArangoDBException e){
//            System.err.println("Failed to delete a node for hashtag In Graph: " + e.getMessage());
//            return false;
//        }
//
//    }

    public static ArrayList<String> getAllfollowingIDs(String userKey) {
        String userID = "Users/"+userKey;
        ArrayList<String> IDs = new ArrayList<>();
        String query = "FOR vertex IN OUTBOUND \""  + userID+"\" "+ graphUserFollowsCollectionName + " RETURN vertex " ;
        System.out.println(query);
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            IDs.add(aDocument.getKey());
        });
        return IDs;
    }

    public static ArrayList<String> getAllfollowersIDs(String userKey){
        String userID = "Users/"+userKey;
        ArrayList<String> IDs = new ArrayList<>();
        String query = "FOR vertex IN INBOUND \""  + userID+"\" "+ graphUserFollowsCollectionName + " RETURN vertex " ;
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            IDs.add(aDocument.getKey());
            System.out.println("ID follower: "+ aDocument.getKey());
        });
        return IDs;
    }

    public static ArrayList<String> getAllfollowingPublicIDsSecondDegree(String userKey){
        String userID = "Users/"+userKey;
        ArrayList<String> IDs = new ArrayList<>();
        String query = "FOR vertex IN 2..2 OUTBOUND  \""  + userID+"\" "+ graphUserFollowsCollectionName + " RETURN vertex " ;
        System.out.println(query);
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            IDs.add(aDocument.getKey());
        });
        IDs.remove(userKey);
        //TODO Mohamed Abouzeid uncomment this line when done :D
//            IDs = removePrivateIDs(IDs);
        return IDs;
    }

    public static ArrayList<String> getAllThreadsForUser(String userKey) {
        String userID = "Users/"+userKey;
        ArrayList<String> IDs = new ArrayList<>();
        String query = "FOR vertex IN OUTBOUND \""  + userID+"\" "+ graphUserConnectedToThreadCollectionName + " RETURN vertex " ;
        System.out.println(query);
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            IDs.add(aDocument.getKey());
        });
        return IDs;
    }

    public static ArrayList<String> getAllUsersInThread(String threadKey){
        String threadID = "Threads/"+threadKey;
        ArrayList<String> IDs = new ArrayList<>();
        String query = "FOR vertex IN INBOUND \""  + threadID+"\" "+ graphUserConnectedToThreadCollectionName + " RETURN vertex " ;
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            IDs.add(aDocument.getKey());
            System.out.println("ID follower: "+ aDocument.getKey());
        });
        return IDs;
    }

    public static ArrayList<String> getAllBlockedIDs(String userKey) {
        try {
            String userID = "Users/"+userKey;
            ArrayList<String> IDs = new ArrayList<>();
            String query = "FOR vertex IN OUTBOUND \""  + userID+"\" "+ graphUserBlockedCollectionName + " RETURN vertex " ;
            System.out.println(query);
            Map<String, Object> bindVars = new MapBuilder().get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                IDs.add(aDocument.getKey());
            });
            return IDs;
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
            return null;
        }

    }

    public static ArrayList<String> getAllFollowingHashtags(String userKey){

        String userID = "Users/"+userKey;
        ArrayList<String> HashtagNames = new ArrayList<>();
        String query = "FOR vertex IN OUTBOUND \""  + userID+"\" "+ graphUserInteractsCollectionName + " RETURN vertex " ;
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            HashtagNames.add(aDocument.getKey());
            System.out.println("Hashtag following: " + aDocument.getKey());
        });
        return HashtagNames;

    }


    public static ArrayList<String> getAllHashtagFollowers(String hashtagNameKey){
        String hashtagName = "Hashtags/"+hashtagNameKey;
        ArrayList<String> IDs = new ArrayList<>();
        String query = "FOR vertex IN INBOUND \""  + hashtagName+"\" "+ graphUserInteractsCollectionName + " RETURN vertex " ;
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            IDs.add(aDocument.getKey());
            System.out.println("ID follower: " + aDocument.getKey());
        });
        return IDs;
    }


    public static ArrayList<String> getAllUsersTaggedInAPost(String postKey){

        String postId = "Posts/"+postKey;
        ArrayList<String> IDs = new ArrayList<>();
        String query = "FOR vertex IN INBOUND \""  + postId+"\" "+ graphUserTaggedCollectionName + " RETURN vertex " ;
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            IDs.add(aDocument.getKey());
            System.out.println("ID follower: " + aDocument.getKey());
        });
        return IDs;

    }

    public static ArrayList<String> getAllTaggedPosts(String userKey){

        String userID = "Users/"+userKey;
        ArrayList<String> HashtagNames = new ArrayList<>();
        String query = "FOR vertex IN OUTBOUND \""  + userID+"\" "+ graphUserTaggedCollectionName + " RETURN vertex " ;
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            HashtagNames.add(aDocument.getKey());
            System.out.println("Hashtag : "+ aDocument.getKey());
        });
        return HashtagNames;
    }


    public static ArrayList<String> getAllHashtagsTaggedInPost(String postKey){

        String postID = "Posts/"+postKey;
        ArrayList<String> HashtagNames = new ArrayList<>();
        String query = "FOR vertex IN OUTBOUND \""  + postID+"\" "+ graphPostTaggedCollectionName + " RETURN vertex " ;
        System.out.println(query);
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            HashtagNames.add(aDocument.getKey());
            System.out.println("Hashtag : "+ aDocument.getKey());
        });
        return HashtagNames;
    }

    public static ArrayList<String> getAllPostsTaggedInHashtag(String hashtagNameKey){

        String hashtagName = "Hashtags/"+hashtagNameKey;
        ArrayList<String> posts = new ArrayList<>();
        String query = "FOR vertex IN INBOUND \""  + hashtagName+"\" "+ graphPostTaggedCollectionName + " RETURN vertex " ;
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            posts.add(aDocument.getKey());
            System.out.println("Post : "+ aDocument.getKey());
        });
        return posts;
    }

}
