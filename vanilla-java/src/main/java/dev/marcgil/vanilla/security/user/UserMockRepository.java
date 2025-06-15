package dev.marcgil.vanilla.security.user;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserMockRepository implements UserRepository {

  private static final Map<String, User> USERS = Stream.of(new User("test-user", "test-password"))
      .collect(Collectors.toMap(User::username, Function.identity()));

  @Override
  public Optional<User> findByUsername(String username) {
    return Optional.ofNullable(USERS.get(username));
  }

}
