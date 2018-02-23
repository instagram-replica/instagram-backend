package services.activities;

import org.json.JSONObject;

import persistence.nosql.ArangoInterfaceMethods;

public class ActivityActions {
	
	public static void handlePostLikeActivity(JSONObject requestJSON, String userId) {	
		JSONObject params = requestJSON.getJSONObject("params");
		String recieverId = params.getString("recieverId");
		
		// create a new json object required to save the follow activity in the database
		// by calling ArangoInterfaceMethods.insertActivity 
		
		// [requestJSON] ----> this controller -----> [ArangoInterface] 
		
		JSONObject activityJSON = new JSONObject();
		activityJSON.put("activity_type", "liking_post");
		activityJSON.put("sender_id", userId);
		activityJSON.put("reciever_id", recieverId);
		
		ArangoInterfaceMethods.insertActivity(activityJSON);
		
	}
	
	public static void handleCommentLikeActivity(JSONObject requestJSON, String userId) {
		JSONObject params = requestJSON.getJSONObject("params");
		String recieverId = params.getString("recieverId");
		
		// create a new json object required to save the follow activity in the database
		// by calling ArangoInterfaceMethods.insertActivity 
		
		// [requestJSON] ----> this controller -----> [ArangoInterface] 
		
		JSONObject activityJSON = new JSONObject();
		activityJSON.put("activity_type", "liking_comment");
		activityJSON.put("sender_id", userId);
		activityJSON.put("reciever_id", recieverId);
		
		ArangoInterfaceMethods.insertActivity(activityJSON);
	}
	
	public static void handleFollowActivity(JSONObject activityJSON, String userId) {
	}	
	
}
