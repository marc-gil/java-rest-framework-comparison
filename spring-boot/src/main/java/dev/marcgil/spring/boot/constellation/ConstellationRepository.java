package dev.marcgil.spring.boot.constellation;

import java.util.List;
import java.util.Optional;

public interface ConstellationRepository {

  List<Constellation> findAll();

  Optional<Constellation> findById(int constellationId);

  Constellation save(Constellation constellation);

}
