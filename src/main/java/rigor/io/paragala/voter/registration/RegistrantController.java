package rigor.io.paragala.voter.registration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.verification.RegistrantVerifier;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/account")
public class RegistrantController {

  private RegistrantVerifier registrantVerifier;

  public RegistrantController(RegistrantVerifier registrantVerifier) {
    this.registrantVerifier = registrantVerifier;
  }

  @PostMapping("/registration")
  public ResponseEntity<?> register(@RequestBody Registrant registrant) throws MessagingException {

    if (!registrantVerifier.checkEmail(registrant.getEmail()))
      return ResponseHub.defaultNotAllowed("Not a valid email");

    boolean verified = registrantVerifier.verifyRegistrant(registrant);
    Map<String, String> map = new HashMap<>();
    if (verified) {
      registrantVerifier.sendEmail(registrant);
      map.put("status", "Email Sent");
      map.put("message", "Please confirm your registration through your email");
      return new ResponseEntity<>(map, HttpStatus.OK);
    }
    map.put("status", "Registration Failed");
    map.put("message", "Voter has not been verified by administration");
    return new ResponseEntity<>(map, HttpStatus.OK);
  }




}
