package rigor.io.paragala.voter.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.token.TokenService;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
public class UserController {

  private TokenService tokenService;
  private UserRepository userRepository;

  public UserController(TokenService tokenService, UserRepository userRepository) {
    this.tokenService = tokenService;
    this.userRepository = userRepository;
    User defaultUser = this.userRepository.save(new User("paragala.ph", "p4r4g4l4"));
    System.out.println(defaultUser);
  }

  @PostMapping("/user")
  public void create(@RequestParam String username, @RequestParam String password) {
    userRepository.save(new User(username, password));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    if (credentials.get("username")==null && credentials.get("password")==null)
      return new ResponseEntity<>("is no good papi", HttpStatus.BAD_REQUEST);

    String username = credentials.get("username");
    String password = credentials.get("password");
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

  private String logoutUser(String token) {
    tokenService.delete(token);
    return "is logged out";
  }

}
