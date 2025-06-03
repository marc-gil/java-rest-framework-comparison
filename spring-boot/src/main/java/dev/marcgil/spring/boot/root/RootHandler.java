package dev.marcgil.spring.boot.root;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootHandler {

  @GetMapping(value = "*", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> handleRoot() {
    return ResponseEntity.ok("""
        {"message": I´m a spring boot application"}
        """);
  }
}
