package dev.marcgil.vanilla.constellation;

import dev.marcgil.vanilla.db.DatabaseConnectionHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConstellationPostgresqlRepository implements ConstellationRepository {

  private final DatabaseConnectionHandler databaseConnectionHandler;

  public ConstellationPostgresqlRepository(DatabaseConnectionHandler databaseConnectionHandler) {
    this.databaseConnectionHandler = databaseConnectionHandler;
  }

  @Override
  public List<Constellation> findAll() {
    List<Constellation> constellations = new ArrayList<>();
    String sqlQuery = "SELECT * FROM constellations";
    try (PreparedStatement preparedStatement = getConnection().prepareStatement(sqlQuery);
        ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        Constellation constellation = mapRowToModel(resultSet);
        constellations.add(constellation);
      }
      return constellations;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<Constellation> findById(int constellationId) {
    String sqlQuery = "SELECT * FROM constellations WHERE id = " + constellationId;
    try (PreparedStatement preparedStatement = getConnection().prepareStatement(sqlQuery);
        ResultSet resultSet = preparedStatement.executeQuery()) {
      if (resultSet.next()) {
        return Optional.of(mapRowToModel(resultSet));
      }
      return Optional.empty();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Constellation save(Constellation constellation) {
    String sqlQuery = "INSERT INTO constellations (name, hemisphere, description) VALUES (?, ? ,?) RETURNING id, name, hemisphere, description";
    try (PreparedStatement preparedStatement = getConnection().prepareStatement(sqlQuery)) {
      preparedStatement.setString(1, constellation.getName());
      preparedStatement.setString(2, constellation.getHemisphere());
      preparedStatement.setString(3, constellation.getDescription());
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          return mapRowToModel(resultSet);
        } else {
          throw new SQLException("Insert failed, no row returned");
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private Connection getConnection() {
    return databaseConnectionHandler.getConnection();
  }


  private Constellation mapRowToModel(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    String name = resultSet.getString("name");
    String hemisphere = resultSet.getString("hemisphere");
    String description = resultSet.getString("description");
    return new Constellation(id, name, hemisphere, description);
  }

}
