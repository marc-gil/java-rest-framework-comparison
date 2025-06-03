package dev.marcgil.vanilla.db;

import dev.marcgil.vanilla.config.ApplicationConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionFactory {

  private final ApplicationConfig applicationConfig;
  private final int maxRetryAttempts = 3;
  private final int delayInMs = 500;

  public DatabaseConnectionFactory(ApplicationConfig applicationConfig) {
    this.applicationConfig = applicationConfig;
  }

  public Connection createConnection() {
    for (int attempt = 0; attempt < maxRetryAttempts; attempt++) {
      try {
        return DriverManager.getConnection(applicationConfig.getDbUrl(),
            applicationConfig.getDbUser(), applicationConfig.getDbPassword());
      } catch (SQLException e) {
        attempt++;
        System.out.println(
            "Attempt " + attempt + ": Database not ready. Retrying in " + delayInMs + "ms...");
        try {
          Thread.sleep(delayInMs);
        } catch (InterruptedException ignored) {
        }
      }
    }
    throw new RuntimeException(
        "Could not connect to database after " + maxRetryAttempts + " attempts.");
  }
}
