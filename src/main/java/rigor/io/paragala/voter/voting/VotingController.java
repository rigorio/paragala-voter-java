package rigor.io.paragala.voter.voting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class VotingController {

    private Admin admin;
    private VoteBox voteBox;

    public VotingController(Admin admin, VoteBox voteBox) {
        this.admin = admin;
        this.voteBox = voteBox;
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

    public Map<String, String> getError(String status, String message) {
        Map<String, String> map = new HashMap<>();
        map.put("status", status);
        map.put("message", message);
        return map;
    }

    private boolean canVote(Map<String, Object> vote) {
        Optional<Voter> voter = (Optional) vote.get("voter");
        boolean b = voter.isPresent();
        if (b) {
            System.out.println("hatdog?");
        vote.put("name", voter.get().getName());
            // change voter status to false n
        }
        return b;
    }

    @GetMapping("")
    public ResponseEntity<?> viewVotes() {
        return new ResponseEntity<>(voteBox.viewVotes(), HttpStatus.OK);
    }

    private ResponseEntity<?> registerVote(Map<String, Object> vote) throws IOException {
        System.out.println(vote);

        VoteForm voteForm = new VoteForm(
                String.valueOf(vote.get("name")),
                String.valueOf(vote.get("id")),
                String.valueOf(vote.get("code")),
                String.valueOf(vote.get("school")));
        List<Nominee> nominees = new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(vote.get("votes")),
                new TypeReference<List<Nominee>>(){});
        nominees.forEach(System.out::println);
        voteForm.setNominees(nominees);
        voteBox.vote(voteForm);
        Map<String, String> response = new HashMap<>();
        response.put("status", "200");
        response.put("message", "Vote Confirmed");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
