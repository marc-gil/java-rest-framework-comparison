package dev.marcgil.spring.boot.security.user;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

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

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    return new org.springframework.security.core.userdetails.User(username, user.getPassword(),
        List.of());
  }

}
