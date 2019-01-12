package rigor.io.paragala.voter.voting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.token.TokenService;
import rigor.io.paragala.voter.user.Admin;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
// TODO add privilege and token check
public class VotingController {

  private Admin admin;
  private VoteBox voteBox;
  private TokenService tokenService;
  private NomineeRepository nomineeRepository;

  public VotingController(Admin admin, VoteBox voteBox, TokenService tokenService, NomineeRepository nomineeRepository) {
    this.admin = admin;
    this.voteBox = voteBox;
    this.tokenService = tokenService;
    this.nomineeRepository = nomineeRepository;
  }

  @PostMapping("/vote")
  public ResponseEntity<?> vote(@RequestBody Map<String, Object> vote) throws IOException {
    Optional<Voter> voter = admin.validateVoter(
        String.valueOf(vote.get("id")),
        String.valueOf(vote.get("code")),
        String.valueOf(vote.get("school"))
    );
    System.out.println(vote);
    vote.put("voter", voter);
    return voter.isPresent()
        ? canVote(vote)
        ? registerVote(vote)
        : new ResponseEntity<>(getError("401", "Voter no longer allowed to vote"), HttpStatus.UNAUTHORIZED)
        : new ResponseEntity<>(getError("401", "Voter not found"), HttpStatus.UNAUTHORIZED);
  }

  //will require token
  @GetMapping("/votes")
  public ResponseEntity<?> viewVotes(@RequestParam String token) {
    return tokenService.isValid(token)
        ? new ResponseEntity<>(voteBox.viewVotes(), HttpStatus.OK)
        : ResponseHub.defaultUnauthorizedResponse();
  }

  private Map<String, String> getError(String status, String message) {
    Map<String, String> map = new HashMap<>();
    map.put("status", status);
    map.put("message", message);
    return map;
  }

  private boolean canVote(Map<String, Object> vote) {
    Optional<Voter> voter = (Optional) vote.get("voter");
    boolean b = voter.get().isStatus();
    if (b) {
      System.out.println("hatdog?");
      vote.put("name", voter.get().getName());
      // change voter status to false n
    }
    return b;
  }

  /**
   * if student exists show prompt
   */
  private ResponseEntity<?> registerVote(Map<String, Object> vote) throws IOException {
    System.out.println(vote);
    VoteForm voteForm = new VoteForm(
        String.valueOf(vote.get("name")),
        String.valueOf(vote.get("id")),
        String.valueOf(vote.get("code")),
        String.valueOf(vote.get("school")));
    List<Nominee> nominees = new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(vote.get("votes")),
                                                          new TypeReference<List<Nominee>>() {
                                                          });
    List<Map> votes = (List) vote.get("votes");
    for (Map boto : votes) {
      String category = String.valueOf(boto.get("category"));
      String title = String.valueOf(boto.get("title"));
      String company = String.valueOf(boto.get("company"));
      nominees.add(
          Nominee.builder()
              .category(category)
              .company(company)
              .title(title)
              .votes(getVotes(vote, title, category))
              .build()
      );
    }

    // change everything below this
    nominees.forEach(System.out::println);
    voteBox.vote(voteForm);
    Map<String, String> response = new HashMap<>();
    response.put("status", "200");
    response.put("message", "Vote Confirmed");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  private ArrayList<VoteForm> getVotes(Map<String, Object> vote, String title, String category) {
    List<Nominee> nominees = nomineeRepository.findAll();
    Optional<Nominee> filteredNominees = nominees.stream()
        .filter(nominee -> nominee.getCategory().equals(category) &&
            nominee.getTitle().equals(title))
        .findAny();
    VoteForm voteForm = new VoteForm(
        String.valueOf(vote.get("name")),
        String.valueOf(vote.get("id")),
        String.valueOf(vote.get("code")),
        String.valueOf(vote.get("school")));
    if (filteredNominees.isPresent()) {
      Nominee nominee = filteredNominees.get();
      List<VoteForm> votes = new ArrayList<>(nominee.getVotes());
      votes.add(voteForm);
      nominee.setVotes(votes);
    }
    return new ArrayList<>(Collections.singletonList(voteForm));
  }

}
