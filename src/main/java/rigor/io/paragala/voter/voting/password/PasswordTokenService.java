package rigor.io.paragala.voter.voting.password;

import org.springframework.stereotype.Service;
import rigor.io.paragala.voter.user.User;

import java.util.*;

@Service
public class PasswordTokenService {

  private Set<String> users = new HashSet<>();

  public boolean addUser(String username) {
    return users.add(username);
  }

  public boolean isValid(String username) {
    return users.contains(username);
  }

  public void delete(String username) {
    users.remove(username);
  }

}
