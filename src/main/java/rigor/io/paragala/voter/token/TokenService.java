package rigor.io.paragala.voter.token;

public interface TokenService {

  String createToken(Admin admin);

  void delete(String token);

  boolean isValid(String token);

  Admin fetchAdmin(String token);

}
