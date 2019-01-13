package rigor.io.paragala.voter.voting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.nominees.Nominee;
import rigor.io.paragala.voter.token.TokenService;
import rigor.io.paragala.voter.voting.machine.VoteBoxService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class VoterController {

  private VoterRepository voterRepository;
  private TokenService tokenService;
  private VoteBoxService voteBoxService;

  public VoterController(VoterRepository voterRepository, TokenService tokenService, VoteBoxService voteBoxService) {
    this.voterRepository = voterRepository;
    this.tokenService = tokenService;
    this.voteBoxService = voteBoxService;
  }

  @PostMapping("/voters")
  public ResponseEntity<?> addStudents(@RequestParam String token,
                                       @RequestBody List<Voter> voters) {
    return tokenService.isValid(token)
        ? new ResponseEntity<>(voterRepository.saveAll(voters), HttpStatus.OK)
        : ResponseHub.defaultUnauthorizedResponse();
  }

  @PostMapping("/voting")
  public ResponseEntity<?> vote(@RequestBody Map<String, Object> data) throws IOException {
    String school = String.valueOf(data.get("school"));
    String uniqueId = String.valueOf(data.get("id"));
    String voterCode = String.valueOf(data.get("code"));
    Optional<Voter> idschool = voterRepository.findByUniqueIdAndSchool(uniqueId, school);

    if (!idschool.isPresent())
      return new ResponseEntity<>(new HashMap<String, Object>() {{
        put("status", "Not found");
        put("message", "Wrong school or uniqueId");
      }}, HttpStatus.BAD_REQUEST);

    Voter voter = idschool.get();
    if (!voter.getVoterCode().equals(voterCode))
      return new ResponseEntity<>(new HashMap<String, Object>() {{
        put("status", "Wrong code");
        put("message", "Please use your proper voter code");
      }}, HttpStatus.BAD_REQUEST);

    ObjectMapper mmapper = new ObjectMapper();

    Object votes = data.get("votes");

    String jsonString = mmapper.writeValueAsString(votes);
    List<Nominee> nominees = mmapper.readValue(jsonString, new TypeReference<List<Nominee>>() {});

    voteBoxService.vote(nominees, voter);
    return new ResponseEntity<>(new HashMap<String, Object>() {{
      put("status", "Success");
      put("message", "thx nxt");
    }}, HttpStatus.BAD_REQUEST);
  }

}
