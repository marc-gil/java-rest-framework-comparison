package dev.marcgil.spring.boot.security;

import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private static final Set<String> UNAUTHORIZED_PATHS = Set.of("/login", "/actuator/health");
  private final AuthorizationFilter authorizationFilter;

  public SecurityConfig(AuthorizationFilter authorizationFilter) {
    this.authorizationFilter = authorizationFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(UNAUTHORIZED_PATHS.toArray(new String[0])).permitAll()
            .anyRequest().authenticated())
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(authorizationFilter,
            org.springframework.security.web.access.intercept.AuthorizationFilter.class)
        .build();
  }

}
