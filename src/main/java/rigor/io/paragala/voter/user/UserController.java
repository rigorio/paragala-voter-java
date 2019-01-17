package rigor.io.paragala.voter.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.token.TokenService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {

  private TokenService tokenService;
  private UserRepository userRepository;

  public UserController(TokenService tokenService, UserRepository userRepository) {
    this.tokenService = tokenService;
    this.userRepository = userRepository;
    this.userRepository.save(new User("paragala.ph", "paragala"));
  }

  /**
   *
   */
  @PostMapping("/paragala")
  public ResponseEntity<?> create(@RequestParam(required = false) String username, @RequestParam String password) {
    User user = userRepository.save(new User(username, password));
    return ResponseHub.defaultCreated(user);
  }

  /**
   *
   */
  @PostMapping("/delete/{user}")
  public ResponseEntity<?> deleteUser(@RequestParam(required = false) String token,
                                      @PathVariable String user,
                                      @RequestBody String password) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    User u = tokenService.fetchUser(token);
    Optional<User> attemptingUser = userRepository.findByUsernameAndPassword(u.getUsername(), password);

    if (!attemptingUser.isPresent())
      return ResponseHub.defaultWrongPassword();

    userRepository.deleteByUsername(user);
    return ResponseHub.defaultDeleted();
  }

  /**
   * TODO check below
   */
  @PostMapping("")
  public ResponseEntity<?> create(@RequestParam(required = false) String token,
                                  @RequestBody Map<String, String> data) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    String username = tokenService.fetchUser(token).getUsername();
    String password = data.get("currentPassword");

    Optional<User> user = userRepository.findByUsernameAndPassword(username, password);

    if (!user.isPresent())
      return ResponseHub.defaultWrongPassword();

    if (userRepository.findAll().size() > 4)
      return ResponseHub.defaultNotAllowed("Maximum limit for admins reached. Please delete an admin in order to create a new admin");

    String email = data.get("email"); // TODO parse and send email
    if (userRepository.findByUsername(email).isPresent())
      return ResponseHub.defaultNotAllowed("Admin already exists");

    String newPassword = data.get("password");
    User createdUser = userRepository.save(new User(email, newPassword));
    return ResponseHub.defaultCreated(createdUser);
  }

  /**
   *
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    if (credentials.get("username") == null && credentials.get("password") == null)
      return ResponseHub.defaultBadRequest();

    String username = credentials.get("username");
    String password = credentials.get("password");
    Optional<User> user = userRepository.findByUsernameAndPassword(username, password);
    System.out.println("watashi wa kite" + user.isPresent());
    return user.isPresent()
        ? new ResponseEntity<>(
        new HashMap<String, String>() {{
          put("status", "Logged In");
          put("message", tokenService.createToken(user.get()));
        }}
        , HttpStatus.OK)
        : ResponseHub.defaultBadRequest();
  }

  /**
   *
   */
  @PostMapping("/logout")
  public ResponseEntity<?> logout(@RequestParam(required = false) String token) {
    return tokenService.isValid(token)
        ? new ResponseEntity<>(logoutUser(token), HttpStatus.OK)
        : ResponseHub.defaultBadRequest();
  }

  private ResponseEntity<?> logoutUser(String token) {
    tokenService.delete(token);
    return new ResponseEntity<>(
        new HashMap<String, String>() {{
          put("status", "Ok");
          put("message", "Logged out");
        }}, HttpStatus.OK);
  }

}
