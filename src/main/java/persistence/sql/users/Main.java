package persistence.sql.users;


import org.javalite.activejdbc.Base;
import org.json.JSONObject;

import org.javalite.activejdbc.Model;
import persistence.sql.users.Models.UsersBlockModel;
import persistence.sql.users.Models.UsersFollowModel;
import persistence.sql.users.Models.UsersModel;
import persistence.sql.users.Models.UsersReportModel;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.postgresql.core.types.*;

import static persistence.sql.Helpers.constructList;
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

    public static List<String> getAllUsersIds() {
        List<UsersModel> results =  UsersModel.findBySQL("SELECT id FROM users");
        return results
                .stream()
                .map(Main::mapModelToUser)
                .map(User::getId)
                .collect(Collectors.toList());
    }

    public static User getUserById(String userId) throws Exception {
        if (!isValidUserId(userId)) {
            throw new Exception(
                    "Cannot fetch user: Invalid user ID"
            );
        }

        UsersModel result = UsersModel.findById(userId);

        if (result == null) {
            return null;
        }

        return mapModelToUser(result);
    }


    public static List<UsersModel> getUserByUsername(String username) {

        return UsersModel.findBySQL("SELECT user FROM users WHERE username=?", username);
    }

    public static List getUserByEmail(String email) {

        return UsersModel.findBySQL("SELECT user FROM users WHERE email=?", email);
    }

    public static List<User> getUsersByIds(String[] usersIds) {
        if (usersIds.length == 0) {
            return new ArrayList<>();
        }

        for (String userId : usersIds) {
            if (!isValidUserId(userId)) {
                throw new RuntimeException(
                        "Cannot fetch user: Invalid user ID: "
                        + userId
                );
            }
        }

        /*
        * Query looks unsafe, but here's the source:
        * http://javalite.io/in_clause
        */
        List<UsersModel> results = UsersModel.where(
                "id IN (" + constructList(usersIds) + ")"
        );

        return results
                .stream()
                .map(Main::mapModelToUser)
                .collect(Collectors.toList());
    }

    public static List<String> getUsersIdsByUsernames(String[] usernames) {
        if (usernames.length == 0) {
            return new ArrayList<>();
        }

        /*
         * Query looks unsafe, but here's the source:
         * http://javalite.io/in_clause
         */
        List<UsersModel> results = UsersModel.where(
                "username IN (" + constructList(usernames) + ")"
        );

        return results
                .stream()
                .map(Main::mapModelToUser)
                .map(User::getId)
                .collect(Collectors.toList());
    }

    public static boolean createUser(User user) throws Exception {
        if (isValidUser(user)) {
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
        return false;
    }

    public static boolean updateUser(String userId, User user) throws Exception {
        if (!isValidUserId(userId)) {
            throw new Exception(
                    "Cannot update user: Invalid user ID"
            );
        }

        if (isValidUser(user)) {
            UsersModel usersModel = UsersModel.findFirst("id = ?", userId);
            boolean set1 = usersModel.set("username", user.getUsername()).saveIt();
            boolean set2 = usersModel.set("full_name", user.getFullName()).saveIt();
            boolean set3 = usersModel.set("website_url", user.getWebsiteUrl()).saveIt();
            boolean set4 = usersModel.set("bio", user.getBio()).saveIt();
            boolean set5 = usersModel.set("phone_number", user.getPhoneNumber()).saveIt();
            boolean set6 = usersModel.set("gender", user.getGender()).saveIt();
            boolean set7 = usersModel.set("email", user.getEmail()).saveIt();
            boolean set8 = usersModel.set("updated_at", new java.util.Date()).saveIt();
            return set1 && set2 && set3 && set4 && set5 && set6 && set7 && set8;
        }
        return false;
    }

    public static boolean deleteUser(String userId) throws Exception {
        if (!isValidUserId(userId)) {
            throw new Exception(
                    "Cannot delete user: Invalid user ID"
            );
        }
        UsersModel userModel = UsersModel.findFirst("id = ?", userId);
        if(userModel == null) throw new Exception("User was not found");
        return userModel.delete();
    }

//    public static boolean deactivateAccount(String userId) {
//        if(!isValidUserId(userId)) {
//            throw new Exception(
//                    "Cannot deactivate account: Invalid user ID"
//            );
//        }
//
//        // TODO: deactivate account
//
//        return true;
//    }

    public static boolean blockUser(String blockerId, String blockedId) throws Exception {
        if (!isValidUserId(blockerId) || !isValidUserId(blockedId)) {
            throw new Exception(
                    "Cannot block user: Invalid user ID"
            );
        }
        UsersBlockModel newBlock = UsersBlockModel.create();
        long nextId = (long) UsersBlockModel.findAll().get(UsersBlockModel.findAll().size() - 1).get("id") + 1;

        newBlock.set("id", nextId);
        newBlock.set("blocker_id", blockerId);
        newBlock.set("blocked_id", blockedId);
        return newBlock.insert();
    }

    public static boolean blocks(String blockerId, String blockedId) throws Exception {
        if (!isValidUserId(blockerId) || !isValidUserId(blockedId)) {
            throw new Exception(
                    "Cannot block user: Invalid user ID"
            );
        }
        UsersBlockModel block = UsersBlockModel.findFirst("blocker_id = ? AND blocked_id = ?", blockerId, blockedId);
        return block != null;
    }

    public static boolean reportUser(String reporterId, String reportedId) throws Exception {
        if (!isValidUserId(reporterId) || !isValidUserId(reportedId)) {
            throw new Exception(
                    "Cannot report user: Invalid user ID"
            );
        }
        UsersReportModel newReport = UsersReportModel.create();
        long nextId = (long) UsersReportModel.findAll().get(UsersReportModel.findAll().size() - 1).get("id") + 1;

        newReport.set("id", nextId);
        newReport.set("reporter_id", reporterId);
        newReport.set("reported_id", reportedId);
        return newReport.insert();
    }

    public static boolean reports(String reporterId, String reportedId) throws Exception {
        if (!isValidUserId(reporterId) || !isValidUserId(reportedId)) {
            throw new Exception(
                    "Cannot block user: Invalid user ID"
            );
        }
        UsersReportModel report = UsersReportModel.findFirst("reporter_id = ? AND reported_id = ?", reporterId, reportedId);
        return report != null;
    }

    public static long getFollowingsCount(String userId) {
        return UsersFollowModel.count("follower_id = ?", userId);
    }

    public static long getFollowersCount(String userId) {
        return UsersFollowModel.count("followed_id = ?", userId);
    }

    public static List getFollowers(String userId) {
        return UsersFollowModel.find("followed_id", userId).collect("follower_id");
    }

    public static List getFollowings(String userId) {
        return UsersFollowModel.find("follower_id", userId).collect("followed_id");
    }

    public static boolean createFollow(String followerId, String followedId) {
        UsersFollowModel usersFollowModel = UsersFollowModel.create();
        usersFollowModel.set("follower_id", followerId);
        usersFollowModel.set("followed_id", followedId);
        usersFollowModel.set("id", generateUUID());
        usersFollowModel.set("created_at", new java.util.Date());
        return usersFollowModel.insert();
    }

    public static boolean deleteFollow(String followerId, String followedId) {
        return (UsersFollowModel.delete("follower_id = ? AND followed_id = ?", followerId, followedId) == 1);
    }


    public static List searchForUser(String userFullName, String searcher) throws Exception {

        List<UsersModel> allResults = UsersModel.findBySQL("SELECT* FROM users WHERE LOWER(full_name) LIKE '%' || ? || '%' limit 10", userFullName.toLowerCase());

        for(int i=0; i<allResults.size(); i++){
          User user1 = mapModelToUser(allResults.get(i));
           if(blocks(searcher,user1.getId())){
               allResults.remove(i);
           }
        }

        return allResults;
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
        user.setNumberOfFollowers("" + getFollowersCount(user.getId()));
        user.setNumberOfFollowings("" + getFollowingsCount(user.getId()));

        String gender = model.getString("username");


        if (gender != null) {
            user.setGender(gender);
        }

        return user;
    }

}
