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
            System.out.println("METHODNAME:  "+methodName);
        switch (methodName) {
            case "getPosts":
                data = Posts.getPosts(paramsObject, userId, methodName); break;
            case "getPost":
                data = Posts.getPost(paramsObject, userId, methodName); break;
            case "createPost":
                data = Posts.createPost(paramsObject, userId, methodName); break;
            case "getTaggedPosts":
                data = Posts.getTaggedPosts(paramsObject, userId, methodName); break;
            case "deletePost":
                data = Posts.deletePost(paramsObject, userId, methodName); break;
            case "createPostLike":
                data = Posts.createPostLike(paramsObject, userId, methodName); break;
            case "deletePostLike":
                data= Posts.deletePostLike(paramsObject,userId,methodName); break;
            case "createComment":
                data = Comments.createComment(paramsObject, userId, methodName); break;
            case "getComments":
                data = Comments.getCommentsOnPost(paramsObject, userId, methodName); break;
            case "createCommentReply":
                data = Comments.createCommentReply(paramsObject, userId, methodName); break;
            case "getPostLikers":
                data = Posts.getPostLikers(paramsObject, userId, methodName); break;
            case "updatePost":
                data = Posts.updatePost(paramsObject, userId, methodName); break;
            case "getHashtagPosts":
                data = Posts.getHashtagPosts(paramsObject,userId,methodName); break;
            case "getDiscoverFeed":
                data= Posts.getDiscoverFeed(paramsObject,userId,methodName); break;
            case "createPostHashtags":
                data = Posts.createPostHashtags(paramsObject,userId,methodName);break;
            default: {
                JSONObject newJsonObj = new JSONObject();
                newJsonObj.put("application", "feed/posts");
                data = newJsonObj;
                break;
            }
        }
        }
        catch(org.json.JSONException e){
            e.printStackTrace();
            error.put("description",utilities.Main.stringifyJSONException(e));
        }
        catch(CustomException e){
            e.printStackTrace();
            error.put("description", e.getMessage());
        }
        catch(Exception e){
            e.printStackTrace();
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
