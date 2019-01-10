package rigor.io.paragala.voter.register;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.token.TokenService;
import rigor.io.paragala.voter.user.Admin;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class RegistrationController {

  private Registrant registrant;
  private Admin admin;
  private TokenService tokenService;

  public RegistrationController(Registrant registrant, TokenService tokenService, Admin admin) {
    this.registrant = registrant;
    this.tokenService = tokenService;
    this.admin = admin;
  }

  @PostMapping("/registration")
  public ResponseEntity<?> enterRegistration(RegistrationForm registrationForm) {
    registrant.register(registrationForm);
    Map<String, String> response = new HashMap<>();
    response.put("status", "Success");
    response.put("message", "Please check your email for confirmation. If there is no email, please wait until your administrator confirms your account.");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/registration")
  public ResponseEntity<?> viewRegistrationForms(@RequestParam String token) {
    return tokenService.isValid(token)
        ? admin.hasPrivileges(tokenService.fetchUser(token), new String[]{"registrations"})
        ? new ResponseEntity<>(admin.viewRegistrants(), HttpStatus.OK)
        : defaultUnauthorizedResponse()
        : defaultUnauthorizedResponse();
  }

  private ResponseEntity<?> defaultUnauthorizedResponse() {
    Map<String, String> unauthorizedResponse = new HashMap<>();
    unauthorizedResponse.put("status", "Unauthorized");
    unauthorizedResponse.put("message", "You do not have the correct privileges to access this feature");
    return new ResponseEntity<>(unauthorizedResponse, HttpStatus.UNAUTHORIZED);
  }

}
