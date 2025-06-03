package dev.marcgil.vanilla.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionHandler {

  private Connection connection;
  private final DatabaseConnectionFactory databaseConnectionFactory;

  public DatabaseConnectionHandler(DatabaseConnectionFactory databaseConnectionFactory) {
    this.databaseConnectionFactory = databaseConnectionFactory;
    this.connection = databaseConnectionFactory.createConnection();
  }

  public Connection getConnection() {
    try {
      if (!connection.isValid(2)) {
        this.connection = databaseConnectionFactory.createConnection();
      }
      return connection;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
