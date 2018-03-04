package services.posts;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.ArangoInterfaceMethods;
import persistence.sql.Post;
import shared.Helpers;
import utilities.Main;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Team1 {

    //Tested
    public static JSONObject createComment(JSONObject paramsObject, String loggedInUserId, String methodName) {
        String postId = paramsObject.getString("postId");
        String comment = paramsObject.getString("text");

        JSONObject commentJSON = createCommentJSON(comment, 0, loggedInUserId, postId);


        ArangoInterfaceMethods.insertCommentOnPost(postId, commentJSON);
        JSONObject jsonValue = new JSONObject();
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject newComment = new JSONObject();
        newComment.put("postId", postId);
        newComment.put("text", comment);
        newComment.put("id", commentJSON.get("id"));
        data.put("newComment", newComment);
        response.put("data", data);
        response.put("error", "0");
        jsonValue.put("method", methodName);
        jsonValue.put("response", response);
        return jsonValue;
    }

    //tested
    public static JSONObject getCommentsOnPost(JSONObject paramsObject, String userId, String methodName) {
        String postId = paramsObject.getString("postId");
        JSONArray comments = ArangoInterfaceMethods.getCommentsOnPost(postId);
        JSONObject jsonValue = new JSONObject();
        jsonValue.put("method", methodName);
        jsonValue.put("comments", comments);
        jsonValue.put("error", "0");
        return jsonValue;
    }

    //Tested
    private static JSONObject createCommentJSON(String text, int depth, String userId, String postId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", Main.generateUUID());
        jsonObject.put("text", text);
        jsonObject.put("depth", depth);
        jsonObject.put("user_id", userId);
        if (postId != null) {
            jsonObject.put("post_id", postId);
            jsonObject.put("comments", new JSONArray());
        }
        jsonObject.put("created_at", new Timestamp(System.currentTimeMillis()));
        jsonObject.put("updated_at", new Timestamp(System.currentTimeMillis()));
        return jsonObject;
    }

    //Tested
    public static JSONObject createCommentReply(JSONObject paramsObject, String userId, String methodname) {

        String commentId = paramsObject.getString("commentId");
        String reply = paramsObject.getString("text");

        JSONObject replyJson = createCommentJSON(reply, 1, userId, null);


        ArangoInterfaceMethods.insertCommentReply(commentId, replyJson);


        JSONObject jsonValue = new JSONObject();
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject newReply = new JSONObject();


        newReply.put("id", replyJson.getString("id"));
        newReply.put("parentCommentId", commentId);
        newReply.put("text", reply);

        data.put("newReply", newReply);

        response.put("data", data);
        response.put("error", "0");

        jsonValue.put("method", methodname);
        jsonValue.put("response", response);

        return jsonValue;

    }
}
