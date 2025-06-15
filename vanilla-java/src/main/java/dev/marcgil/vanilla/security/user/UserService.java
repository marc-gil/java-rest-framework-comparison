package dev.marcgil.vanilla.security.user;

public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getUser(String username, String password) {
    User user = userRepository.findByUsername(username).orElseThrow();
    if (!user.getPassword().equals(password)) {
      throw new RuntimeException();
    }
    return user;
  }

}
