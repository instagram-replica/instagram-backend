package services.posts.Actions;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.nosql.PostMethods;
import shared.Settings;
import utilities.Main;

import java.sql.Timestamp;

import static shared.Helpers.createJSONError;
import static shared.Helpers.isAuthorizedToView;

public class CreateComment implements Action {
    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) throws Exception {
        //TODO: Create activity for the post's owner, and check for mentions @ACTIVITIES_TEAM
        JSONObject params = new JSONObject();
        JSONObject activities = new JSONObject();

        String postId = jsonObject.getString("postId");
        JSONObject post = PostMethods.getPost(postId);

        String comment = jsonObject.getString("text");

        //TODO mentions
        //ArrayList<String> mentionsUserNames = getMentions(comment);
        //JSONArray mentionedUserIds = getUsersIdsByUsernames("posts", mentionsUserNames, loggedInUserId);

        JSONObject commentJSON = createCommentJSON(comment, 0, userId, postId);
        if (isAuthorizedToView(Settings.getInstance().getInstanceId(), userId, post.getString("user_id"), userId)) {
            PostMethods.insertCommentOnPost(postId, commentJSON);
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
            params.put("commentID", newComment.get("id"));
            params.put("receiverId", post.get("user_id"));
            activities.put("method", methodName);
            activities.put("params", params);
            shared.mq_server.Controller.send("posts", "activities", activities, userId);
            return jsonValue;
        }
        return createJSONError("Not authorized to view");
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
}
