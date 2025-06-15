package dev.marcgil.spring.boot.security;

import dev.marcgil.spring.boot.security.user.User;
import dev.marcgil.spring.boot.security.user.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

  private final UserService userService;
  private final JwtService jwtService;

  public LoginController(UserService userService, JwtService jwtService) {
    this.userService = userService;
    this.jwtService = jwtService;
  }

  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<AuthResponse> login(@ModelAttribute LoginRequest request) {
    User user = userService.getUser(request.username(), request.password());
    return ResponseEntity.ok(
        new AuthResponse(jwtService.generateAccessToken(user), 3600, "Bearer"));
  }

  public record AuthResponse(
      String access_token,
      int expires_in,
      String token_type) {

  }

  public record LoginRequest(
      String username,
      String password) {

  }

}
