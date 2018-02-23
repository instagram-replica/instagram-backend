package services.activities;

import org.json.JSONObject;

public class Controller extends shared.Controller {

    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject newJsonObj = new JSONObject();
        newJsonObj.put("application", "activities");
        String methodName = jsonObject.getString("method");
        switch (methodName) {
		case "createPost":
			NotificationActions.handlePostNotification(jsonObject, userId);
			break;
		case "createPostLike":
			ActivityActions.handlePostLikeActivity(jsonObject, userId);
			NotificationActions.handlePostLikeNotification(jsonObject, userId);
			break;
		case "createComment":
			NotificationActions.handleCommentNotification(jsonObject, userId);
			break;	
		case "createCommentLike":
			ActivityActions.handleCommentLikeActivity(jsonObject, userId);
			NotificationActions.handleCommentLikeNotification(jsonObject, userId);
			break;
		case "createCommentReply":
			NotificationActions.handleCommentReplyNotification(jsonObject, userId);
			break;
		case "createFollow":
			ActivityActions.handleFollowActivity(jsonObject, userId);
			NotificationActions.handleFollowNotification(jsonObject, userId);
			break;
		case "getActivities":
			//get activities method
			break;
			
		default:
			break;
		}
        return newJsonObj;
    }

}
