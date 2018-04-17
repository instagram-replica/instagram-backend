package services.users;

import auth.BCrypt;
import exceptions.AuthenticationException;
import persistence.nosql.ArangoInterfaceMethods;
import persistence.nosql.GraphMethods;
import persistence.sql.users.Database;
import exceptions.DatabaseException;
import persistence.sql.users.User;
import exceptions.ValidationException;
import services.users.validation.ValidationResult;
import services.users.validation.ValidationResultType;
import services.users.validation.Validator;

import java.util.List;

import static utilities.Main.generateUUID;

public class Logic {
    public static User signup(User inputUser) throws ValidationException, DatabaseException {
        User modifiedUser = new User.Builder(inputUser)
                .id(generateUUID())
                .passwordHash(BCrypt.hashPassword(inputUser.password))
                .build();

        ValidationResult validationResult = Validator.validateUserForInsert(modifiedUser);

        if (validationResult.type == ValidationResultType.FAILURE) {
            throw new ValidationException(validationResult.message);
        }

        if (Database.getUserByEmail(modifiedUser.email) != null) {
            throw new DatabaseException("Email already exists");
        }

        if (Database.getUserByUsername(modifiedUser.username) != null) {
            throw new DatabaseException("Username already exists");
        }

        GraphMethods.makeUserNode(modifiedUser.id);

        return Database.createUser(modifiedUser);
    }

    public static User login(String email, String password)
            throws ValidationException, AuthenticationException {
        ValidationResult validationResult = Validator.validateCredentials(email, password);

        if (validationResult.type == ValidationResultType.FAILURE) {
            throw new ValidationException(validationResult.message);
        }

        User matchedUser = Database.getUserByEmail(email);

        if (matchedUser == null) {
            throw new AuthenticationException(
                    "Email provided cannot be found: " + email
            );
        }

        boolean doPasswordsMatch = BCrypt.comparePassword(
                password,
                matchedUser.passwordHash
        );

        if (!doPasswordsMatch) {
            throw new AuthenticationException(
                    "Password provided mismatches authentic password"
            );
        }

        return matchedUser;
    }

    public static User getUser(String userId) throws ValidationException, DatabaseException {
        ValidationResult validationResult = Validator.validateId(userId);

        if (validationResult.type == ValidationResultType.FAILURE) {
            throw new ValidationException(validationResult.message);
        }

        User user = Database.getUserById(userId);

        if (user == null) {
            throw new DatabaseException("User ID does not exist: " + userId);
        }

        return user;
    }

    public static User updateUser(User user) throws ValidationException, DatabaseException {
        ValidationResult validationResult = Validator.validateUserForUpdate(user);

        if (validationResult.type == ValidationResultType.FAILURE) {
            throw new ValidationException(validationResult.message);
        }

        return Database.updateUser(user);
    }

    public static List<User> searchUsers(String term, int offset, int limit) throws ValidationException {
        ValidationResult validationResult = Validator.validatePaginationArgs(offset, limit);

        if (validationResult.type == ValidationResultType.FAILURE) {
            throw new ValidationException(validationResult.message);
        }

        return Database.searchUsersByFullName(term, offset, limit);
    }

    public static List<User> getUsersByIds(String[] ids) throws ValidationException {
        for (String id : ids) {
            ValidationResult validationResult = Validator.validateId(id);

            if (validationResult.type == ValidationResultType.FAILURE) {
                throw new ValidationException(validationResult.message);
            }
        }

        return Database.getUsersByIds(ids);
    }

    public static List<String> getUsersIdsByUsernames(String[] usernames) throws ValidationException {
        for (String username : usernames) {
            ValidationResult validationResult = Validator.validateUsername(username);

            if (validationResult.type == ValidationResultType.FAILURE) {
                throw new ValidationException(validationResult.message);
            }
        }

        return Database.getUsersIdsByUsernames(usernames);
    }

    public static boolean isAuthorizedToView(String viewerId, String viewedId)
            throws DatabaseException, ValidationException {
        ValidationResult validationResult = Validator.validateId(viewerId);

        if (validationResult.type == ValidationResultType.FAILURE) {
            throw new ValidationException(validationResult.message);
        }

        validationResult = Validator.validateId(viewedId);

        if (validationResult.type == ValidationResultType.FAILURE) {
            throw new ValidationException(validationResult.message);
        }

        if (viewerId.equals(viewedId)) {
           return true;
        }

        // TODO @ARANGODB: Check if viewer is blocking viewed
        boolean hasViewerBlockedViewed = false;
        // TODO @ARANGODB: Check if viewed is blocking viewer
        boolean hasViewedBlockedViewer = false;

        if (hasViewerBlockedViewed || hasViewedBlockedViewer) {
            return false;
        }

        User viewedUser = Logic.getUser(viewedId);

        if (!viewedUser.isPrivate) {
            return true;
        }

        // TODO @ARANGODB: Check if viewer is following viewed
        boolean hasViewerFollowedViewed = true;

        if (hasViewerFollowedViewed) {
            return true;
        }

        return false;
    }
}
