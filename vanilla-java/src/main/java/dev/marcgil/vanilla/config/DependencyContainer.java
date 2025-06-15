package dev.marcgil.vanilla.config;

import dev.marcgil.vanilla.constellation.ConstellationRepository;
import dev.marcgil.vanilla.constellation.ConstellationService;
import dev.marcgil.vanilla.db.MockRepositoryFactory;
import dev.marcgil.vanilla.db.PostgresRepositoryFactory;
import dev.marcgil.vanilla.db.RepositoryFactory;
import dev.marcgil.vanilla.security.ConfigSecretKeyProvider;
import dev.marcgil.vanilla.security.JwtService;
import dev.marcgil.vanilla.security.SecretKeyProvider;
import dev.marcgil.vanilla.security.user.UserRepository;
import dev.marcgil.vanilla.security.user.UserService;

public class DependencyContainer {

  private final ConstellationRepository constellationRepository;
  private final UserRepository userRepository;
  private final UserService userService;
  private final ConstellationService constellationService;
  private final SecretKeyProvider secretKeyProvider;
  private final JwtService jwtService;

  public DependencyContainer(ApplicationConfig applicationConfig) {
    RepositoryFactory repositoryFactory;
    switch (applicationConfig.getDbAdapter()){
      case MOCK -> repositoryFactory = new MockRepositoryFactory();
      case POSTGRES -> repositoryFactory = new PostgresRepositoryFactory(applicationConfig);
      default -> throw new IllegalStateException(
          "Non defined behaviour db.adapter " + applicationConfig.getDbAdapter());
    }
    this.constellationRepository = repositoryFactory.createConstellationRepository();
    this.userRepository = repositoryFactory.createUserRepository();
    this.userService = new UserService(userRepository);
    this.constellationService = new ConstellationService(constellationRepository);
    this.secretKeyProvider = new ConfigSecretKeyProvider(applicationConfig.getSecret());
    this.jwtService = new JwtService(secretKeyProvider);
  }

  public ConstellationRepository getConstellationRepository() {
    return constellationRepository;
  }

  public UserRepository getUserRepository() {
    return userRepository;
  }

  public UserService getUserService() {
    return userService;
  }

  public ConstellationService getConstellationService() {
    return constellationService;
  }

  public JwtService getJwtService() {
    return jwtService;
  }
}
