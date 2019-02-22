package rigor.io.paragala.voter.voting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.nominees.Nominee;
import rigor.io.paragala.voter.nominees.NomineeRepository;
import rigor.io.paragala.voter.token.TokenService;
import rigor.io.paragala.voter.voting.machine.VoteBoxService;
import rigor.io.paragala.voter.voting.voterCode.VoterCode;
import rigor.io.paragala.voter.voting.voterCode.VoterCodeRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/voters")
public class VoterController {

  private VoterRepository voterRepository;
  private TokenService tokenService;
  private VoterCodeRepository voterCodeRepository;
  private NomineeRepository nomineeRepository;
  private VoteBoxService voteBoxService;
  private DatingService datingService;

  public VoterController(VoterRepository voterRepository, TokenService tokenService,
                         VoterCodeRepository voterCodeRepository, NomineeRepository nomineeRepository,
                         VoteBoxService voteBoxService, DatingService datingService) {
    this.voterRepository = voterRepository;
    this.tokenService = tokenService;
    this.voterCodeRepository = voterCodeRepository;
    this.nomineeRepository = nomineeRepository;
    this.voteBoxService = voteBoxService;
    this.datingService = datingService;
    this.voterCodeRepository.saveAll(new ArrayList<VoterCode>() {{
      add(new VoterCode("a"));
      add(new VoterCode("b"));
      add(new VoterCode("c"));
    }});
  }

  @GetMapping("")
  public ResponseEntity<?> getVoters(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    List<Voter> voters = voterRepository.findAll();
    return ResponseHub.defaultFound(voters);
  }

  @PostMapping("")
  public ResponseEntity<?> addStudent(@RequestParam(required = false) String token,
                                      @RequestBody Voter voter) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    Voter savedVoter = voterRepository.save(voter);
    return ResponseHub.defaultCreated(savedVoter);
  }

  @PostMapping("/upload")
  @SuppressWarnings("all")
  public ResponseEntity<?> uploadStudents(@RequestParam(required = false) String token,
                                          @RequestParam String school,
                                          @RequestPart(name = "file") MultipartFile file) throws IOException {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    if (file == null)
      return ResponseHub.defaultBadRequest();

    CsvParserSettings settings = new CsvParserSettings();
    settings.getFormat().setLineSeparator("\n");
    CsvParser csvParser = new CsvParser(settings);

    List<String[]> strings = csvParser.parseAll(file.getInputStream());

    List<Voter> voters = new ArrayList<>();
    strings.forEach(string -> {
      String uniqueId = string[0];
      Voter voter = new Voter(school, uniqueId);
      voters.add(voter);
    });
    Iterable<Voter> allVoters = voterRepository.saveAll(voters);
    return ResponseHub.defaultCreated(Lists.newArrayList(allVoters));
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteStudent(@RequestParam(required = false) String token,
                                         @PathVariable Long id) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    voterRepository.deleteById(id);
    return ResponseHub.defaultDeleted();
  }


  @PostMapping("/v2/vote")
  public ResponseEntity<?> vote(@RequestBody Vote vote) {

    if (!datingService.isAllowed())
      return ResponseHub.notAllowedToDate(datingService);

    String code = vote.getVoterCode();
    Optional<VoterCode> byCode = voterCodeRepository.findByCode(code);
    if (!byCode.isPresent()) {
      return new ResponseEntity<>(new ResponseMessage("Not allowed", "The code you entered has either been used or does not exist."), HttpStatus.OK);
    }
    List<Long> votes = vote.getVotes();
    voteBoxService.vote(votes);
    voterCodeRepository.deleteVoterCodeByCode(code);
    return new ResponseEntity<>(new ResponseMessage("Success", "Votes were tallied"), HttpStatus.OK);
  }

  @GetMapping("/v2/nominees")
  public ResponseEntity<?> getNominees() {
    return new ResponseEntity<>(new ResponseMessage("Success", nomineeRepository.findAll()), HttpStatus.OK);
  }

  @GetMapping("/voter-code/all")
  public ResponseEntity<?> viewCodes() {
    List<String> voterCodes = voterCodeRepository.findAll().stream().map(VoterCode::getCode).collect(Collectors.toList());
    return new ResponseEntity<>(new ResponseMessage("Success", voterCodes), HttpStatus.OK);
  }

  @GetMapping("/voter-code/{number}")
  public ResponseEntity<?> generateVoterCodes(@PathVariable int number) {
    RandomStringGenerator rsg = new RandomStringGenerator();
    List<VoterCode> codes = rsg.generateCodes(number).stream().map(VoterCode::new).collect(Collectors.toList());
    List<VoterCode> savedCodes = voterCodeRepository.saveAll(codes);
    List<String> voterCodes = savedCodes.stream().map(VoterCode::getCode).collect(Collectors.toList());
    return new ResponseEntity<>(new ResponseMessage("Success", voterCodes), HttpStatus.OK);
  }

  /**
   * actual voting
   *
   * @param data the request
   * @return
   * @throws IOException
   */
  @PostMapping("/vote")
  public ResponseEntity<?> vote(@RequestBody Map<String, Object> data) throws IOException {

    if (!datingService.isAllowed())
      return ResponseHub.notAllowedToDate(datingService);

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
        put("status", "Vote not sent");
        put("message", "You used the wrong voter code. Please copy the code sent in your email.");
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
