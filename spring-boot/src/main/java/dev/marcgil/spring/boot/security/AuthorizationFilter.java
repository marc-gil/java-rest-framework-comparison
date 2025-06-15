package dev.marcgil.spring.boot.security;

import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {

  private static final String BEARER_HEADER_PREFIX = "Bearer ";
  private static final Set<String> UNAUTHORIZED_PATHS = Set.of("/login", "/actuator/health");
  private final JwtService jwtService;

  public AuthorizationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return UNAUTHORIZED_PATHS.contains(request.getServletPath());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws IOException {
    String authorization = request.getHeader("Authorization");
    if (authorization == null || !authorization.startsWith(BEARER_HEADER_PREFIX)) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
      return;
    }
    Optional<String> token = extractToken(authorization);
    if (token.isEmpty()) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
      return;
    }
    try {
      Jws<Claims> jwsToken = jwtService.parseToken(token.get());
      UsernamePasswordAuthenticationToken authentication = authenticated(jwsToken, null, List.of());
      SecurityContextHolder.getContext().setAuthentication(authentication);
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
    }
  }

  private Optional<String> extractToken(String authorizationHeader) {
    if (authorizationHeader.toLowerCase().startsWith(BEARER_HEADER_PREFIX.toLowerCase())) {
      return Optional.of(authorizationHeader.substring(BEARER_HEADER_PREFIX.length()).trim());
    }
    return Optional.empty();
  }

}
