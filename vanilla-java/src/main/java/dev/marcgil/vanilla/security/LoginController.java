package dev.marcgil.vanilla.security;

import com.sun.net.httpserver.HttpExchange;
import dev.marcgil.vanilla.http.RouteHandler;
import dev.marcgil.vanilla.http.RouteHandler.RouteBuilder;
import dev.marcgil.vanilla.http.RoutesHandler;
import dev.marcgil.vanilla.security.user.User;
import dev.marcgil.vanilla.security.user.UserService;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginController implements RoutesHandler {

  private final UserService userService;
  private final JwtService jwtService;

  public LoginController(UserService userService, JwtService jwtService) {
    this.userService = userService;
    this.jwtService = jwtService;
  }

  @Override
  public List<RouteHandler> getHandlers() {
    return RouteBuilder.builder()
        .post("/login", this::login)
        .build();
  }

  public void login(HttpExchange exchange) {
    validateContentTypeIsFormUrlEncoded(exchange);
    try (InputStream requestBody = exchange.getRequestBody()) {
      Map<String, String> formData = parseFormData(new String(requestBody.readAllBytes()));
      User user = userService.getUser(formData.get("username"), formData.get("password"));
      String response = String.format("""
          {
          "access_token": "%s",
          "expires_in": %d,
          "token_type": "%s"
           }""", buildAccessToken(user), 3600, "Bearer");
      sendJsonResponse(response, exchange, 200);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void validateContentTypeIsFormUrlEncoded(HttpExchange exchange) {
    String contentType = exchange.getRequestHeaders().get("Content-Type").stream()
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Missing Content-Type header"));
    if (!contentType.contains("application/x-www-form-urlencoded")) {
      throw new IllegalArgumentException("Content-Type must be application/x-www-form-urlencoded");
    }
  }

  private static Map<String, String> parseFormData(String body) {
    Map<String, String> formData = new HashMap<>();
    String[] pairs = body.split("&");
    for (String pair : pairs) {
      String[] keyValue = pair.split("=", 2);
      String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
      String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8) : "";
      formData.put(key, value);
    }
    return formData;
  }

  private String buildAccessToken(User user) {
    return jwtService.generateAccessToken(user);
  }

}
