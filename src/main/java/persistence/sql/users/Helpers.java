package persistence.sql.users;

import org.joda.time.DateTime;
import persistence.sql.users.Models.UserModel;

import java.sql.Timestamp;
import java.util.Date;

public class Helpers {
    public static UserModel mapUserToModel(User user) {
        UserModel model = new UserModel();

        model.set("id", user.id);

        if (user.username != null) {
            model.set("username", user.username);
        }

        if (user.email != null) {
            model.set("email", user.email);
        }

        if (user.passwordHash != null) {
            model.set("password_hash", user.passwordHash);
        }

        if (user.isPrivate != null) {
            model.set("is_private", user.isPrivate);
        }

        if (user.fullName != null) {
            model.set("full_name", user.fullName);
        }

        if (user.bio != null) {
            model.set("bio", user.bio);
        }

        if (user.phoneNumber != null) {
            model.set("phone_number", user.phoneNumber);
        }

        if (user.profilePictureUrl != null) {
            model.set("profile_picture_url", user.profilePictureUrl);
        }

        if (user.websiteUrl != null) {
            model.set("website_url", user.websiteUrl);
        }

        if (user.verifiedAt != null) {
            model.set("verified_at", new Timestamp(user.verifiedAt.toInstant().getMillis()));
        }

        if (user.updatedAt != null) {
            model.set("updated_at", new Timestamp(user.updatedAt.toInstant().getMillis()));
        }

        if (user.blockedAt != null) {
            model.set("blocked_at", new Timestamp(user.blockedAt.toInstant().getMillis()));
        }

        if (user.deletedAt != null) {
            model.set("deleted_at", new Timestamp(user.deletedAt.toInstant().getMillis()));
        }

        return model;
    }

    public static User mapModelToUser(UserModel model) {
        if (model == null) {
            return null;
        }

        return new User.Builder()
                .id(model.getString("id"))
                .username(model.getString("username"))
                .email(model.getString("email"))
                .passwordHash(model.getString("password_hash"))
                .isPrivate(model.getBoolean("is_private"))
                .fullName(model.getString("full_name"))
                .bio(model.getString("bio"))
                .phoneNumber(model.getString("phone_number"))
                .profilePictureUrl(model.getString("profile_picture_url"))
                .websiteUrl(model.getString("website_url"))
                .verifiedAt(mapJavaDateToJodaDateTime(model.getDate("verified_at")))
                .createdAt(mapJavaDateToJodaDateTime(model.getDate("created_at")))
                .updatedAt(mapJavaDateToJodaDateTime(model.getDate("updated_at")))
                .blockedAt(mapJavaDateToJodaDateTime(model.getDate("blocked_at")))
                .deletedAt(mapJavaDateToJodaDateTime(model.getDate("deleted_at")))
                .build();
    }

    private static DateTime mapJavaDateToJodaDateTime(Date date) {
        return date == null? null : new DateTime(date);
    }
}
