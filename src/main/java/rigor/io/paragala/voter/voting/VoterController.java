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
@RequestMapping("/api")
public class VoterController {

  private VoterRepository voterRepository;
  private TokenService tokenService;
  private VoteBoxService voteBoxService;

  public VoterController(VoterRepository voterRepository, TokenService tokenService, VoteBoxService voteBoxService) {
    this.voterRepository = voterRepository;
    this.tokenService = tokenService;
    this.voteBoxService = voteBoxService;
  }

  @GetMapping("/voters")
  public ResponseEntity<?> getVoters(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    return new ResponseEntity<>(voterRepository.findAll(), HttpStatus.OK);
  }

  @PostMapping("/voters")
  public ResponseEntity<?> addStudents(@RequestParam(required = false) String token,
                                       @RequestBody List<Voter> voters) {
    return tokenService.isValid(token)
        ? new ResponseEntity<>(voterRepository.saveAll(voters), HttpStatus.OK)
        : ResponseHub.defaultUnauthorizedResponse();
  }

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

  @PostMapping("/vote")
  public ResponseEntity<?> vote(@RequestBody Map<String, Object> data) throws IOException {
    String uniqueId = String.valueOf(data.get("id"));
    String voterCode = String.valueOf(data.get("code"));
    String school = String.valueOf(data.get("school"));
    Optional<Voter> idschool = voterRepository.findByUniqueIdAndSchool(uniqueId, school);

    if (!idschool.isPresent())
      return new ResponseEntity<>(new HashMap<String, Object>() {{
        put("status", "Not found");
        put("message", "Wrong school or uniqueId");
      }}, HttpStatus.BAD_REQUEST);

    Voter voter = idschool.get();

    if (!voter.isEligible())
      return new ResponseEntity<>(new HashMap<String, Object>() {{
        put("status", "Not allowed");
        put("message", "You are no longer eligible to vote. Please contact your administrator for concerns.");
      }}, HttpStatus.BAD_REQUEST);

    if (!voter.getVoterCode().equals(voterCode))
      return new ResponseEntity<>(new HashMap<String, Object>() {{
        put("status", "Wrong code");
        put("message", "Please use your proper voter code");
      }}, HttpStatus.BAD_REQUEST);

    ObjectMapper mapper = new ObjectMapper();

    Object votes = data.get("votes");

    String jsonString = mapper.writeValueAsString(votes);
    List<Nominee> nominees = mapper.readValue(jsonString, new TypeReference<List<Nominee>>() {});

    voteBoxService.vote(nominees, voter);
    return new ResponseEntity<>(new HashMap<String, Object>() {{
      put("status", "Success");
      put("message", "Thank you for voting!");
    }}, HttpStatus.BAD_REQUEST);
  }

}
