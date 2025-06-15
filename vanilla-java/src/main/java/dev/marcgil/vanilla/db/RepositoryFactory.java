package dev.marcgil.vanilla.db;

import dev.marcgil.vanilla.constellation.ConstellationRepository;
import dev.marcgil.vanilla.security.user.UserRepository;

public interface RepositoryFactory {

  UserRepository createUserRepository();

  ConstellationRepository createConstellationRepository();

}