package persistence.sql.users;

import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

import static persistence.sql.Helpers.constructPlaceholdersList;
import static persistence.sql.Helpers.toTimestamp;

public class Database {
  public static List<String> getAllUsersIds() throws SQLException {
    Connection connection = persistence.sql.Database.getConnection();
    String query = "SELECT id FROM users";

    PreparedStatement statement = connection.prepareStatement(query);
    ResultSet resultSet = statement.executeQuery();

    return Helpers.mapResultSetToUsers(resultSet)
      .parallelStream()
      .map(User::getId)
      .collect(Collectors.toList());
  }

  public static User getUserById(String id) throws SQLException {
    Connection connection = persistence.sql.Database.getConnection();
    String query = "SELECT * FROM users WHERE id = ? LIMIT 1";

    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, id);

    ResultSet resultSet = statement.executeQuery();
    List<User> results = Helpers.mapResultSetToUsers(resultSet);

    return results.isEmpty() ? null : results.get(0);
  }

  public static List<User> getUsersByIds(String[] ids) throws SQLException {
    Connection connection = persistence.sql.Database.getConnection();
    String query = "SELECT * FROM users WHERE id IN " + constructPlaceholdersList(ids.length);

    PreparedStatement statement = connection.prepareStatement(query);

    for (int i = 0; i < ids.length; i++) {
      statement.setString(i + 1, ids[i]);
    }

    ResultSet resultSet = statement.executeQuery();
    return Helpers.mapResultSetToUsers(resultSet);
  }

  public static User getUserByEmail(String email) throws SQLException {
    Connection connection = persistence.sql.Database.getConnection();
    String query = "SELECT * FROM users WHERE email = ? LIMIT 1";

    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, email);

    ResultSet resultSet = statement.executeQuery();
    List<User> results = Helpers.mapResultSetToUsers(resultSet);

    return results.isEmpty() ? null : results.get(0);
  }

  public static User getUserByUsername(String username) throws SQLException {
    Connection connection = persistence.sql.Database.getConnection();
    String query = "SELECT * FROM users WHERE username = ? LIMIT 1";

    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, username);

    ResultSet resultSet = statement.executeQuery();
    List<User> results = Helpers.mapResultSetToUsers(resultSet);

    return results.isEmpty() ? null : results.get(0);
  }

  public static List<String> getUsersIdsByUsernames(String[] usernames) throws SQLException {
    Connection connection = persistence.sql.Database.getConnection();
    String query = "SELECT id FROM users WHERE username IN " + constructPlaceholdersList(usernames.length);

    PreparedStatement statement = connection.prepareStatement(query);

    for (int i = 0; i < usernames.length; i++) {
      statement.setString(i + 1, usernames[i]);
    }

    ResultSet resultSet = statement.executeQuery();
    return Helpers.mapResultSetToIds(resultSet);
  }

  public static List<User> searchUsersByFullName(
    String fullName,
    int offset,
    int limit
  ) throws SQLException {
    Connection connection = persistence.sql.Database.getConnection();
    String query = "SELECT * FROM users WHERE LOWER(full_name) " +
                   "LIKE '%' || ? || '%' OFFSET ? LIMIT ?";

    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, fullName.toLowerCase());
    statement.setInt(2, offset);
    statement.setInt(3, limit);

    ResultSet resultSet = statement.executeQuery();
    return Helpers.mapResultSetToUsers(resultSet);
  }

  public static User createUser(User user) throws SQLException {
    Connection connection = persistence.sql.Database.getConnection();
    String query = "INSERT INTO users"
                 + " (id, username, email, password_hash, is_private, full_name,"
                 + " bio, gender, phone_number, profile_picture_url, website_url,"
                 + " verified_at, created_at, updated_at, blocked_at, deleted_at)"
                 + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    System.out.println(user.toString());

    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, user.id);
    statement.setString(2, user.username);
    statement.setString(3, user.email);
    statement.setString(4, user.passwordHash);
    statement.setBoolean(5, user.isPrivate);
    statement.setString(6, user.fullName);
    statement.setString(7, user.bio);
    statement.setString(8, user.gender);
    statement.setString(9, user.phoneNumber);
    statement.setString(10, user.profilePictureUrl);
    statement.setString(11, user.websiteUrl);
    statement.setTimestamp(12, toTimestamp(user.verifiedAt));
    statement.setTimestamp(13, toTimestamp(user.createdAt));
    statement.setTimestamp(14, toTimestamp(user.updatedAt));
    statement.setTimestamp(15, toTimestamp(user.blockedAt));
    statement.setTimestamp(16, toTimestamp(user.deletedAt));

    statement.executeUpdate();
    return Database.getUserById(user.id);
  }

  public static User updateUser(User user) throws SQLException {
    /* TODO */
    return Database.getUserById(user.id);
  }
}
