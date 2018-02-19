package services.users;
import persistence.sql.users.Main;
import persistence.sql.users.User;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;


import org.json.JSONObject;
import persistence.sql.users.*;

public class Authentication {

    public static JSONObject SignUp(JSONObject params, String userId){
        User newUser = new User();
        newUser.setUsername(params.getString("userName"));
        newUser.setFullName(params.getString("fullName"));
        newUser.setPasswordHash(params.getString("passwordHash"));
        newUser.setEmail(params.getString("email"));
        newUser.setPhoneNumber(params.getString("phone"));

        JSONObject response = new JSONObject();
        response.put("sessionId", 12);
        return response;
    }

    public static JSONObject SignIn(JSONObject params, String userId){
        User user = Main.getUserById(params.getString("username"));
        if(user.getPasswordHash().equals(params.getString("password"))){
            JSONObject session = new JSONObject();
            session.put("sessionId", 12);
            JSONObject response = new JSONObject();
            response.put("response", session);
            return new JSONObject().put("error", "null");
        }
        else return null;
    }

    public static JSONObject GetUserInfo(JSONObject jsonObject, String userID){

        JSONObject userData = new JSONObject();

        User requestedUser = Main.getUserById(jsonObject.getString("userId"));

        JSONObject userProfile = new JSONObject();

        userProfile.put("userId",userID);
        userProfile.put("username",requestedUser.getUsername());
        userProfile.put("name",requestedUser.getFullName());
        userProfile.put("avatar",requestedUser.getProfilePictureUrl());
        userProfile.put("bio",requestedUser.getBio());
        userProfile.put("website",requestedUser.getWebsiteUrl());
        userProfile.put("noOfPosts",100);
        userProfile.put("noOfFollowers",100);
        userProfile.put("noOfFollowing",100);

        if(requestedUser.isPublic())
            userProfile.put("privacy","public");
        else
            userProfile.put("privacy","private");


        if(userID.equals(jsonObject.get("userId"))) {

            userProfile.put("following",true);
            userProfile.put("followers",true);

        } else {

            //TODO  set the following and followers flag based on the following/followers
            // lists of the requested user

        }

        userData.put("method","getUserInfo");
        userData.put("profile",userProfile);
        userData.put("error", "null");

           return userData;
    }


}
