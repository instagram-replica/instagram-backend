package services.posts;


import com.arangodb.ArangoDBException;
import exceptions.CustomException;
import org.json.JSONException;
import org.json.JSONObject;
import shared.Settings;

public class Controller extends shared.mq_server.Controller{
    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) throws Exception {
        try{
        String methodName = jsonObject.getString("method");
        JSONObject paramsObject = jsonObject.getJSONObject("params");
        switch (methodName) {
            case "getPosts":
                return Posts.getPosts(paramsObject, userId, methodName);
            case "getPost":
                return Posts.getPost(paramsObject, userId, methodName);
            case "createPost":
                return Posts.createPost(paramsObject, userId, methodName);
            case "getTaggedPosts":
                return Posts.getTaggedPosts(paramsObject, userId, methodName);
            case "deletePost":
                return Posts.deletePost(paramsObject, userId, methodName);
            case "createPostLike":
                return Posts.createPostLike(paramsObject, userId, methodName);
//            case "deletePostLike":
//                return Posts.deletePostLike(paramsObject,userId,methodName);
            case "createComment":
                return Comments.createComment(paramsObject, userId, methodName);
            case "getComments":
                return Comments.getCommentsOnPost(paramsObject, userId, methodName);
            case "createCommentReply":
                return Comments.createCommentReply(paramsObject, userId, methodName);
            case "getPostLikers":
                return Posts.getPostLikers(paramsObject, userId, methodName);
            case "updatePost":
                return Posts.updatePost(paramsObject, userId, methodName);
            default: {
                JSONObject newJsonObj = new JSONObject();
                newJsonObj.put("application", "feed/posts");
                return newJsonObj;
            }
        }
        }
//        catch(CustomException e){
//            throw new Exception();
//        }
        catch(org.json.JSONException  e){
//           //TODO error json
            System.out.println("JSON ERROR");
            JSONObject newJsonObj = new JSONObject();
            newJsonObj.put("application", "feed/posts");
            return newJsonObj;
        }
        catch(ArangoDBException e){
//           //TODO error json
            System.out.println("JSON ERROR");
            JSONObject newJsonObj = new JSONObject();
            newJsonObj.put("application", "feed/posts");
            return newJsonObj;
        }
        catch(Exception e){
            //TODO internal server error
            System.err.println(e.getMessage());
            System.out.println("JSON ERROR");
            JSONObject newJsonObj = new JSONObject();
            newJsonObj.put("application", "feed/posts");
            return newJsonObj;

        }
    }


}
