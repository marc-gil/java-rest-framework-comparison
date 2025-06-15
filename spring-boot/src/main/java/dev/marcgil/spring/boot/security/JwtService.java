package dev.marcgil.spring.boot.security;

import dev.marcgil.spring.boot.security.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final JwtParser jwtParser;
  private final SecretKey secretKey;

  public JwtService(SecretKey secretKey) {
    this.secretKey = secretKey;
    this.jwtParser = Jwts.parser()
        .verifyWith(secretKey)
        .build();
  }

  public Jws<Claims> parseToken(String token) {
    return jwtParser.parseSignedClaims(token);
  }

  public String generateAccessToken(User user) {
    Instant now = Instant.now();
    Instant expiry = now.plus(1, ChronoUnit.HOURS);
    return Jwts.builder()
        .signWith(secretKey)
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiry))
        .subject(user.username())
        .compact();
  }

}
