package rigor.io.paragala.voter.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rigor.io.paragala.voter.token.TokenService;

import java.util.Optional;

@RestController
public class UserController {

  private TokenService tokenService;
  private UserRepository userRepository;

  public UserController(TokenService tokenService, UserRepository userRepository) {
    this.tokenService = tokenService;
    this.userRepository = userRepository;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(String username, String password) {
    Optional<User> user = userRepository.findByUsernameAndPassword(username, password);
    return user.isPresent()
        ? new ResponseEntity<>(tokenService.createToken(user.get()), HttpStatus.OK)
        : new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(String token) {
    return tokenService.isValid(token)
        ? new ResponseEntity<>(logoutUser(token), HttpStatus.OK)
        : new ResponseEntity<>(" alrady logged out", HttpStatus.NOT_FOUND);
  }

  private ResponseEntity<String> logoutUser(String token) {
    tokenService.delete(token);
    return new ResponseEntity<>("is logged", HttpStatus.OK);
  }

}
