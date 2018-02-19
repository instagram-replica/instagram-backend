package services.users;
import persistence.sql.users.Main;
import persistence.sql.users.User;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;


import org.json.JSONObject;
import persistence.sql.users.Main;
import persistence.sql.users.User;

public class Authentication {


    public static JSONObject SignIn(JSONObject paramsoject){
        String name=paramsoject.getString("username");
        String password=paramsoject.getString("passwordHash");
        User user = Main.getUserById(name);
        if(user.getPasswordHash().equals(password)){
            JSONObject response = new JSONObject();
            JSONObject item = new JSONObject();
            item.put("sessionId", 12);
            response.put("response", item);
            return response;
        }else
            return new JSONObject().put("error", "null");
    }

    public static JSONObject getUserInfo(JSONObject jsonObject, String userID){

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
