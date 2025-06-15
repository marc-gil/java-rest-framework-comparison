package dev.marcgil.vanilla.db;

import dev.marcgil.vanilla.config.ApplicationConfig;
import dev.marcgil.vanilla.constellation.ConstellationPostgresqlRepository;
import dev.marcgil.vanilla.constellation.ConstellationRepository;
import dev.marcgil.vanilla.security.user.UserPostgresRepository;
import dev.marcgil.vanilla.security.user.UserRepository;

public class PostgresRepositoryFactory implements RepositoryFactory {

  private final DatabaseConnectionHandler databaseConnectionHandler;

  public PostgresRepositoryFactory(ApplicationConfig applicationConfig) {
    DatabaseConnectionFactory databaseConnectionFactory = new DatabaseConnectionFactory(
        applicationConfig);
    this.databaseConnectionHandler = new DatabaseConnectionHandler(databaseConnectionFactory);
  }


  @Override
  public UserRepository createUserRepository() {
    return new UserPostgresRepository(databaseConnectionHandler);
  }

  @Override
  public ConstellationRepository createConstellationRepository() {
    return new ConstellationPostgresqlRepository(databaseConnectionHandler);
  }

}
