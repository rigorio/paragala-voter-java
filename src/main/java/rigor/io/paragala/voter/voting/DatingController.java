package rigor.io.paragala.voter.voting;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/date")
public class DatingController {

  private DatingService datingService;

  public DatingController(DatingService datingService) {
    this.datingService = datingService;
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
                                    @RequestBody Map<String, String> data) {

    String startTime = data.get("start");
    String endTime = data.get("end");
    datingService.setStartEnd(startTime, endTime);
    return new ResponseEntity<>("Success", HttpStatus.OK);
  }

}
