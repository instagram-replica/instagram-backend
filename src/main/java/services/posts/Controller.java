package services.posts;


import com.arangodb.ArangoDBException;
import exceptions.CustomException;
import org.json.JSONException;
import org.json.JSONObject;
import shared.Settings;

public class Controller extends shared.mq_server.Controller{
    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) throws Exception {
        JSONObject data = new JSONObject();
        JSONObject error = new JSONObject();
        try{
        String methodName = jsonObject.getString("method");
        JSONObject paramsObject = jsonObject.getJSONObject("params");
        switch (methodName) {
            case "getPosts":
                data = Posts.getPosts(paramsObject, userId, methodName);
            case "getPost":
                data = Posts.getPost(paramsObject, userId, methodName);
            case "createPost":
                data = Posts.createPost(paramsObject, userId, methodName);
            case "getTaggedPosts":
                data = Posts.getTaggedPosts(paramsObject, userId, methodName);
            case "deletePost":
                data = Posts.deletePost(paramsObject, userId, methodName);
            case "createPostLike":
                data = Posts.createPostLike(paramsObject, userId, methodName);
//            case "deletePostLike":
//                return Posts.deletePostLike(paramsObject,userId,methodName);
            case "createComment":
                data = Comments.createComment(paramsObject, userId, methodName);
            case "getComments":
                data = Comments.getCommentsOnPost(paramsObject, userId, methodName);
            case "createCommentReply":
                data = Comments.createCommentReply(paramsObject, userId, methodName);
            case "getPostLikers":
                data = Posts.getPostLikers(paramsObject, userId, methodName);
            case "updatePost":
                data = Posts.updatePost(paramsObject, userId, methodName);
            default: {
                JSONObject newJsonObj = new JSONObject();
                newJsonObj.put("application", "feed/posts");
                data = newJsonObj;
            }
        }
        }
        catch(org.json.JSONException e){
            error.put("description",utilities.Main.stringifyJSONException(e));
        }
        catch(CustomException e){
          error.put("description", e.getMessage());
        }
        catch(Exception e){
            //TODO internal server error
            System.err.println(e.getMessage());
            error.put("description","Internal Server Error");
        }
        finally {
            JSONObject response = new JSONObject();
            response.put("error",error);
            response.put("data",data);
            return response;
        }
    }


}
