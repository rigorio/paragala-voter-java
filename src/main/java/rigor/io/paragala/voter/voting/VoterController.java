package rigor.io.paragala.voter.voting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.nominees.Nominee;
import rigor.io.paragala.voter.token.TokenService;
import rigor.io.paragala.voter.voting.machine.VoteBoxService;

import java.io.IOException;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api/voters")
public class VoterController {

  private VoterRepository voterRepository;
  private TokenService tokenService;
  private VoteBoxService voteBoxService;

  public VoterController(VoterRepository voterRepository, TokenService tokenService, VoteBoxService voteBoxService) {
    this.voterRepository = voterRepository;
    this.tokenService = tokenService;
    this.voteBoxService = voteBoxService;
  }

  /**
   *
   */
  @GetMapping("")
  public ResponseEntity<?> getVoters(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    List<Voter> voters = voterRepository.findAll();
    return ResponseHub.defaultFound(voters);
  }

  /**
   *
   */
  @PostMapping("")
  public ResponseEntity<?> addStudents(@RequestParam(required = false) String token,
                                       @RequestBody Voter voter) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    Voter savedVoter = voterRepository.save(voter);
    return ResponseHub.defaultCreated(savedVoter);
  }

  /**
   *
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteStudent(@RequestParam(required = false) String token,
                                         @PathVariable Long id) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    voterRepository.deleteById(id);
    return ResponseHub.defaultDeleted();
  }

  /**
   *
   */
  @PostMapping("/vote")
  public ResponseEntity<?> vote(@RequestBody Map<String, Object> data) throws IOException {
    String uniqueId = String.valueOf(data.get("id"));
    String voterCode = String.valueOf(data.get("code"));
    String school = String.valueOf(data.get("school"));
    Optional<Voter> optionalVoter = voterRepository.findByUniqueIdAndSchool(uniqueId, school);

    if (!optionalVoter.isPresent())
      return new ResponseEntity<>(new HashMap<String, Object>() {{
        put("status", "Not found");
        put("message", "Wrong school or uniqueId");
      }}, HttpStatus.OK);

    Voter voter = optionalVoter.get();

    if (!voter.isEligible())
      return new ResponseEntity<>(new HashMap<String, Object>() {{
        put("status", "Not allowed");
        put("message", "You are no longer eligible to vote. Please contact your administrator for concerns.");
      }}, HttpStatus.OK);

    if (!voter.getVoterCode().equals(voterCode))
      return new ResponseEntity<>(new HashMap<String, Object>() {{
        put("status", "Wrong code");
        put("message", "Please use your proper voter code");
      }}, HttpStatus.OK);

    ObjectMapper mapper = new ObjectMapper();

    Object votes = data.get("votes");

    String jsonString = mapper.writeValueAsString(votes);
    List<Nominee> nominees = mapper.readValue(jsonString, new TypeReference<List<Nominee>>() {});

    voteBoxService.vote(nominees, voter);
    return new ResponseEntity<>(new HashMap<String, Object>() {{
      put("status", "Success");
      put("message", "Thank you for voting!");
    }}, HttpStatus.ACCEPTED);
  }


  @Deprecated
  @GetMapping("/defaults/voters")
  public ResponseEntity<?> defaultVoters(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    voterRepository.deleteAll();
    return new ResponseEntity<>(voterRepository.saveAll(
        new ArrayList<>(Arrays.asList(
            new Voter("Holy Angel University", "1312312"),
            new Voter("Angeles University Foundation", "2312312"),
            new Voter("Mabalacat City College", "3312312"),
            new Voter("Tarlac State Univesity", "4312312"),
            new Voter("Benguet State University", "5312312")
        ))
    ), HttpStatus.OK);
  }
}
