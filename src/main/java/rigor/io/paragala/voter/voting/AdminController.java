package rigor.io.paragala.voter.voting;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.nominees.NomineeRepository;
import rigor.io.paragala.voter.token.TokenService;
import rigor.io.paragala.voter.voting.machine.VoteBoxService;
import rigor.io.paragala.voter.voting.machine.VoteFormRepository;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AdminController {

  private VoteBoxService voteBoxService;
  private TokenService tokenService;
  private NomineeRepository nomineeRepository;
  private VoteFormRepository voteFormRepository;

  public AdminController(VoteBoxService voteBoxService, TokenService tokenService, NomineeRepository nomineeRepository, VoteFormRepository voteFormRepository) {
    this.voteBoxService = voteBoxService;
    this.tokenService = tokenService;
    this.nomineeRepository = nomineeRepository;
    this.voteFormRepository = voteFormRepository;
  }

  @GetMapping("/data/results")
  public ResponseEntity<?> viewVotes(@RequestParam(required = false) String token) {
//      boolean isValid = tokenService.isValid(token);
//
//      if (!isValid)
//        return ResponseHub.defaultUnauthorizedResponse();

    return new ResponseEntity<>(voteBoxService.getAllVotes(), HttpStatus.OK);
  }

  @GetMapping("/votes/v2")
  public ResponseEntity<?> viewFullVotes(@RequestParam(required = false) String token) {
    return tokenService.isValid(token)
        ? new ResponseEntity<>(voteBoxService.getFullNomineeTallies(), HttpStatus.OK)
        : ResponseHub.defaultUnauthorizedResponse();
  }

  @GetMapping("/votes/v3")
  public ResponseEntity<?> viewFullVotes2(@RequestParam(required = false) String token) {
    return tokenService.isValid(token)
        ? new ResponseEntity<>(voteFormRepository.findAll(), HttpStatus.OK)
        : ResponseHub.defaultUnauthorizedResponse();
  }
}
