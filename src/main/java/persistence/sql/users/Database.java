package persistence.sql.users;

import exceptions.DatabaseException;
import persistence.sql.users.Models.UserModel;

import java.util.List;
import java.util.stream.Collectors;

import static persistence.sql.Helpers.constructList;
import static persistence.sql.users.Helpers.mapModelToUser;
import static persistence.sql.users.Helpers.mapUserToModel;

public class Database {
    public static List<String> getAllUsersIds() {
        List<UserModel> results = UserModel.findBySQL("SELECT id FROM users");
        return results
                .parallelStream()
                .map(Helpers::mapModelToUser)
                .map(User::getId)
                .collect(Collectors.toList());
    }

    public static User getUserById(String id) {
        UserModel model = UserModel.findFirst("id = ?", id);
        return mapModelToUser(model);
    }

    public static List<User> getUsersByIds(String[] ids) {
        /*
         * Query looks unsafe, but here's the source:
         * http://javalite.io/in_clause
         */
        List<UserModel> results = UserModel.where(
                "id IN (" + constructList(ids) + ")"
        );

        return results
                .parallelStream()
                .map(Helpers::mapModelToUser)
                .collect(Collectors.toList());
    }

    public static User getUserByEmail(String email) {
        UserModel model = UserModel.findFirst("email = ?", email);
        return mapModelToUser(model);
    }

    public static List<String> getUsersIdsByUsernames(String[] usernames) {
        /*
         * Query looks unsafe, but here's the source:
         * http://javalite.io/in_clause
         */
        List<UserModel> results = UserModel.where(
                "username IN (" + constructList(usernames) + ")"
        );

        return results
                .parallelStream()
                .map(Helpers::mapModelToUser)
                .map(User::getId)
                .collect(Collectors.toList());
    }

    public static List<User> searchUsersByFullName(String fullName, int offset, int limit) {
        List<UserModel> models = UserModel
                .findBySQL(
                        "SELECT * FROM users WHERE LOWER(full_name) LIKE '%' || ? || '%'" +
                                "OFFSET ? " +
                                "LIMIT ?",
                        fullName.toLowerCase(),
                        offset,
                        limit
                );

        return models.parallelStream()
                .map(Helpers::mapModelToUser)
                .collect(Collectors.toList());
    }

    public static User createUser(User user) throws DatabaseException {
        UserModel model = mapUserToModel(user);
        boolean isInserted = model.insert();

        if (!isInserted) {
            throw new DatabaseException(
                    "Record not inserted in users table: " + user.toString()
            );
        }

        return getUserById(user.id);
    }

    public static User updateUser(User user) throws DatabaseException {
        UserModel model = mapUserToModel(user);
        boolean isUpdated = model.saveIt();

        if (!isUpdated) {
            throw new DatabaseException(
                    "Record not updated in users table: " + user.toString()
            );
        }

        return getUserById(user.id);
    }
}
