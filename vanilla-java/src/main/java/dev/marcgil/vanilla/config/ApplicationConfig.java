package dev.marcgil.vanilla.config;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

public class ApplicationConfig {

  private final Properties properties;
  private final int serverPort;
  private final String dbUrl;
  private final String dbUser;
  private final String dbPassword;
  private final String secret;
  private final DatabaseAdapter dbAdapter;

  public ApplicationConfig(String propertiesFileName) {
    Properties properties = new Properties();
    try (InputStream input = ApplicationConfig.class.getClassLoader()
        .getResourceAsStream(propertiesFileName)) {
      if (input != null) {
        properties.load(input);
      } else {
        System.err.println(propertiesFileName + " not found in classpath.");
      }
    } catch (Exception e) {
      System.err.println("Failed to load " + propertiesFileName + ": " + e.getMessage());
    }
    this.properties = properties;
    this.serverPort = Optional.ofNullable(properties.getProperty("server.port"))
        .map(Integer::parseInt)
        .orElse(DEFAULT_SERVER_PORT);
    this.dbUrl = properties.getProperty("db.url");
    this.dbUser = properties.getProperty("db.user");
    this.dbPassword = properties.getProperty("db.password");
    this.dbAdapter = Optional.ofNullable(properties.getProperty("db.adapter"))
        .map(this::toDatabaseAdapter)
        .orElse(DatabaseAdapter.MOCK);
    this.secret = properties.getProperty("security.authentication.secret");
  }

  private DatabaseAdapter toDatabaseAdapter(String property) {
    return Arrays.stream(DatabaseAdapter.values())
        .filter(value -> value.name().equalsIgnoreCase(property))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            "The introduced value for db.adapter " + property
                + " is not any of the allowed values: " + Arrays.toString(
                DatabaseAdapter.values())));
  }

  private static final int DEFAULT_SERVER_PORT = 8080;

  public int getServerPort() {
    return serverPort;
  }

  public String getDbUrl() {
    return dbUrl;
  }

  public String getDbPassword() {
    return dbPassword;
  }

  public String getDbUser() {
    return dbUser;
  }

  public String getSecret() {
    return secret;
  }

  public String getProperty(String property) {
    return properties.getProperty(property);
  }

  public DatabaseAdapter getDbAdapter() {
    return dbAdapter;
  }

  public enum DatabaseAdapter {
    MOCK, POSTGRES
  }

}
