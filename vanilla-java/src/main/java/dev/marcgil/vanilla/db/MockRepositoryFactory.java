package dev.marcgil.vanilla.db;

import dev.marcgil.vanilla.constellation.ConstellationMockRepository;
import dev.marcgil.vanilla.constellation.ConstellationRepository;
import dev.marcgil.vanilla.security.user.UserMockRepository;
import dev.marcgil.vanilla.security.user.UserRepository;

public class MockRepositoryFactory implements RepositoryFactory {

  @Override
  public UserRepository createUserRepository() {
    return new UserMockRepository();
  }

  @Override
  public ConstellationRepository createConstellationRepository() {
    return new ConstellationMockRepository();
  }

}
