package dev.marcgil.spring.boot.constellation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "db.adapter", havingValue = "postgres")
public class ConstellationPostgresRepository implements ConstellationRepository {

  private final JdbcTemplate jdbcTemplate;

  public ConstellationPostgresRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Constellation> findAll() {
    String sqlQuery = "SELECT * FROM constellations";
    return jdbcTemplate.query(sqlQuery, this::mapRowToModel);
  }

  @Override
  public Optional<Constellation> findById(int constellationId) {
    String sqlQuery = "SELECT * FROM constellations WHERE id = ?";
    return Optional.ofNullable(
        jdbcTemplate.queryForObject(sqlQuery, this::mapRowToModel, constellationId));
  }

  @Override
  public Constellation save(Constellation constellation) {
    String sqlQuery = "INSERT INTO constellations (name, hemisphere, description) VALUES (?, ?, ?) RETURNING id, name, hemisphere, description";
    return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToModel, constellation.getName(),
        constellation.getHemisphere(), constellation.getDescription());
  }

  private Constellation mapRowToModel(ResultSet resultSet, int rowNumber)
      throws SQLException {
    int id = resultSet.getInt("id");
    String name = resultSet.getString("name");
    String hemisphere = resultSet.getString("hemisphere");
    String description = resultSet.getString("description");
    return new Constellation(id, name, hemisphere, description);
  }

}
