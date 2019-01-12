package rigor.io.paragala.voter.registration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.verification.RegistrantVerifier;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RegistrantController {

  private RegistrantVerifier registrantVerifier;

  public RegistrantController(RegistrantVerifier registrantVerifier) {
    this.registrantVerifier = registrantVerifier;
  }

  @PostMapping("/registration")
  public ResponseEntity<?> register(@RequestBody Registrant registrant) {
    boolean verified = registrantVerifier.verifyRegistrant(registrant);
    Map<String, String> map = new HashMap<>();
    if (verified) {
      registrantVerifier.sendEmail(registrant);
      map.put("status", "Email Sent");
      map.put("message", "Please confirm your registration through your email");
      return new ResponseEntity<>(map, HttpStatus.OK);
    }
    map.put("status", "Registration Failed");
    map.put("message", "Student has not been verified by administration");
    return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
  }

  @PostMapping("/confirmation")
  public ResponseEntity<?> confirmRegistration(@RequestParam String token) {
    registrantVerifier.confirmRegistration(token);
    Map<String, String> map = new HashMap<>();
    map.put("status", "Account successfully created!");
    map.put("message", "Please proceed to vote on the paragala app");
    return new ResponseEntity<>(map, HttpStatus.OK);
  }


}
