package rigor.io.paragala.voter.voting;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.nominees.NomineeRepository;
import rigor.io.paragala.voter.token.TokenService;
import rigor.io.paragala.voter.voting.machine.VoteBoxService;
import rigor.io.paragala.voter.voting.machine.VoteFormRepository;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/results")
public class ResultsController {

  private VoteBoxService voteBoxService;
  private TokenService tokenService;
  private VoteFormRepository voteFormRepository;

  public ResultsController(VoteBoxService voteBoxService,
                           TokenService tokenService,
                           VoteFormRepository voteFormRepository) {
    this.voteBoxService = voteBoxService;
    this.tokenService = tokenService;
    this.voteFormRepository = voteFormRepository;
  }

  @GetMapping("/tally")
  public ResponseEntity<?> viewVotes(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
        return ResponseHub.defaultUnauthorizedResponse();

    List<Map<String, Object>> votes = voteBoxService.getAllVotes();
    return ResponseHub.defaultFound(votes);
  }

  @Deprecated
  @GetMapping("/votes/v1")
  public ResponseEntity<?> viewFullVotes(@RequestParam(required = false) String token) {
    return tokenService.isValid(token)
        ? new ResponseEntity<>(voteBoxService.getFullNomineeTallies(), HttpStatus.OK)
        : ResponseHub.defaultUnauthorizedResponse();
  }

  @Deprecated
  @GetMapping("/votes/v2")
  public ResponseEntity<?> viewFullVotes2(@RequestParam(required = false) String token) {
    return tokenService.isValid(token)
        ? new ResponseEntity<>(voteFormRepository.findAll(), HttpStatus.OK)
        : ResponseHub.defaultUnauthorizedResponse();
  }
}
