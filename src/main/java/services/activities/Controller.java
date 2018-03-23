package services.activities;

import org.json.JSONObject;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;
import static utilities.Main.readPropertiesFile;

public class Controller extends shared.mq_server.Controller {

    Properties props;

    public Controller(){
        super();
        try {
            props = readPropertiesFile("src/main/resources/activities_mapper.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject newJsonObj = new JSONObject();
        JSONObject error = new JSONObject();

        String methodName = jsonObject.getString("method");
        String methodSignature = props.getProperty(methodName);
        JSONObject paramsObject = jsonObject.getJSONObject("params");

        try {
            Method method = NotificationActions.class.getMethod(methodSignature, JSONObject.class, String.class);
            newJsonObj = (JSONObject) method.invoke(null,paramsObject, userId);
        }
        catch(org.json.JSONException e){
            e.printStackTrace();
            error.put("description",utilities.Main.stringifyJSONException(e));
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getMessage());
            error.put("description","Internal Server Error");
        }

        JSONObject response = new JSONObject();
        response.put("error",error);
        response.put("data",newJsonObj);
        return response;
//
//        //interface insert method, change params of json object to match different activity
//        //types
//        switch (methodName) {
//            case "createPostWithTag":
//                handlePostTagNotification(paramsObject, userId);
//                break;
//            case "createPostLike":
//                NotificationActions.handlePostLikeNotification(paramsObject, userId);
//                break;
//            case "createComment":
//                NotificationActions.handleCommentNotification(paramsObject, userId);
//                break;
//            case "createCommentLike":
//                NotificationActions.handleCommentNotification(paramsObject, userId);
//                break;
//            case "createCommentReply":
//                NotificationActions.handleCommentReplyNotification(paramsObject, userId);
//                break;
//            case "createFollow":
//                NotificationActions.handleFollowNotification(paramsObject, userId);
//                break;
//            case "getNotifications":
//                newJsonObj = NotificationActions.handleGettingNotifications(paramsObject, userId);
//                break;
//            case "getActivities":
//                newJsonObj = ActivityActions.handleGettingActivities(paramsObject, userId);
//                break;
//
//            default:
//                break;
//        }
//        return newJsonObj;
    }

}
