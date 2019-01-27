package rigor.io.paragala.voter.voting;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.token.TokenService;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/date")
public class DatingController {

  private DatingService datingService;
  private TokenService tokenService;

  public DatingController(DatingService datingService, TokenService tokenService) {
    this.datingService = datingService;
    this.tokenService = tokenService;
  }

  @GetMapping("/start")
  public ResponseEntity<?> getStart(@RequestParam(required = false) String token){
    return new ResponseEntity<>(datingService.getStartDateTime(), HttpStatus.OK);
  }

  @GetMapping("/end")
  public ResponseEntity<?> getEnd(@RequestParam(required = false) String token) {
    return new ResponseEntity<>(datingService.getEndDateTime(), HttpStatus.OK);
  }

  @PostMapping("")
  public ResponseEntity<?> setStart(@RequestParam(required = false) String token,
                                    @RequestBody Map<String, Object> data) {

    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    String startTime = String.valueOf(data.get("start"));
    String endTime = String.valueOf(data.get("end"));
    datingService.setStartEnd(startTime, endTime);
    return new ResponseEntity<>(new HashMap<String, String>(){{
      put("status", "Success");
      put("message", "I'm tired");
    }}, HttpStatus.OK);
  }

}
