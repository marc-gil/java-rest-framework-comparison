package dev.marcgil.spring.boot.config;

import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class DockerComposeConfig implements EnvironmentPostProcessor {

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment,
      SpringApplication application) {
    String dbAdapter = environment.getProperty("db.adapter", "mock");
    if ("mock".equalsIgnoreCase(dbAdapter)) {
      Map<String, Object> props = Map.of("spring.docker.compose.enabled", false);
      environment.getPropertySources().addFirst(new MapPropertySource("custom-docker-disable", props));
    }
  }

}
