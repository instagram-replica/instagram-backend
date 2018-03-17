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
		JSONObject paramsObject = jsonObject.getJSONObject("params");
        
        //interface insert method, change params of json object to match different activity
        //types
        switch (methodName) {
		case "createPostWithTag":
			NotificationActions.handlePostTagNotification(paramsObject,userId);
			break;
		case "createPostLike":
			NotificationActions.handlePostLikeNotification(paramsObject, userId);
			break;
		case "createComment":
			NotificationActions.handleCommentNotification(paramsObject, userId);
			break;	
		case "createCommentLike":
			NotificationActions.handleCommentNotification(paramsObject, userId);
			break;
		case "createCommentReply":
			NotificationActions.handleCommentReplyNotification(paramsObject, userId);
			break;
		case "createFollow":
			NotificationActions.handleFollowNotification(paramsObject, userId);
			break;
		case "getNotifications":
			newJsonObj = NotificationActions.handleGettingNotifications(paramsObject, userId);
			break;
		case "getActivities":
			newJsonObj = ActivityActions.handleGettingActivities(paramsObject, userId);
			break;
			
		default:
			break;
		}
        return newJsonObj;
    }

}
