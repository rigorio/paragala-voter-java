package rigor.io.paragala.voter.register;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.token.TokenService;
import rigor.io.paragala.voter.user.Admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

  @PostMapping("/students")
  public ResponseEntity<?> addStudents(@RequestParam String token,
                                       @RequestBody List<Student> students) {
    return tokenService.isValid(token)
        ? new ResponseEntity<>(admin.addStudents(students), HttpStatus.OK)
        : ResponseHub.defaultUnauthorizedResponse();
  }

  @PostMapping("/registration")
  public ResponseEntity<?> sendRegistration(RegistrationForm registrationForm) {
    registrant.register(registrationForm);
    Map<String, String> response = new HashMap<>();
    response.put("status", "Success");
    response.put("message", "Please check your email for confirmation. If there is no email, please wait until your administrator confirms your account.");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/registrants")
  public ResponseEntity<?> viewRegistrationForms(@RequestParam String token) {
    return tokenService.isValid(token)
        ? admin.hasPrivileges(tokenService.fetchUser(token), new String[]{"registrations"})
        ? new ResponseEntity<>(admin.viewRegistrants(), HttpStatus.OK)
        : ResponseHub.defaultUnauthorizedResponse()
        : ResponseHub.defaultUnauthorizedResponse();
  }

  @PostMapping("/registrants")
  public ResponseEntity<?> confirmRegistrants(@RequestParam String token,
                                              @RequestBody List<Long> ids) {
    return tokenService.isValid(token)
        ? confirmRegistrants(ids)
        : ResponseHub.defaultUnauthorizedResponse();
  }

  @PostMapping("/confirm")
  public ResponseEntity<?> confirmRegistration(@RequestParam String token) {
    Optional<RegistrationForm> registrationForm = admin.getRegistrationForm(token);
    return registrationForm.isPresent()
        ? createVoter(registrationForm.get())
        : ResponseHub.defaultUnauthorizedResponse();
  }

  private ResponseEntity<?> createVoter(RegistrationForm registrationForm) {
    return new ResponseEntity<>(admin.createVoter(registrationForm), HttpStatus.OK);
  }

  private ResponseEntity<?> confirmRegistrants(List<Long> ids) {
    for (Long id : ids) {
      admin.confirmRegistrant(id);
    }


    Map<String, String> response = new HashMap<>();
    response.put("status", "Success");
    response.put("message", "Email sent to registrants");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}
