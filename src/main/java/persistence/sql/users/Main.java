package persistence.sql.users;


import org.json.JSONObject;

import org.javalite.activejdbc.Model;
import persistence.sql.users.Models.UsersBlockModel;
import persistence.sql.users.Models.UsersFollowModel;
import persistence.sql.users.Models.UsersModel;
import persistence.sql.users.Models.UsersReportModel;

import java.io.IOException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.postgresql.core.types.*;

import static persistence.sql.Main.closeConnection;
import static persistence.sql.Main.openConnection;
import static persistence.sql.users.Validation.isValidUser;
import static persistence.sql.users.Validation.isValidUserId;
import static utilities.Main.generateUUID;

public class Main {
    public static List<User> getAllUsers() {
        List<UsersModel> results = UsersModel.findAll();
        return results
                .stream()
                .map(Main::mapModelToUser)
                .collect(Collectors.toList());
    }

    public static User getUserById(String userId) {
        if(!isValidUserId(userId)) {
            throw new RuntimeException(
                    "Cannot fetch user: Invalid user ID"
            );
        }

        UsersModel result = UsersModel.findById(userId);

        if(result == null) {
            return null;
        }

        return mapModelToUser(result);
    }


    public static List getUserByUsername(String username){

        return UsersModel.findBySQL("SELECT user FROM users WHERE username=?",username);
    }

    public static List getUserByEmail(String email){

        return UsersModel.findBySQL("SELECT user FROM users WHERE email=?",email);
    }

    public static boolean createUser(User user) {
        if(!isValidUser(user)) {
            throw new RuntimeException(
                    "Cannot create user: Invalid user data"
            );
        }

        UsersModel usersModel = new UsersModel();
        usersModel.set("id", user.getId());
        usersModel.set("username", user.getUsername());
        usersModel.set("email", user.getEmail());
        usersModel.set("password_hash", user.getPasswordHash());
        usersModel.set("is_private", user.isPrivate());
        usersModel.set("full_name", user.getFullName());
        usersModel.set("gender", user.getGender());
        usersModel.set("bio", user.getBio());
        usersModel.set("phone_number", user.getPhoneNumber());
        usersModel.set("profile_picture_url", user.getProfilePictureUrl());
        usersModel.set("website_url", user.getWebsiteUrl());
        usersModel.set("verified_at", user.getVerifiedAt());


        return usersModel.insert();
    }

    public static boolean updateUser(String userId, User user) {
        if(!isValidUserId(userId)) {
            throw new RuntimeException(
                    "Cannot update user: Invalid user ID"
            );
        }

        if(!isValidUser(user)) {
            throw new RuntimeException(
                    "Cannot update user: Invalid user data"
            );
        }
        UsersModel usersModel = new UsersModel();
        usersModel.set("username", user.getUsername());
        usersModel.set("name", user.getFullName());
        usersModel.set("website", user.getWebsiteUrl());
        usersModel.set("bio", user.getBio());
        usersModel.set("phone", user.getPhoneNumber());
        usersModel.set("gender", user.getGender());
        usersModel.set("email", user.getEmail());
        usersModel.set("updated_at", new java.util.Date());
        return usersModel.saveIt();
    }

    public static boolean deleteUser(String userId) {
        if(!isValidUserId(userId)) {
            throw new RuntimeException(
                    "Cannot delete user: Invalid user ID"
            );
        }
        UsersModel userModel = UsersModel.findById(userId);
        userModel.set("deleted_at", new java.util.Date());
        return userModel.saveIt();
    }

//    public static boolean deactivateAccount(String userId) {
//        if(!isValidUserId(userId)) {
//            throw new RuntimeException(
//                    "Cannot deactivate account: Invalid user ID"
//            );
//        }
//
//        // TODO: deactivate account
//
//        return true;
//    }

    public static boolean blockUser(String blockerId,String blockedId) {
        if(!isValidUserId(blockerId) || !isValidUserId(blockedId)) {
            throw new RuntimeException(
                    "Cannot block user: Invalid user ID"
            );
        }
        UsersBlockModel newBlock = UsersBlockModel.create();
        newBlock.set("id", generateUUID());
        newBlock.set("blocker_id", blockedId);
        newBlock.set("blocked_id",blockedId);
        newBlock.set("created_at", new java.util.Date());
        return newBlock.saveIt();
    }


    public static boolean reportUser(String reporterId,String reportedId) {
        if(!isValidUserId(reporterId) || !isValidUserId(reportedId)) {
            throw new RuntimeException(
                    "Cannot report user: Invalid user ID"
            );
        }
        UsersReportModel newReport = new UsersReportModel();
        newReport.set("id", generateUUID());
        newReport.set("reporter_id", reporterId);
        newReport.set("reported_id", reportedId);
        return newReport.saveIt();
    }

    public static long getFollowingsCount(String userId){
        return UsersFollowModel.count("follower_id = ?",userId);
    }

    public static long getFollowersCount(String userId){
        return UsersFollowModel.count("followed_id = ?",userId);
    }

    public static List getFollowers(String userId){
       return UsersFollowModel.find("followed_id", userId).collect("follower_id");
    }

    public static List getFollowings(String userId){
        return UsersFollowModel.find("follower_id", userId).collect("followed_id");
    }

    public static boolean createFollow(String followerId, String followedId){
        UsersFollowModel usersFollowModel = new UsersFollowModel();
        usersFollowModel.set("follower_id", followerId);
        usersFollowModel.set("followed_id", followedId);
        usersFollowModel.set("id", generateUUID());
        usersFollowModel.set("created_at", new java.util.Date());
        return usersFollowModel.saveIt();
    }

    public static boolean deleteFollow(String followerId, String followedId){
        return (UsersFollowModel.delete("follower_id = ? AND followed_id = ?", followerId, followedId) == 1);
    }


    public static List searchForUser(String userFullName){

        return UsersModel.where("username like ?", "%?%", userFullName);

    }

    private static User mapModelToUser(UsersModel model) {
        User user = new User();

        user.setId(model.getString("id"));
        user.setUsername(model.getString("username"));
        user.setEmail(model.getString("email"));
        user.setPasswordHash(model.getString("password_hash"));
        user.setPrivate(model.getBoolean("is_private"));
        user.setFullName(model.getString("full_name"));
        user.setBio(model.getString("bio"));
        user.setPhoneNumber(model.getString("phone_number"));
        user.setProfilePictureUrl(model.getString("profile_picture_url"));
        user.setWebsiteUrl(model.getString("website_url"));
        user.setVerifiedAt(model.getDate("verified_at"));
        user.setCreatedAt(model.getDate("created_at"));
        user.setUpdatedAt(model.getDate("updated_at"));
        user.setBlockedAt(model.getDate("blocked_at"));
        user.setDeletedAt(model.getDate("deleted_at"));
        user.setNumberOfFollowers(""+getFollowersCount(user.getId()));
        user.setNumberOfFollowings(""+getFollowingsCount(user.getId()));

        String gender = model.getString("username");


        if (gender != null) {
            user.setGender(gender);
        }

        return user;
    }

}
