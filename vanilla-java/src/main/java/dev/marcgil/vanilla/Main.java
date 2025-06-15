package dev.marcgil.vanilla;

import com.sun.net.httpserver.HttpServer;
import dev.marcgil.vanilla.actuator.ActuatorController;
import dev.marcgil.vanilla.config.ApplicationConfig;
import dev.marcgil.vanilla.constellation.ConstellationMockRepository;
import dev.marcgil.vanilla.constellation.ConstellationPostgresqlRepository;
import dev.marcgil.vanilla.constellation.ConstellationRepository;
import dev.marcgil.vanilla.constellation.ConstellationRestController;
import dev.marcgil.vanilla.constellation.ConstellationService;
import dev.marcgil.vanilla.db.DatabaseConnectionFactory;
import dev.marcgil.vanilla.db.DatabaseConnectionHandler;
import dev.marcgil.vanilla.docker.DockerComposeRunner;
import dev.marcgil.vanilla.security.AuthorizationInterceptor;
import dev.marcgil.vanilla.logger.StartupLogger;
import dev.marcgil.vanilla.root.RootHandler;
import dev.marcgil.vanilla.http.Router;
import dev.marcgil.vanilla.security.ConfigSecretKeyProvider;
import dev.marcgil.vanilla.security.JwtService;
import dev.marcgil.vanilla.security.LoginController;
import dev.marcgil.vanilla.security.SecretKeyProvider;
import dev.marcgil.vanilla.security.user.UserMockRepository;
import dev.marcgil.vanilla.security.user.UserPostgresRepository;
import dev.marcgil.vanilla.security.user.UserRepository;
import dev.marcgil.vanilla.security.user.UserService;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class Main {

  private static final String PROPERTIES_FILE_NAME = "application.properties";

  public static void main(String[] args) throws IOException {
    StartupLogger startupLogger = new StartupLogger();
    ApplicationConfig applicationConfig = new ApplicationConfig(PROPERTIES_FILE_NAME);

    int serverPort = applicationConfig.getServerPort();
    HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

    ConstellationRepository constellationRepository;
    UserRepository userRepository;
    switch (applicationConfig.getDbAdapter()) {
      case MOCK -> {
        constellationRepository = new ConstellationMockRepository();
        userRepository = new UserMockRepository();
      }
      case POSTGRES -> {
        DockerComposeRunner dockerRunner = new DockerComposeRunner();
        dockerRunner.dockerComposeUp();
        DatabaseConnectionFactory databaseConnectionFactory = new DatabaseConnectionFactory(
            applicationConfig);
        DatabaseConnectionHandler databaseConnectionHandler = new DatabaseConnectionHandler(
            databaseConnectionFactory);
        constellationRepository = new ConstellationPostgresqlRepository(
            databaseConnectionHandler);
        userRepository = new UserPostgresRepository(databaseConnectionHandler);
      }
      default -> throw new IllegalStateException(
          "Non defined behaviour db.adapter " + applicationConfig.getDbAdapter());
    }

    Router router = buildRouter(applicationConfig, userRepository,
        constellationRepository);
    server.createContext("/", router);

    System.out.println("Server started on http://localhost:" + serverPort);

    server.start();
    startupLogger.logStartedApplication();
  }

  private static Router buildRouter(ApplicationConfig applicationConfig,
      UserRepository userRepository, ConstellationRepository constellationRepository) {
    RootHandler rootHandler = new RootHandler();
    ActuatorController actuatorController = new ActuatorController();
    ConstellationService constellationService = new ConstellationService(constellationRepository);
    ConstellationRestController constellationRestController = new ConstellationRestController(
        constellationService);
    UserService userService = new UserService(userRepository);
    SecretKeyProvider secretKeyProvider = new ConfigSecretKeyProvider(
        applicationConfig.getSecret());
    JwtService jwtService = new JwtService(secretKeyProvider);
    LoginController loginController = new LoginController(userService, jwtService);

    return new Router(
        List.of(new AuthorizationInterceptor(jwtService)),
        constellationRestController, actuatorController, loginController, rootHandler);
  }

}
