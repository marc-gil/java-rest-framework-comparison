package dev.marcgil.spring.boot.constellation;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/constellations")
public class ConstellationRestController {

  private final ConstellationService constellationService;

  public ConstellationRestController(ConstellationService constellationService) {
    this.constellationService = constellationService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Constellation>> findAll() {
    return ResponseEntity.ok(constellationService.getAllConstellations());
  }

  @GetMapping(value = "/{constellationId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> findById(@PathVariable int constellationId) {
    Constellation constellation = constellationService.getConstellationById(constellationId);
    if (constellation == null) {
      return ResponseEntity.status(404)
          .body("""
              {"message":"Constellation not found"}
              """);
    }
    return ResponseEntity.ok(constellation);
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Constellation> create(@RequestBody Constellation constellation) {
    Constellation createdConstellation = constellationService.create(constellation);
    return ResponseEntity.status(201).body(createdConstellation);
  }

}
