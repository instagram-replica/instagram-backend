package services.users;

import auth.AuthenticationException;
import persistence.sql.users.Database;
import persistence.sql.users.DatabaseException;
import persistence.sql.users.User;
import services.users.validation.ValidationException;
import services.users.validation.ValidationResult;
import services.users.validation.ValidationResultType;

import java.util.Arrays;
import java.util.List;

import static auth.BCrypt.comparePassword;
import static auth.BCrypt.hashPassword;
import static persistence.sql.users.Database.*;
import static services.users.validation.Validator.*;
import static utilities.Main.generateUUID;

public class Logic {
    public static User signup(User inputUser) throws ValidationException, DatabaseException {
        User modifiedUser = new User.Builder(inputUser)
                .id(generateUUID())
                .passwordHash(hashPassword(inputUser.password))
                .build();

        ValidationResult validationResult = validateUser(modifiedUser);

        if (validationResult.type == ValidationResultType.FAILURE) {
            throw new ValidationException(validationResult.message);
        }

        return createUser(modifiedUser);
    }

    public static User login(String email, String password)
            throws ValidationException, AuthenticationException {
        ValidationResult validationResult = validateCredentials(email, password);

        if (validationResult.type == ValidationResultType.FAILURE) {
            throw new ValidationException(validationResult.message);
        }

        User matchedUser = getUserByEmail(email);

        if (matchedUser == null) {
            throw new AuthenticationException(
                    "Email provided cannot be found: " + email
            );
        }

        boolean doPasswordsMatch = comparePassword(
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

    public static User getProfile(String userId) throws ValidationException {
        ValidationResult validationResult = validateId(userId);

        if (validationResult.type == ValidationResultType.FAILURE) {
            throw new ValidationException(validationResult.message);
        }

        return getUserById(userId);
    }

    public static User updateProfile(User user) throws ValidationException, DatabaseException {
        ValidationResult validationResult = validateUser(user);

        if (validationResult.type == ValidationResultType.FAILURE) {
            throw new ValidationException(validationResult.message);
        }

        return updateUser(user);
    }

    // TODO: Generalize search criteria
    public static List<User> searchUsersByFullName(String fullName, int offset, int limit) {
        // TODO: Validate args

        return Database.searchUsersByFullName(fullName, offset, limit);
    }

    public static List<User> getUsersByIds(String[] ids) {
        // TODO: Validate args
        return Database.getUsersByIds(ids);
    }

    public static List<String> getUsersIdsByUsernames(String[] usernames) {
        // TODO: Validate args
        return Database.getUsersIdsByUsernames(usernames);
    }
}
