package rigor.io.paragala.voter.registration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrantController {

  @PostMapping("/registration")
  public ResponseEntity<?> register(@RequestBody Registrant registrant) {

    return new ResponseEntity<>("", HttpStatus.OK);
  }



}
