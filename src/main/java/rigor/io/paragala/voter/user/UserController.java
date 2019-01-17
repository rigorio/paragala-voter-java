package rigor.io.paragala.voter.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.token.TokenService;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class UserController {

  private TokenService tokenService;
  private UserRepository userRepository;

  public UserController(TokenService tokenService, UserRepository userRepository) {
    this.tokenService = tokenService;
    this.userRepository = userRepository;
    this.userRepository.save(new User("paragala.ph", "p4r4g4l4"));
  }

  @PostMapping("/user")
  public void create(@RequestParam String username, @RequestParam String password) {
    userRepository.save(new User(username, password));
  }

  @DeleteMapping("/users/{user}")
  public ResponseEntity<?> deleteUser(@RequestParam(required = false) String token,
                                      @PathVariable String user) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();
    userRepository.deleteByUsername(user);
    return new ResponseEntity<>("deleted", HttpStatus.ACCEPTED);
  }

  @PostMapping("/users")
  public ResponseEntity<?> create(@RequestParam(required = false) String token,
                                  @RequestBody Map<String, String> data) {

    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    String username = tokenService.fetchUser(token).getUsername();
    String password = data.get("currentPassword");

    Optional<User> user = userRepository.findByUsernameAndPassword(username, password);

    if (!user.isPresent())
      return ResponseHub.defaultUnauthorizedResponse(); // change to "wrong password"
    if (userRepository.findAll().size() > 4)
      return new ResponseEntity<>("2 much", HttpStatus.BAD_REQUEST);

    String newUsername = data.get("username");
    if (userRepository.findByUsername(newUsername).isPresent())
      return new ResponseEntity<>("no wae", HttpStatus.BAD_REQUEST);
    String newPassword = data.get("password");
    return new ResponseEntity<>(userRepository.save(new User(newUsername, newPassword)), HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    if (credentials.get("username") == null && credentials.get("password") == null)
      return new ResponseEntity<>("is no good papi", HttpStatus.BAD_REQUEST);

    String username = credentials.get("username");
    String password = credentials.get("password");
    Optional<User> user = userRepository.findByUsernameAndPassword(username, password);
    return user.isPresent()
        ? new ResponseEntity<>(tokenService.createToken(user.get()), HttpStatus.OK)
        : new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(@RequestParam(required = false) String token) {
    return tokenService.isValid(token)
        ? new ResponseEntity<>(logoutUser(token), HttpStatus.OK)
        : new ResponseEntity<>(" alrady logged out", HttpStatus.NOT_FOUND);
  }

  private String logoutUser(String token) {
    tokenService.delete(token);
    return "is logged out";
  }

}
