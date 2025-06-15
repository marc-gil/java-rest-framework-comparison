package dev.marcgil.vanilla.security.user;

import dev.marcgil.vanilla.db.DatabaseConnectionHandler;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserPostgresRepository implements UserRepository {

  private final DatabaseConnectionHandler databaseConnectionHandler;

  public UserPostgresRepository(DatabaseConnectionHandler databaseConnectionHandler) {
    this.databaseConnectionHandler = databaseConnectionHandler;
  }

  @Override
  public Optional<User> findByUsername(String username) {
    String sqlQuery = "SELECT * FROM users WHERE username = ?";
    try (PreparedStatement preparedStatement = databaseConnectionHandler.getConnection()
        .prepareStatement(sqlQuery)) {
      preparedStatement.setString(1, username);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          return Optional.of(
              new User(resultSet.getString("username"), resultSet.getString("password")));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return Optional.empty();
  }
}
