package dev.marcgil.vanilla.root;

import com.sun.net.httpserver.HttpExchange;
import dev.marcgil.vanilla.http.RouteHandler;
import dev.marcgil.vanilla.http.RoutesHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class RootHandler implements RoutesHandler {

  private final List<RouteHandler> routeHandlers;

  public RootHandler() {
    routeHandlers = List.of(new RouteHandler((path, method) -> true, this::handleRootMessage));
  }

  private void handleRootMessage(HttpExchange exchange) {
    String response = """
        {"message": "I´m a vanilla java server application"}
        """;
    exchange.getResponseHeaders().set("Content-Type", "application/json");
    try {
      exchange.sendResponseHeaders(200, response.getBytes().length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(response.getBytes());
      }
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
