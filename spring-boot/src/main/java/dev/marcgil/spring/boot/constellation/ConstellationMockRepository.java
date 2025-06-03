package dev.marcgil.spring.boot.constellation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "db.adapter", havingValue = "mock", matchIfMissing = true)
public class ConstellationMockRepository implements ConstellationRepository {

  private static int nextId = 6;

  public static final Map<Integer, Constellation> CONSTELLATION_MAP = Stream.of(
          new Constellation(1, "Orion", "northern", "Visible in winter, famous for its belt"),
          new Constellation(2, "Southern Cross", "southern",
              "Navigation guide in the southern hemisphere"),
          new Constellation(3, "Cassiopeia", "northern",
              "W- or M-shaped depending on its orientation"),
          new Constellation(4, "Centaurus", "southern",
              "Contains the closest star to the Sun: Proxima Centauri"),
          new Constellation(5, "Scorpius", "southern", "Visible in summer, features a curved tail"))
      .collect(Collectors.toMap(Constellation::getId, Function.identity()));

  @Override
  public List<Constellation> findAll() {
    return CONSTELLATION_MAP.values().stream().toList();
  }

  @Override
  public Optional<Constellation> findById(int constellationId) {
    return Optional.ofNullable(CONSTELLATION_MAP.get(constellationId));
  }

  @Override
  public Constellation save(Constellation constellation) {
    constellation.setId(nextId);
    nextId++;
    CONSTELLATION_MAP.put(constellation.getId(), constellation);
    return constellation;
  }

}
