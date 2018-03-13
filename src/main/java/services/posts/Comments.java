package services.posts;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.ArangoInterfaceMethods;
import utilities.Main;

import java.sql.Timestamp;
import java.util.ArrayList;

import static shared.Helpers.createJSONError;
import static shared.Helpers.isAuthorizedToView;

public class Comments {

    private static ArrayList<String> getMentions(String text) {
        String[] split = text.split(" ");
        ArrayList<String> mentions = new ArrayList<>();

        for (String word : split) {
            if (word.startsWith("@")) {
                mentions.add(word.substring(1, word.length()));
            }
        }
        return mentions;
    }

    public static JSONObject createComment(JSONObject paramsObject, String loggedInUserId, String methodName) {
        //TODO: Create activity for the post's owner, and check for mentions @ACTIVITIES_TEAM
        try {
            String postId = paramsObject.getString("postId");
            JSONObject post = ArangoInterfaceMethods.getPost(postId);

            String comment = paramsObject.getString("text");

            //@TODO: @USERS_TEAM send usernames to the user service and get array of user ids back
            ArrayList<String> mentionsUserNames = getMentions(comment);

            JSONObject commentJSON = createCommentJSON(comment, 0, loggedInUserId, postId);
            if (isAuthorizedToView("posts", loggedInUserId, post.getString("user_id"))) {
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
                response.put("error", "null");
                jsonValue.put("method", methodName);
                jsonValue.put("response", response);
                return jsonValue;
            }
            return createJSONError("Not authorized to view");
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }
    }

    public static JSONObject getCommentsOnPost(JSONObject paramsObject, String loggedInUserId, String methodName) {
        String postId = paramsObject.getString("postId");
        try {
            JSONObject post = ArangoInterfaceMethods.getPost(postId);
            if (isAuthorizedToView("posts", loggedInUserId, post.getString("user_id"))) {
                JSONArray comments = ArangoInterfaceMethods.getCommentsOnPost(postId);
                JSONObject jsonValue = new JSONObject();
                jsonValue.put("method", methodName);
                jsonValue.put("count", comments.length());
                jsonValue.put("comments", comments);
                jsonValue.put("error", "null");
                return jsonValue;
            }
            return createJSONError("Not authorized to view");
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }
    }

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

    public static JSONObject createCommentReply(JSONObject paramsObject, String userId, String methodName) {
        //TODO: Create activity for the post's owner, and check for mentions @ACTIVITIES_TEAM
        String commentId = paramsObject.getString("commentId");
        String reply = paramsObject.getString("text");

        JSONObject replyJson = createCommentJSON(reply, 1, userId, null);


        //@TODO: @USERS_TEAM send usernames to the user service and get array of user ids back
        ArrayList<String> mentionsUserNames = getMentions(reply);

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
        response.put("error", "null");

        jsonValue.put("method", methodName);
        jsonValue.put("response", response);

        return jsonValue;

    }
}