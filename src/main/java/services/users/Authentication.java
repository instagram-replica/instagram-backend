package services.users;

import persistence.sql.users.Main;
import persistence.sql.users.Models.UsersModel;
import persistence.sql.users.User;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

import static persistence.nosql.ArangoInterfaceMethods.*;
import static shared.Helpers.createJSONError;

public class Authentication {

    public static JSONObject authorizedToView(String viewerId, String toBeViewedId) {
        User user;
        try {
            user = Main.getUserById(toBeViewedId);
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }

        boolean isPublic = user != null && !user.isPrivate();

        boolean isFollowing = isFollowing(viewerId, toBeViewedId);

        JSONObject resJSONOb = new JSONObject();
        resJSONOb.put("authorized", isPublic || isFollowing);
        return resJSONOb;
    }

    public static JSONObject SignUp(JSONObject params) {
        User newUser = new User();
        newUser.setUsername(params.getString("username"));
        newUser.setFullName(params.getString("fullname"));
        newUser.setEmail(params.getString("email"));
        newUser.setGender(params.getString("gender"));
        //TODO: Add dateOfBirth to users' table
//        newUser.setDateOfBirth((Date)params.get("dateOfBirth"));
        //TODO: @Maged send avatar param from the media server handler
//        newUser.setProfilePictureUrl(params.getString("avatar"));
        newUser.setPhoneNumber(params.getString("phone"));
        newUser.setPasswordHash(params.getString("passwordHash"));
        try {
            Main.createUser(newUser);
            JSONObject session = new JSONObject();
            session.put("sessionId", 20);
            JSONObject res = new JSONObject();
            res.put("response", session);
            return res;
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }
    }

    public static JSONObject SignIn(JSONObject params, String userId) {
        User user = null;
        try {
            user = Main.getUserById(params.getString("username"));
            if (user.getPasswordHash().equals(params.getString("password"))) {
                JSONObject session = new JSONObject();
                session.put("sessionId", 12);
                JSONObject response = new JSONObject();
                response.put("response", session);
                return response;
            } else return new JSONObject().put("error", "Password doesn't match");
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }
    }

    public static JSONObject GetUserInfo(JSONObject jsonObject, String userID) {
        //TODO: test number of posts, followers and followings

        JSONObject userData = new JSONObject();
        String requestedUserId = jsonObject.getString("userId");
        User requestedUser = null;
        ArrayList<String> followersIds = getAllfollowersIDs(requestedUserId);
        ArrayList<String> followingsIds = getAllfollowingIDs(requestedUserId);

        try {
            requestedUser = Main.getUserById(requestedUserId);

            if(!authorizedToView(userID, requestedUserId).getBoolean("authorized"))
                return new JSONObject().put("error", "User not authorized to view account");

            JSONObject userProfile = new JSONObject();

            userProfile.put("userId", requestedUser.getId());
            userProfile.put("username", requestedUser.getUsername());
            userProfile.put("name", requestedUser.getFullName());
            userProfile.put("avatar", requestedUser.getProfilePictureUrl());
            userProfile.put("bio", requestedUser.getBio());
            userProfile.put("website", requestedUser.getWebsiteUrl());
            userProfile.put("noOfPosts", getPosts(requestedUserId).length());
            userProfile.put("noOfFollowers", followersIds.size());
            userProfile.put("noOfFollowing", followingsIds.size());

            if (requestedUser.isPrivate())
                userProfile.put("privacy", "private");
            else
                userProfile.put("privacy", "public");


            if (userID.equals(jsonObject.get("userId"))) {

                userProfile.put("following", true);
                userProfile.put("followers", true);

            } else {
                userProfile.put("following", followingsIds.contains(userID));
                userProfile.put("followers", followersIds.contains(userID));
            }

            userData.put("method", "getUserInfo");
            userData.put("profile", userProfile);
            userData.put("error", "null");
        } catch (Exception e) {
            return createJSONError(e.getMessage());
        }

        return userData;
    }
}
