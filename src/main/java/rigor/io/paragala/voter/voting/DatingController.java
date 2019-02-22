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
  public ResponseEntity<?> getStart() {
    String startDateTime = datingService.getStartDateTime();
    return new ResponseEntity<>(createMap(startDateTime), HttpStatus.OK);
  }

  @GetMapping("/end")
  public ResponseEntity<?> getEnd() {
    String endDateTime = datingService.getEndDateTime();
    return new ResponseEntity<>(createMap(endDateTime), HttpStatus.OK);
  }

  @PostMapping("")
  public ResponseEntity<?> setDate(@RequestParam(required = false) String token,
                                   @RequestBody Map<String, Object> data) {

    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    String startTime = String.valueOf(data.get("start"));
    String endTime = String.valueOf(data.get("end"));


    if (datingService.checkDates(startTime, endTime))
      return new ResponseEntity<>(new ResponseMessage("Failed", "End date cannot be before start date"), HttpStatus.OK);


      datingService.setStartEnd(startTime, endTime);
    return new ResponseEntity<>(new HashMap<String, String>() {{
      put("status", "Success");
      put("message", "I'm tired");
    }}, HttpStatus.OK);
  }

  public HashMap<String, String> createMap(String message) {
    return new HashMap<String, String>() {{
      put("status", "Success");
      put("message", message);
    }};
  }

}
