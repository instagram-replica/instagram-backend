package persistence.sql.users;

import org.joda.time.DateTime;
import persistence.sql.users.Models.UserModel;

import java.sql.Timestamp;

public class Helpers {
    public static UserModel mapUserToModel(User user) {
        return new UserModel()
                .set("id", user.id)
                .set("username", user.username)
                .set("email", user.email)
                .set("password_hash", user.passwordHash)
                .set("is_private", user.isPrivate)
                .set("full_name", user.fullName)
                .set("bio", user.bio)
                .set("phone_number", user.phoneNumber)
                .set("profile_picture_url", user.profilePictureUrl)
                .set("website_url", user.websiteUrl)
                .set("verified_at", new Timestamp(user.verifiedAt.toInstant().getMillis()))
//                .set("created_at", user.createdAt)
                .set("updated_at", new Timestamp(user.updatedAt.toInstant().getMillis()))
                .set("blocked_at", new Timestamp(user.blockedAt.toInstant().getMillis()))
                .set("deleted_at", new Timestamp(user.verifiedAt.toInstant().getMillis()));
    }

    public static User mapModelToUser(UserModel model) {
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
                .verifiedAt(new DateTime(model.getDate("verified_at")))
                .createdAt(new DateTime(model.getDate("created_at")))
                .updatedAt(new DateTime(model.getDate("updated_at")))
                .blockedAt(new DateTime(model.getDate("blocked_at")))
                .deletedAt(new DateTime(model.getDate("deleted_at")))
                .build();
    }
}
