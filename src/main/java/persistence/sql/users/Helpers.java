package persistence.sql.users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static persistence.sql.Helpers.toJodaDateTime;

public class Helpers {
  public static List<User> mapResultSetToUsers(ResultSet resultSet) throws SQLException {
    List<User> users = new ArrayList<>();

    while (resultSet.next()) {
      User user = new User.Builder()
        .id(resultSet.getString("id"))
        .username(resultSet.getString("username"))
        .email(resultSet.getString("email"))
        .passwordHash(resultSet.getString("password_hash"))
        .isPrivate(resultSet.getBoolean("is_private"))
        .fullName(resultSet.getString("full_name"))
        .bio(resultSet.getString("bio"))
        .gender(resultSet.getString("gender"))
        .phoneNumber(resultSet.getString("phone_number"))
        .profilePictureUrl(resultSet.getString("profile_picture_url"))
        .websiteUrl(resultSet.getString("website_url"))
        .verifiedAt(toJodaDateTime(resultSet.getDate("verified_at")))
        .createdAt(toJodaDateTime(resultSet.getDate("created_at")))
        .updatedAt(toJodaDateTime(resultSet.getDate("updated_at")))
        .blockedAt(toJodaDateTime(resultSet.getDate("blocked_at")))
        .deletedAt(toJodaDateTime(resultSet.getDate("deleted_at")))
        .build();

      users.add(user);
    }

    return users;
  }

  public static List<String> mapResultSetToIds(ResultSet resultSet) throws SQLException {
    List<String> ids = new ArrayList<>();

    while (resultSet.next()) {
      ids.add(resultSet.getString("id"));
    }

    return ids;
  }
}
