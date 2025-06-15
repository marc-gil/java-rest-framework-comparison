package dev.marcgil.vanilla.actuator;

import com.sun.net.httpserver.HttpExchange;
import dev.marcgil.vanilla.http.RouteHandler;
import dev.marcgil.vanilla.http.RouteHandler.RouteBuilder;
import dev.marcgil.vanilla.http.RoutesHandler;
import java.io.IOException;
import java.util.List;

public class ActuatorHandler implements RoutesHandler {

  private static final String ACTUATOR_PATH = "/actuator/health";
  private final List<RouteHandler> routeHandlers;

  public ActuatorHandler() {
    this.routeHandlers = RouteBuilder.builder()
        .get(ACTUATOR_PATH, this::handleActuatorUp)
        .fallback(ACTUATOR_PATH, this::notImplementedResponse)
        .build();
  }

  private void handleActuatorUp(HttpExchange exchange) {
    String response = "{\"status\":\"UP\"}";
    sendJsonResponse(response, exchange, 200);
  }

  private void notImplementedResponse(HttpExchange exchange) {
    try {
      exchange.sendResponseHeaders(405, -1);
      exchange.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<RouteHandler> getHandlers() {
    return routeHandlers;
  }

}
