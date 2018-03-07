package persistence.sql.users;

import static utilities.Main.isUUID;
import java.util.regex.*;
import java.util.List;
class Validation {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_PHONE_NUMBER =
            Pattern.compile("[0-9]+");

    static boolean isValidUser(User user) {

        Matcher emailMatcher = VALID_EMAIL_ADDRESS_REGEX .matcher(user.getEmail());
        Matcher phoneMatcher = VALID_PHONE_NUMBER .matcher(user.getPhoneNumber());

             if(!emailMatcher.matches())
                 throw new RuntimeException(
                         "inavlid email"
                 );
             if(!phoneMatcher.matches())
                 throw new RuntimeException(
                         "invalid phone"
                 );
             if(!Main.getUserByUsername(user.getUsername()).isEmpty()){
                 throw new RuntimeException(
                         "username exists"
                 );
             }
             if(!Main.getUserByEmail(user.getEmail()).isEmpty()){
                 throw new RuntimeException(
                          "email exists"
                 );
             }



        return true;
    }

    static boolean isValidUserId(String userId) {
        return isUUID(userId);
    }
}
