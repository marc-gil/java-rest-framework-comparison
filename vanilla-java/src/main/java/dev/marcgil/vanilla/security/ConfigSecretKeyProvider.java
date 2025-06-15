package dev.marcgil.vanilla.security;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

public class ConfigSecretKeyProvider implements SecretKeyProvider {

  private final SecretKey secretKey;

  public ConfigSecretKeyProvider(String secret) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public SecretKey getSecretKey() {
    return secretKey;
  }

}
