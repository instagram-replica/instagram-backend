package persistence.sql.users;

import static utilities.Main.isUUID;

class Validation {
    static boolean isValidUser(User user) {
        // TODO: Implement
        return true;
    }

    static boolean isValidUserId(String userId) {
        return isUUID(userId);
    }
}
