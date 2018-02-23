package services.activities;

import org.json.JSONObject;

import persistence.nosql.ArangoInterfaceMethods;

public class ActivityActions {
	
	public static void handlePostLikeActivity(JSONObject requestJSON, String userId) {	
		JSONObject params = requestJSON.getJSONObject("params");
		String postId = params.getString("postId");
		
		// create a new json object required to save the follow activity in the database
		// by calling ArangoInterfaceMethods.insertActivity 
		
		// [requestJSON] ----> this controller -----> [ArangoInterface] 
		
		JSONObject activityJSON = new JSONObject();
		activityJSON.put("activity_type", "liking_post");
		activityJSON.put("post_id", postId);
		activityJSON.put("sender_id", userId);
		
		ArangoInterfaceMethods.insertActivity(activityJSON);
		
	}
	
	public static void handleCommentLikeActivity(JSONObject activityJSON, String userId) {	
	}
	
	public static void handleFollowActivity(JSONObject activityJSON, String userId) {
	}	
	
}
