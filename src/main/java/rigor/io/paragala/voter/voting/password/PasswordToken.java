package rigor.io.paragala.voter.voting.password;

import org.springframework.stereotype.Service;
import rigor.io.paragala.voter.user.User;
import rigor.io.paragala.voter.voting.RandomStringGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PasswordToken {

  private Map<String, User> tokens = new HashMap<>();

  public String createToken(User user) {
    RandomStringGenerator rsg = new RandomStringGenerator();
    String token = rsg.generateCodes(1).get(0);
    tokens.put(token, user);
    return token;
  }


  public void delete(String token) {
    tokens.remove(token);
  }

  public boolean isValid(String token) {
    return tokens.get("" + token) != null; // guarding against null?
  }


  public User fetchUser(String token) {
    return tokens.get(token);
  }

}
