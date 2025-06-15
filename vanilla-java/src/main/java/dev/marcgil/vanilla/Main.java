package dev.marcgil.vanilla;

import com.sun.net.httpserver.HttpServer;
import dev.marcgil.vanilla.actuator.ActuatorHandler;
import dev.marcgil.vanilla.config.ApplicationConfig;
import dev.marcgil.vanilla.config.ApplicationConfig.DatabaseAdapter;
import dev.marcgil.vanilla.config.DependencyContainer;
import dev.marcgil.vanilla.constellation.ConstellationHandler;
import dev.marcgil.vanilla.docker.DockerComposeRunner;
import dev.marcgil.vanilla.security.AuthorizationInterceptor;
import dev.marcgil.vanilla.logger.StartupLogger;
import dev.marcgil.vanilla.root.RootHandler;
import dev.marcgil.vanilla.http.Router;
import dev.marcgil.vanilla.security.JwtService;
import dev.marcgil.vanilla.security.LoginController;
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

    if (applicationConfig.getDbAdapter() == DatabaseAdapter.POSTGRES) {
      DockerComposeRunner dockerRunner = new DockerComposeRunner();
      dockerRunner.dockerComposeUp();
    }
    DependencyContainer dependencyContainer = new DependencyContainer(applicationConfig);

    Router router = buildRouter(dependencyContainer);
    server.createContext("/", router);

    System.out.println("Server started on http://localhost:" + serverPort);

    server.start();
    startupLogger.logStartedApplication();
  }

  private static Router buildRouter(DependencyContainer dependencyContainer) {
    RootHandler rootHandler = new RootHandler();
    ActuatorHandler actuatorHandler = new ActuatorHandler();

    ConstellationHandler constellationHandler = new ConstellationHandler(
        dependencyContainer.getConstellationService());

    JwtService jwtService = dependencyContainer.getJwtService();
    LoginController loginController = new LoginController(dependencyContainer.getUserService(),
        jwtService);

    return new Router(
        List.of(new AuthorizationInterceptor(jwtService)),
        constellationHandler, actuatorHandler, loginController, rootHandler);
  }

}
