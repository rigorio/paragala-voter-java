package rigor.io.paragala.voter.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.token.TokenService;
import rigor.io.paragala.voter.verification.EmailSender;
import rigor.io.paragala.voter.voting.ResponseMessage;

import javax.mail.MessagingException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {

  private TokenService tokenService;
  private UserRepository userRepository;

  public UserController(TokenService tokenService, UserRepository userRepository) {
    this.tokenService = tokenService;
    this.userRepository = userRepository;
    User user = new User("paragala.ph", "paragala");
    user.setSuperAdmin(true);
    this.userRepository.save(user);
  }

  @GetMapping("/password/confirm")
  public ResponseEntity<?> confirmPass(@RequestParam String token,
                                       @RequestParam String password) {

    Optional<User> byUsernameAndPassword = userRepository.findByUsernameAndPassword(tokenService.fetchUser(token).getUsername(), password);
    if (!byUsernameAndPassword.isPresent())
      return new ResponseEntity<>(new ResponseMessage("Failed", "Password incorrect"), HttpStatus.OK);

    return new ResponseEntity<>(new ResponseMessage("Success", "Password correct"), HttpStatus.OK);

  }

  /**
   * endpoint to invoke if heroku dyno goes to sleep
   */
  @GetMapping("/awaken")
  public ResponseEntity<?> awaken() {
    return new ResponseEntity<>("Sleeping dyno awakened.", HttpStatus.OK);
  }

  /**
   * Secret method to create as many users as possible
   */
  @PostMapping("/paragala")
  public ResponseEntity<?> create(@RequestParam String username, @RequestParam String password) {
    User user = userRepository.save(new User(username, password));
    return ResponseHub.defaultCreated(user);
  }

  @GetMapping("")
  public ResponseEntity<?> viewAll(@RequestParam(required = false) String token) {

    if (!tokenService.isValid(token) && !tokenService.fetchUser(token).isSuperAdmin())
      return ResponseHub.defaultUnauthorizedResponse();

    User u = tokenService.fetchUser(token);

    List<User> users = userRepository.findAll().stream()
        .filter(user -> !user.getId().equals(u.getId()))
        .collect(Collectors.toList());
    return new ResponseEntity<>(new ResponseMessage("Success", users), HttpStatus.OK);
  }

  @PutMapping("")
  public ResponseEntity<?> editUser(@RequestParam(required = false) String token,
                                    @RequestBody List<User> users) {

    if (!tokenService.isValid(token) && !tokenService.fetchUser(token).isSuperAdmin())
      return ResponseHub.defaultUnauthorizedResponse();

    List<User> savedUsers = userRepository.saveAll(users);
    System.out.println(savedUsers);

    return new ResponseEntity<>(new ResponseMessage("Success", "Details were saved"), HttpStatus.OK);
  }

  /**
   *
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUser(@RequestParam(required = false) String token,
                                      @RequestParam String password, // this is very wrong but idk
                                      @PathVariable Long id) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    User u = tokenService.fetchUser(token);
    System.out.println(u);
    System.out.println("ah");
    System.out.println(password);
    Optional<User> attemptingUser = userRepository.findByUsernameAndPassword(u.getUsername(), password);

    if (!attemptingUser.isPresent())
      return ResponseHub.defaultWrongPassword();

    User user = attemptingUser.get();

    if (!user.isSuperAdmin())
      return new ResponseEntity<>(new ResponseMessage("Failed", "You are not a superAdmin"), HttpStatus.OK);

    userRepository.deleteById(id);

    return new ResponseEntity<>(new ResponseMessage("Success", "User was deleted"), HttpStatus.OK);
  }

  @PostMapping("/password")
  public ResponseEntity<?> changePassword(@RequestParam(required = false) String token,
                                          @RequestBody Map<String, String> data) {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(new ResponseMessage("Failed", "Not Authorized"), HttpStatus.OK);


    String oldPassword = data.get("oldPassword");
    String newPassword = data.get("newPassword");

    User user = tokenService.fetchUser(token);

    Optional<User> u = userRepository.findByUsernameAndPassword(user.getUsername(), oldPassword);

    if (!u.isPresent())
      return new ResponseEntity<>(new ResponseMessage("Failed", "Wrong password"), HttpStatus.OK);

    user.setPassword(newPassword);
    User savedUser = userRepository.save(user);
    System.out.println("Saved user " + savedUser);

    return new ResponseEntity<>(new ResponseMessage("Success", "Password was changed"), HttpStatus.OK);
  }

//  @GetMapping("/ah")
//  public ResponseEntity<?> asdfasdf(@RequestParam String username, @RequestParam String password) {
//
//    userRepository.findByUsernameAndPassword()
//
//  }

  @PostMapping("/password/forgot")
  public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> data) throws MessagingException {

    String email = data.get("email");

    String username = email.split("@")[0];
    Optional<User> u = userRepository.findByUsername(username);

    if (!u.isPresent())
      return new ResponseEntity<>(new ResponseMessage("Failed", "User was not found"), HttpStatus.OK);

    // TODO
    EmailSender emailSender = new EmailSender();


    return new ResponseEntity<>(new ResponseMessage("Email sent!", "Please view your email to change your password"), HttpStatus.OK);
  }


  @PostMapping("")
  public ResponseEntity<?> registration(@RequestParam(required = false) String token,
                                        @RequestBody Map<String, String> data) throws MessagingException {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    String username = tokenService.fetchUser(token).getUsername();
    String password = data.get("currentPassword");

    Optional<User> attemptingUser = userRepository.findByUsernameAndPassword(username, password);

    if (!attemptingUser.isPresent())
      return ResponseHub.defaultWrongPassword();

    User user = attemptingUser.get();

    if (!user.isSuperAdmin())
      return ResponseHub.defaultUnauthorizedResponse();

    if (userRepository.findAll().size() > 4)
      return ResponseHub.defaultNotAllowed("Maximum limit for admins reached. Please delete an admin in order to create a new admin");

    String email = data.get("email");
    String wantedUserName = email.split("@")[0];

    if (userRepository.findByUsername(wantedUserName).isPresent())
      return ResponseHub.defaultNotAllowed("That email is already in use!");

    String wantedPassword = data.get("password");

    if (wantedPassword.contains("@@"))
      return ResponseHub.defaultNotAllowed("Please don't include @@ in your password"); // this is a stupid hack

    EmailSender emailSender = new EmailSender();
    Boolean isSuper = Boolean.valueOf(data.get("superAdmin"));
    emailSender.sendAdminEmail(email, wantedPassword, isSuper);
    Map<String, String> map = new HashMap<>();
    map.put("status", "Email Sent");
    map.put("message", "Please confirm your registration through your email");
    return new ResponseEntity<>(map, HttpStatus.OK);
  }

  @GetMapping("/confirmation")
  public ResponseEntity<?> confirmation(@RequestParam String code) {

    String[] confCode = new String(Base64.getDecoder().decode(code)).split("@@");
    String username = confCode[0];
    String password = confCode[1];
    boolean isSuper = Boolean.parseBoolean(confCode[2]);

    User user = new User(username, password);
    user.setSuperAdmin(isSuper);
    userRepository.save(user);
    Map<String, String> map = new HashMap<>();
    map.put("status", "Success");
    map.put("message", "Account Created");
    return new ResponseEntity<>(map, HttpStatus.OK);
  }

  /**
   *
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    if (credentials.get("username") == null && credentials.get("password") == null)
      return ResponseHub.badLogin();

    String username = credentials.get("username");
    String password = credentials.get("password");
    Optional<User> user = userRepository.findByUsernameAndPassword(username, password);
    System.out.println("Status: " + user.isPresent());
    return user.isPresent()
        ? successfulLogin(user)
        : ResponseHub.badLogin();
  }

  private ResponseEntity<HashMap<String, String>> successfulLogin(Optional<User> user) {
    return new ResponseEntity<>(
        new HashMap<String, String>() {{
          put("status", "Logged In");
          put("message", tokenService.createToken(user.get()));
        }}
        , HttpStatus.OK);
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
