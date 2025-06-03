package dev.marcgil.vanilla.actuator;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

public class ActuatorController implements HttpHandler {

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if ("GET".equals(exchange.getRequestMethod())) {
      String response = "{\"status\":\"UP\"}";
      exchange.sendResponseHeaders(200, response.getBytes().length);
      exchange.getResponseHeaders().set("Content-Type", "application/json");
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(response.getBytes());
      }
    } else {
      exchange.sendResponseHeaders(405, -1);
    }
    exchange.close();
  }

}
