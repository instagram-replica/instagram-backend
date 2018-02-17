package persistence.sql.users;

import java.util.List;
import java.util.stream.Collectors;

import static persistence.sql.users.Validation.isValidUser;
import static persistence.sql.users.Validation.isValidUserId;

public class Main {
    public static List<User> getAllUsers() {
        List<Model> results = Model.findAll();
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

        Model result = Model.findById(userId);

        if(result == null) {
            return null;
        }

        return mapModelToUser(result);
    }

    public static User createUser(User user) {
        if(!isValidUser(user)) {
            throw new RuntimeException(
                    "Cannot create user: Invalid user data"
            );
        }

        // TODO: Generate UUID
        // TODO: Create user

        return new User();
    }

    public static User updateUser(String userId, User user) {
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

        // TODO: Update user

        return new User();
    }

    public static User deleteUser(String userId) {
        if(!isValidUserId(userId)) {
            throw new RuntimeException(
                    "Cannot delete user: Invalid user ID"
            );
        }

        // TODO: Delete user

        return new User();
    }

    private static User mapModelToUser(Model model) {
        User user = new User();

        user.setId(model.getString("id"));
        user.setUsername(model.getString("username"));
        user.setEmail(model.getString("email"));
        user.setPasswordHash(model.getString("password_hash"));
        user.setPublic(model.getBoolean("is_public"));
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

        String gender = model.getString("username");

        if (gender != null) {
            switch (gender) {
                case "male":
                    user.setGender(Gender.MALE);
                    break;
                case "female":
                    user.setGender(Gender.FEMALE);
                    break;
                case "undefined":
                    user.setGender(Gender.UNDEFINED);
                    break;
                default:
                    user.setGender(null);
                    break;
            }
        }

        return user;
    }
}
