package services.users.validation;

import persistence.sql.users.User;

import static utilities.Main.isUUID;

public class Validator {
    public static ValidationResult validateUser(User user) {
        if (user.id == null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "User's ID cannot be undefined"
            );
        }

        if (user.username == null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "User's username cannot be undefined"
            );
        }

        if (user.email == null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "User's email cannot be undefined"
            );
        }

        if (user.password == null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "User's password cannot be undefined"
            );
        }

        if (user.isPrivate == null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "User's privacy flag cannot be undefined"
            );
        }

        if (user.fullName == null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "User's full name cannot be undefined"
            );
        }

        if (user.gender == null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "User's gender cannot be undefined"
            );
        }

        if (!isValidId(user.id)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid user ID: " + user.id
            );
        }

        if (!isValidPassword(user.password)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid user password"
            );
        }

        return new ValidationResult(ValidationResultType.SUCCESS);
    }

    public static ValidationResult validateCredentials(String email, String password) {
        if (!isValidEmail(email)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid user email: " + email
            );
        }

        if (!isValidPassword(password)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid user password"
            );
        }

        return new ValidationResult(ValidationResultType.SUCCESS);
    }

    public static ValidationResult validateId(String id) {
        if (!isValidId(id)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid user ID: " + id
            );
        }

        return new ValidationResult(ValidationResultType.SUCCESS);
    }

    private static boolean isValidId(String id) {
        return isUUID(id);
    }

    private static boolean isValidUsername(String username) {
        // TODO
        return true;
    }

    private static boolean isValidEmail(String email) {
        // TODO
        return true;
    }

    private static boolean isValidPassword(String password) {
        return password.length() >= 6;
    }
}
