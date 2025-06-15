package dev.marcgil.vanilla.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.marcgil.vanilla.security.SecurityContextHolder;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Router implements HttpHandler {

  private final List<RouteHandler> routeHandlers;
  private final List<HttpHandler> middlewares;

  public Router(RoutesHandler... routesHandler) {
    this(List.of(), routesHandler);
  }

  public Router(List<HttpHandler> middlewares, RoutesHandler... routesHandler) {
    this.routeHandlers = Arrays.stream(routesHandler)
        .map(RoutesHandler::getHandlers)
        .flatMap(Collection::stream)
        .toList();
    this.middlewares = middlewares;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    try {
      for (HttpHandler middleware : middlewares) {
        middleware.handle(exchange);
        if (exchange.getResponseCode() != -1) {
          return;
        }
      }
      String path = exchange.getRequestURI().getPath();
      String method = exchange.getRequestMethod();
      for (RouteHandler routeHandler : routeHandlers) {
        if (routeHandler.matches(path, method)) {
          routeHandler.handle(exchange);
          return;
        }
      }
      exchange.sendResponseHeaders(404, -1);
    } finally {
      SecurityContextHolder.clear();
    }
  }

}
