package dev.marcgil.vanilla.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public class SecurityContextHolder {

  private static final ThreadLocal<Jws<Claims>> JWT_HOLDER = new ThreadLocal<>();

  public static void setToken(Jws<Claims> token) {
    JWT_HOLDER.set(token);
  }

  public static Jws<Claims> getToken() {
    return JWT_HOLDER.get();
  }

  public static void clear() {
    JWT_HOLDER.remove();
  }

}
