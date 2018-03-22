package services.users.validation;

import persistence.sql.users.User;

import java.util.regex.Pattern;

import static utilities.Main.isUUID;

public class Validator {
    private static final Pattern VALID_USERNAME_REGEX = Pattern.compile(
            "^[A-Za-z0-9_]+$"
    );
    private static final Pattern VALID_EMAIL_REGEX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern VALID_FULL_NAME_REGEX = Pattern.compile(
            "^[\\p{L} .'-]+$"
    );
    private static final Pattern VALID_PHONE_NUMBER_REGEX = Pattern.compile(
            "^[0-9]+$"
    );
    private static final Pattern VALID_GENDER_REGEX = Pattern.compile(
            "^(male|female|undefined)$"
    );
    private static final Pattern VALID_URL_REGEX = Pattern.compile(
            "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
    );

    public static ValidationResult validateUserForInsert(User user) {
        if (user.id == null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "ID cannot be undefined"
            );
        }

        if (user.username == null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Username cannot be undefined"
            );
        }

        if (user.email == null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Email cannot be undefined"
            );
        }

        if (user.password == null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Password cannot be undefined"
            );
        }

        if (user.isPrivate == null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Privacy flag cannot be undefined"
            );
        }

        if (user.fullName == null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Full name cannot be undefined"
            );
        }

        if (user.gender == null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Gender cannot be undefined"
            );
        }

        if (!isValidId(user.id)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid ID: " + user.id
            );
        }

        if (!isValidUsername(user.username)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid username: " + user.username
            );
        }

        if (!isValidEmail(user.email)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid email: " + user.email
            );
        }

        if (!isValidPassword(user.password)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid password"
            );
        }

        if (!isValidFullName(user.fullName)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid full name: " + user.fullName
            );
        }

        if (!isValidGender(user.gender)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid gender: " + user.gender
            );
        }

        if (user.phoneNumber != null && !isValidPhoneNumber(user.phoneNumber)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid phone number: " + user.phoneNumber
            );
        }

        if (user.profilePictureUrl != null && !isValidUrl(user.profilePictureUrl)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid profile picture URL: " + user.profilePictureUrl
            );
        }

        if (user.websiteUrl != null && !isValidUrl(user.websiteUrl)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid website URL: " + user.websiteUrl
            );
        }

        return new ValidationResult(ValidationResultType.SUCCESS);
    }

    public static ValidationResult validateUserForUpdate(User user) {
        if (user.id == null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "ID cannot be undefined"
            );
        }

        if (user.username != null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Username cannot be updated"
            );
        }

        if (user.email != null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Email cannot be updated"
            );
        }

        if (user.password != null) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Password cannot be updated"
            );
        }

        if (!isValidId(user.id)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid ID: " + user.id
            );
        }

        if (user.fullName != null && !isValidFullName(user.fullName)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid full name: " + user.fullName
            );
        }

        if (user.gender != null && !isValidGender(user.gender)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid gender: " + user.gender
            );
        }

        if (user.phoneNumber != null && !isValidPhoneNumber(user.phoneNumber)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid phone number: " + user.phoneNumber
            );
        }

        if (user.profilePictureUrl != null && !isValidUrl(user.profilePictureUrl)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid profile picture URL: " + user.profilePictureUrl
            );
        }

        if (user.websiteUrl != null && !isValidUrl(user.websiteUrl)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid website URL: " + user.websiteUrl
            );
        }

        return new ValidationResult(ValidationResultType.SUCCESS);
    }

    public static ValidationResult validateCredentials(String email, String password) {
        if (!isValidEmail(email)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid email: " + email
            );
        }

        if (!isValidPassword(password)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid password"
            );
        }

        return new ValidationResult(ValidationResultType.SUCCESS);
    }

    public static ValidationResult validateId(String id) {
        if (!isValidId(id)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid ID: " + id
            );
        }

        return new ValidationResult(ValidationResultType.SUCCESS);
    }

    public static ValidationResult validateUsername(String username) {
        if (!isValidUsername(username)) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid username: " + username
            );
        }

        return new ValidationResult(ValidationResultType.SUCCESS);
    }

    public static ValidationResult validatePaginationArgs(int offset, int limit) {
        if (offset < 0) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid pagination offset: " + offset
            );
        }

        if (limit < 0) {
            return new ValidationResult(
                    ValidationResultType.FAILURE,
                    "Invalid pagination limit: " + limit
            );
        }

        return new ValidationResult(ValidationResultType.SUCCESS);
    }

    private static boolean isValidId(String id) {
        return isUUID(id);
    }

    private static boolean isValidUsername(String username) {
        return username.length() >= 3
                && username.length() <= 30
                && VALID_USERNAME_REGEX.matcher(username).find();
    }

    private static boolean isValidEmail(String email) {
        return VALID_EMAIL_REGEX.matcher(email).find();
    }

    private static boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private static boolean isValidFullName(String fullName) {
        return fullName.length() >= 3
                && fullName.length() <= 30
                && VALID_FULL_NAME_REGEX.matcher(fullName).find();
    }

    private static boolean isValidGender(String gender) {
        return VALID_GENDER_REGEX.matcher(gender).find();
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        return VALID_PHONE_NUMBER_REGEX.matcher(phoneNumber).find();
    }

    private static boolean isValidUrl(String url) {
        return VALID_URL_REGEX.matcher(url).find();
    }
}
