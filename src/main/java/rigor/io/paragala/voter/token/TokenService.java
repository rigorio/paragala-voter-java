package rigor.io.paragala.voter.token;

import rigor.io.paragala.voter.user.User;

public interface TokenService {

  String createToken(User admin);

  void delete(String token);

  boolean isValid(String token);

  User fetchUser(String token);

}
