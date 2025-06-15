package dev.marcgil.vanilla.constellation;

import com.sun.net.httpserver.HttpExchange;
import dev.marcgil.vanilla.http.RouteHandler;
import dev.marcgil.vanilla.http.RouteHandler.RouteBuilder;
import dev.marcgil.vanilla.http.RoutesHandler;
import dev.marcgil.vanilla.json.Json;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ConstellationHandler implements RoutesHandler {

  public static final String CONSTELLATIONS = "/constellations";

  private final List<RouteHandler> routeHandlers;
  private final ConstellationService constellationService;

  public ConstellationHandler(ConstellationService constellationService) {
    this.constellationService = constellationService;
    this.routeHandlers = buildRoutes();
  }

  private List<RouteHandler> buildRoutes() {
    return RouteBuilder.builder()
        .get(CONSTELLATIONS, this::handleGetAllConstellations)
        .get((path, method) -> path.matches(CONSTELLATIONS + "/\\d+"),
            this::handleGetConstellationById)
        .post(CONSTELLATIONS, this::handleCreateConstellation)
        .fallback(CONSTELLATIONS, exchange -> sendResponse("Not Found", exchange, 404))
        .build();
  }

  public void handleGetAllConstellations(HttpExchange exchange) {
    List<Constellation> constellations = constellationService.getAllConstellations();
    sendJsonResponse(constellations.stream().map(Json::from).collect(
        Collectors.joining(",", "[", "]")), exchange, 200);
  }

  private void handleGetConstellationById(HttpExchange exchange) {
    int constellationId = extractConstellationId(exchange);
    Constellation constellation = constellationService.getConstellationById(constellationId);
    if (constellation != null) {
      sendJsonResponse(Json.from(constellation), exchange, 200);
    } else {
      String response = """
          {"message":"Constellation not found"}
          """;
      sendJsonResponse(response, exchange, 404);
    }
  }

  private int extractConstellationId(HttpExchange exchange) {
    String[] parts = exchange.getRequestURI().getPath().split("/");
    return Integer.parseInt(parts[2]);
  }

  private void handleCreateConstellation(HttpExchange exchange) {
    try {
      Constellation constellation = parseRequestBody(exchange);
      Constellation createdConstellation = constellationService.create(constellation);
      sendJsonResponse(Json.from(createdConstellation), exchange, 201);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Constellation parseRequestBody(HttpExchange exchange) throws IOException {
    String requestBody = new String(exchange.getRequestBody().readAllBytes());
    return Json.to(requestBody, Constellation.class);
  }

  @Override
  public List<RouteHandler> getHandlers() {
    return routeHandlers;
  }

}
