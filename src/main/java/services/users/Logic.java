package services.users;

import auth.AuthenticationException;
import persistence.sql.users.DatabaseException;
import persistence.sql.users.User;
import services.users.validation.ValidationException;
import services.users.validation.ValidationResult;
import services.users.validation.ValidationResultType;

import static auth.BCrypt.comparePassword;
import static auth.BCrypt.hashPassword;
import static persistence.sql.users.Main.createUser;
import static persistence.sql.users.Main.getUserByEmail;
import static services.users.validation.Validator.validateCredentials;
import static services.users.validation.Validator.validateUser;
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

    public static User signin(User inputUser)
            throws ValidationException, DatabaseException, AuthenticationException {
        ValidationResult validationResult = validateCredentials(
                inputUser.email,
                inputUser.password
        );

        if (validationResult.type == ValidationResultType.FAILURE) {
            throw new ValidationException(validationResult.message);
        }

        User matchedUser = getUserByEmail(inputUser.email);

        if (matchedUser == null) {
            throw new AuthenticationException(
                    "Email provided cannot be found: " + inputUser.email
            );
        }

        boolean doPasswordsMatch = comparePassword(
                inputUser.password,
                matchedUser.passwordHash
        );

        if (!doPasswordsMatch) {
            throw new AuthenticationException(
                    "Password provided mismatches authentic password"
            );
        }

        return matchedUser;
    }
}
