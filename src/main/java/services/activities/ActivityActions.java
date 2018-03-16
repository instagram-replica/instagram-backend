package services.activities;

import org.json.JSONObject;

import persistence.nosql.ArangoInterfaceMethods;

public class ActivityActions {

    public static void handlePostLikeActivity(JSONObject requestJSON, String userId) {
        JSONObject params = requestJSON.getJSONObject("params");
        String receiverId = params.getString("receiverId");
        String postID = params.getString("postID");

        JSONObject activityJSON = new JSONObject();
        JSONObject innerJSON = new JSONObject();
        innerJSON.put("type", "liking");
        innerJSON.put("post_id",postID);
        activityJSON.put("activity_type",innerJSON);
        activityJSON.put("sender_id", userId);
        activityJSON.put("receiver_id", receiverId);
        activityJSON.put("created_at",new java.util.Date());
        //activityJSON.put("blocked_at",null);
        activityJSON.put("id",utilities.Main.generateUUID());
        ArangoInterfaceMethods.insertActivity(activityJSON);

    }

//	public static void handleCommentLikeActivity(JSONObject requestJSON, String userId) {
//		JSONObject params = requestJSON.getJSONObject("params");
//		String receiverId = params.getString("receiverId");
//		String commentID = params.getString("commentID");
//
//		// create a new json object required to save the follow activity in the database
//		// by calling ArangoInterfaceMethods.insertActivity
//
//		// [requestJSON] ----> this controller -----> [ArangoInterface]
//
//		JSONObject activityJSON = new JSONObject();
//		JSONObject innerJSON = new JSONObject();
//		innerJSON.put("type", "liking_comment");
//		innerJSON.put("comment_id", commentID);
//		activityJSON.put("activity_type", innerJSON);
//		activityJSON.put("sender_id", userId);
//		activityJSON.put("receiver_id", receiverId);
//		activityJSON.put("created_at",new java.util.Date());
//		//activityJSON.put("blocked_at",null);
//		activityJSON.put("id",utilities.Main.generateUUID());
//		ArangoInterfaceMethods.insertActivity(activityJSON);
//	}

    public static void handleFollowActivity(JSONObject requestJSON, String userId) {
        JSONObject params = requestJSON.getJSONObject("params");
        String receiverId = params.getString("userId");

        JSONObject activityJSON = new JSONObject();
        JSONObject innerJSON = new JSONObject();
        innerJSON.put("type", "following");
        activityJSON.put("activity_type",innerJSON);
        activityJSON.put("sender_id", userId);
        activityJSON.put("receiver_id", receiverId);
        activityJSON.put("created_at",new java.util.Date());
        //activityJSON.put("blocked_at",null);
        activityJSON.put("id",utilities.Main.generateUUID());
        ArangoInterfaceMethods.insertActivity(activityJSON);
    }

    public static void handleGettingActivities(String userId){
        JSONObject activities= ArangoInterfaceMethods.getActivity(userId);
        //missing returned object
    }
}