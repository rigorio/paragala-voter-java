package rigor.io.paragala.voter.token;

import org.springframework.stereotype.Service;
import rigor.io.paragala.voter.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UUIDTokenService implements TokenService {

  private Map<String, User> tokens = new HashMap<>();

  @Override
  public String createToken(User user) {
    String token = UUID.randomUUID().toString();
    tokens.put(token, user);
    return token;
  }

  @Override
  public void delete(String token) {
    tokens.remove(token);
  }

  @Override
  public boolean isValid(String token) {
    return tokens.get("" + token) != null; // guarding against null?
  }

  @Override
  public User fetchUser(String token) {
    return tokens.get(token);
  }
}
