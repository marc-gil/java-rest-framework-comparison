package dev.marcgil.vanilla.constellation;

import java.util.List;

public class ConstellationService {

  private final ConstellationRepository constellationRepository;

  public ConstellationService(ConstellationRepository constellationRepository) {
    this.constellationRepository = constellationRepository;
  }

  public List<Constellation> getAllConstellations() {
    return constellationRepository.findAll();
  }

  public Constellation getConstellationById(int constellationId) {
    return constellationRepository.findById(constellationId)
        .orElse(null);
  }

  public Constellation create(Constellation constellation) {
    return constellationRepository.save(constellation);
  }

}
