package services.posts.Actions;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.PostMethods;
import shared.mq_server.Controller;
import utilities.Main;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class CreateCommentReply implements Action {
    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) throws IOException, InterruptedException {
        //TODO: Create activity for the post's owner, and check for mentions @ACTIVITIES_TEAM
        String commentId = jsonObject.getString("commentId");
        String reply = jsonObject.getString("text");
        JSONObject params = new JSONObject();
        JSONObject activities = new JSONObject();
        JSONObject replyJson = createCommentJSON(reply, 1, userId, null);

        //@TODO: @USERS_TEAM send usernames to the user service and get array of user ids back
        ArrayList<String> mentionsUserNames = getMentions(reply);

        PostMethods.insertCommentReply(commentId, replyJson);

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
        params.put("commentID", newReply.get("id"));
        params.put("receiverId", jsonObject.getString("commentOwnerId"));
        activities.put("method", methodName);
        activities.put("params", params);
        Controller.send("posts", "activities", activities, userId);
        return jsonValue;

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
}
