package dev.marcgil.vanilla.http;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface RoutesHandler {

  List<RouteHandler> getHandlers();

  default void sendJsonResponse(String response, HttpExchange exchange, int statusCode) {
    exchange.getResponseHeaders().set("Content-Type", "application/json");
    sendResponse(response, exchange, statusCode);
  }

  default void sendResponse(String response, HttpExchange exchange, int statusCode) {
    try {
      exchange.sendResponseHeaders(statusCode, response.getBytes().length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(response.getBytes());
      }
      exchange.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}