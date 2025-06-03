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
import dev.marcgil.vanilla.logger.StartupLogger;
import dev.marcgil.vanilla.root.RootHandler;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

  private static final String PROPERTIES_FILE_NAME = "application.properties";

  public static void main(String[] args) throws IOException {
    StartupLogger startupLogger = new StartupLogger();
    ApplicationConfig applicationConfig = new ApplicationConfig(PROPERTIES_FILE_NAME);

    int serverPort = applicationConfig.getServerPort();
    HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

    ConstellationService constellationService = buildConstellationService(applicationConfig);

    server.createContext("/", new RootHandler());
    server.createContext("/constellations", new ConstellationRestController(constellationService));
    server.createContext("/actuator/health", new ActuatorController());

    System.out.println("Server started on http://localhost:" + serverPort);

    server.start();
    startupLogger.logStartedApplication();
  }

  private static ConstellationService buildConstellationService(
      ApplicationConfig applicationConfig) {
    ConstellationRepository constellationRepository;

    switch (applicationConfig.getDbAdapter()) {
      case MOCK -> constellationRepository = new ConstellationMockRepository();
      case POSTGRES -> {
        DockerComposeRunner dockerRunner = new DockerComposeRunner();
        dockerRunner.dockerComposeUp();
        DatabaseConnectionFactory databaseConnectionFactory = new DatabaseConnectionFactory(
            applicationConfig);
        DatabaseConnectionHandler databaseConnectionHandler = new DatabaseConnectionHandler(
            databaseConnectionFactory);
        constellationRepository = new ConstellationPostgresqlRepository(
            databaseConnectionHandler);
      }
      default -> throw new IllegalStateException(
          "Non defined behaviour db.adapter " + applicationConfig.getDbAdapter());
    }

    return new ConstellationService(constellationRepository);
  }

}
