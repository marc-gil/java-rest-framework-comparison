package dev.marcgil.vanilla.constellation;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.marcgil.vanilla.json.Json;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class ConstellationRestController implements HttpHandler {

  public static final String CONSTELLATIONS = "/constellations";

  private final ConstellationService constellationService;

  public ConstellationRestController(ConstellationService constellationService) {
    this.constellationService = constellationService;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String path = exchange.getRequestURI().getPath();
    String method = exchange.getRequestMethod();
    if (path.equals(CONSTELLATIONS) && method.equals("GET")) {
      handleGetAllConstellations(exchange);
    } else if (path.matches(CONSTELLATIONS + "/\\d+") && method.equals("GET")) {
      handleGetConstellationById(exchange);
    } else if (path.equals(CONSTELLATIONS) && method.equals("POST")) {
      handleCreateConstellation(exchange);
    } else {
      String response = "Not Found";
      sendResponse(response, exchange, 404);
    }
    exchange.close();
  }

  private void handleGetAllConstellations(HttpExchange exchange) throws IOException {
    List<Constellation> constellations = constellationService.getAllConstellations();
    sendJsonResponse(constellations.stream().map(Json::from).collect(
        Collectors.joining(",", "[", "]")), exchange, 200);
  }

  private void handleGetConstellationById(HttpExchange exchange) throws IOException {
    String[] parts = exchange.getRequestURI().getPath().split("/");
    int constellationId = Integer.parseInt(parts[2]);
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

  private void handleCreateConstellation(HttpExchange exchange) throws IOException {
    String requestBody = new String(exchange.getRequestBody().readAllBytes());
    Constellation constellation = Json.to(requestBody, Constellation.class);
    Constellation createdConstellation = constellationService.create(constellation);
    sendJsonResponse(Json.from(createdConstellation), exchange, 201);
  }

  private void sendJsonResponse(String response, HttpExchange exchange, int statusCode)
      throws IOException {
    exchange.getResponseHeaders().set("Content-Type", "application/json");
    sendResponse(response, exchange, statusCode);
  }

  private void sendResponse(String response, HttpExchange exchange, int statusCode)
      throws IOException {
    exchange.sendResponseHeaders(statusCode, response.getBytes().length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(response.getBytes());
    }
  }

}
