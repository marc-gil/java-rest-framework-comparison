package dev.marcgil.spring.boot.security.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "db.adapter", havingValue = "postgres")
public class UserPostgresRepository implements UserRepository {

  private final JdbcTemplate jdbcTemplate;

  public UserPostgresRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Optional<User> findByUsername(String username) {
    String sqlQuery = "SELECT * FROM users WHERE username = ?";
    return Optional.ofNullable(
        jdbcTemplate.queryForObject(sqlQuery, this::mapRowToModel, username));
  }

  private User mapRowToModel(ResultSet resultSet, int row) throws SQLException {
    return new User(resultSet.getString("username"), resultSet.getString("password"));
  }

}
