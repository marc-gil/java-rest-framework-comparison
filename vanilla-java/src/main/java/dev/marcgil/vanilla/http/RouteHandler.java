package dev.marcgil.vanilla.http;

import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public record RouteHandler(
    BiPredicate<String, String> routeMatcher,
    Consumer<HttpExchange> handler) {

  boolean matches(String path, String method) {
    return routeMatcher.test(path, method);
  }

  public void handle(HttpExchange exchange) {
    handler.accept(exchange);
  }

  private record RouteMatcher(String path, String method) {

    public boolean matches(String requestPath, String requestMethod) {
      return path.equals(requestPath) && method.equalsIgnoreCase(requestMethod);
    }
  }

  public static class RouteBuilder {

    private final List<RouteHandler> routeHandlers = new ArrayList<>();

    public static RouteBuilder builder() {
      return new RouteBuilder();
    }

    public RouteBuilder get(String path, Consumer<HttpExchange> handler) {
      this.routeHandlers.add(new RouteHandler(new RouteMatcher(path, "GET")::matches, handler));
      return this;
    }

    public RouteBuilder get(BiPredicate<String, String> routeMatcher,
        Consumer<HttpExchange> handler) {
      this.routeHandlers.add(
          new RouteHandler(routeMatcher.and((path, method) -> method.equals("GET")), handler));
      return this;
    }

    public RouteBuilder post(String path, Consumer<HttpExchange> handler) {
      this.routeHandlers.add(new RouteHandler(new RouteMatcher(path, "POST")::matches, handler));
      return this;
    }

    public RouteBuilder post(BiPredicate<String, String> routeMatcher,
        Consumer<HttpExchange> handler) {
      this.routeHandlers.add(
          new RouteHandler(routeMatcher.and((path, method) -> method.equals("POST")), handler));
      return this;
    }

    public RouteBuilder fallback(BiPredicate<String, String> routeMatcher,
        Consumer<HttpExchange> handler) {
      this.routeHandlers.add(new RouteHandler(routeMatcher, handler));
      return this;
    }

    public RouteBuilder fallback(String path, Consumer<HttpExchange> handler) {
      this.routeHandlers.add(new RouteHandler((pth, method) -> pth.equals(path), handler));
      return this;
    }

    public List<RouteHandler> build() {
      return routeHandlers;
    }

  }

}