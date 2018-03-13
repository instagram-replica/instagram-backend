package services.users;

import persistence.sql.users.Main;
import persistence.sql.users.User;
import org.json.JSONObject;


import static shared.Helpers.createJSONError;

public class Authentication {

    public static JSONObject authorizedToView(String viewerId, String toBeViewedId) {
        //TODO:
        JSONObject resJSONOb = new JSONObject();
        resJSONOb.put("authorized", true);
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
        //TODO: If the user is private and userId is not following requested user return meaningful json with private user as message
        JSONObject userData = new JSONObject();
        User requestedUser = null;
        try {
            requestedUser = Main.getUserById(jsonObject.getString("userId"));
            JSONObject userProfile = new JSONObject();

            userProfile.put("userId", requestedUser.getId());
            userProfile.put("username", requestedUser.getUsername());
            userProfile.put("name", requestedUser.getFullName());
            userProfile.put("avatar", requestedUser.getProfilePictureUrl());
            userProfile.put("bio", requestedUser.getBio());
            userProfile.put("website", requestedUser.getWebsiteUrl());
            // TODO: Connect with the nosql
            userProfile.put("noOfPosts", 100);
            userProfile.put("noOfFollowers", 100);
            userProfile.put("noOfFollowing", 100);

            if (requestedUser.isPrivate())
                userProfile.put("privacy", "private");
            else
                userProfile.put("privacy", "public");


            if (userID.equals(jsonObject.get("userId"))) {

                userProfile.put("following", true);
                userProfile.put("followers", true);

            } else {

                //TODO set the following and followers flag based on the following/followers
                // lists of the requested user

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
