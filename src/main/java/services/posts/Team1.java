package services.posts;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.ArangoInterfaceMethods;
import persistence.sql.Post;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
public class Team1 {
    public static JSONObject getPosts(JSONObject paramsObject, String loggedInUserId) {
        int pageSize = paramsObject.getInt("pageSize");
        int pageIndex = paramsObject.getInt("pageIndex");
        String ownerId = paramsObject.getString("userId");
        List<Post> posts = Post.where("user_id = ? ", ownerId);
        List<JSONObject> jsonObjects =
                posts
                        .stream()
                        .map(post -> mapPostToJSONObj(post))
                        .collect(Collectors.toList());
        JSONObject jsonValue = new JSONObject();
        jsonValue.put("posts", jsonObjects);
        jsonValue.put("error", "0");
        return jsonValue;
    }
    public static JSONObject mapPostToJSONObj(Post post) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", post.get("id"));
        jsonObject.put("created_at", post.get("created_at"));
        jsonObject.put("updated_at", post.get("updated_at"));
        jsonObject.put("caption", post.get("caption"));
        return jsonObject;
    }
    public static JSONObject createComment(JSONObject paramsObject, String loggedInUserId, String methodName){
        String postId = paramsObject.getString( "postId");
        JSONObject comment = paramsObject.getJSONObject("text");
        ArangoInterfaceMethods.insertCommentOnPost(postId,comment);
        JSONObject jsonValue = new JSONObject();
        JSONObject response = new JSONObject();
        JSONObject data= new JSONObject();
        JSONObject newComment = new JSONObject();
        newComment.put("postId",postId);
        newComment.put("text", comment);
        newComment.put("id",loggedInUserId); //// ?????
        data.put("newComment",newComment);
        response.put("data",data);
        response.put("error", "0");
        jsonValue.put("method",methodName);
        jsonValue.put("response",response);
        return jsonValue;
    }
    public static JSONObject getCommentsOnPost(JSONObject paramsObject,String userId,String methodName) {
        String postId = paramsObject.getString( "postId");
        JSONArray comments =ArangoInterfaceMethods.getCommentsOnPost(postId);
        JSONObject jsonValue = new JSONObject();
        jsonValue.put("method",methodName);
        jsonValue.put("comments",comments);
        jsonValue.put("error","0");
        return jsonValue;
    }
    public static JSONObject createCommentReply(JSONObject paramsObject, String userId,String methodname){
        String commentId = paramsObject.getString( "commentId");
        String reply = paramsObject.getString("text");
        ArangoInterfaceMethods.insertCommentReply(commentId,reply);
        JSONObject jsonValue = new JSONObject();
        JSONObject response = new JSONObject();
        JSONObject data= new JSONObject();
        JSONObject newReply = new JSONObject();
        newReply.put("id",userId); // not sure if that is the meant id
        newReply.put("parentCommentId",commentId);
        newReply.put("text",reply);
        data.put("newReply",newReply);
        response.put("data",data);
        response.put("error","0");
        jsonValue.put("method",methodname);
        jsonValue.put("response",response);
        return  jsonValue;

    }
}
