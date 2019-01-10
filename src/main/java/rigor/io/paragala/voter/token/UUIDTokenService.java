package rigor.io.paragala.voter.token;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UUIDTokenService implements TokenService {

  private Map<String, Admin> tokens = new HashMap<>();

  @Override
  public String createToken(Admin admin) {
    String token = UUID.randomUUID().toString();
    tokens.put(token, admin);
    return token;
  }

  @Override
  public void delete(String token) {
    tokens.remove(token);
  }

  @Override
  public boolean isValid(String token) {
    return tokens.get(token) != null;
  }

  @Override
  public Admin fetchAdmin(String token) {
    return tokens.get(token);
  }
}
