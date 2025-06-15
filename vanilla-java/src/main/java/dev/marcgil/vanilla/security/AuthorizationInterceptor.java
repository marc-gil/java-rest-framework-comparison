package dev.marcgil.vanilla.security;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AuthorizationInterceptor implements HttpHandler {

  private static final String BEARER_HEADER_PREFIX = "Bearer ";
  private static final Set<String> UNAUTHORIZED_PATHS = Set.of("/login", "/actuator/health");
  private final JwtService jwtService;

  public AuthorizationInterceptor(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if (isUnauthorizedPath(exchange)) {
      return;
    }
    Optional<String> token = extractToken(exchange);
    if (token.isEmpty()) {
      sendBadRequestResponse(exchange);
      return;
    }
    try {
      Jws<Claims> jwsToken = jwtService.parseToken(token.get());
      SecurityContextHolder.setToken(jwsToken);
    } catch (Exception e) {
      sendBadRequestResponse(exchange);
    }
  }

  private boolean isUnauthorizedPath(HttpExchange exchange) {
    return UNAUTHORIZED_PATHS.contains(exchange.getRequestURI().getPath());
  }

  private Optional<String> extractToken(HttpExchange exchange) {
    List<String> authorizationHeaders = exchange.getRequestHeaders().get("Authorization");
    if (authorizationHeaders == null || authorizationHeaders.size() != 1) {
      return Optional.empty();
    }
    return extractToken(authorizationHeaders.getFirst());
  }

  private Optional<String> extractToken(String authorizationHeader) {
    if (authorizationHeader.toLowerCase().startsWith(BEARER_HEADER_PREFIX.toLowerCase())) {
      return Optional.of(authorizationHeader.substring(BEARER_HEADER_PREFIX.length()).trim());
    }
    return Optional.empty();
  }

  private void sendBadRequestResponse(HttpExchange exchange) throws IOException {
    String response = "Bad Request";
    exchange.sendResponseHeaders(400, response.getBytes().length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(response.getBytes());
    }
    exchange.close();
  }

}
