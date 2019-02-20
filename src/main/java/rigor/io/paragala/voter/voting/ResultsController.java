package rigor.io.paragala.voter.voting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.details.Category;
import rigor.io.paragala.voter.details.CategoryRepository;
import rigor.io.paragala.voter.nominees.Nominee;
import rigor.io.paragala.voter.nominees.NomineeRepository;
import rigor.io.paragala.voter.token.TokenService;
import rigor.io.paragala.voter.voting.machine.VoteBoxService;
import rigor.io.paragala.voter.voting.machine.VoteFormRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/results")
@SuppressWarnings("all")
public class ResultsController {

  private VoteBoxService voteBoxService;
  private TokenService tokenService;
  private VoteFormRepository voteFormRepository;
  private NomineeRepository nomineeRepository;
  private CategoryRepository categoryRepository;

  public ResultsController(VoteBoxService voteBoxService,
                           TokenService tokenService,
                           VoteFormRepository voteFormRepository,
                           NomineeRepository nomineeRepository, CategoryRepository categoryRepository) {
    this.voteBoxService = voteBoxService;
    this.tokenService = tokenService;
    this.voteFormRepository = voteFormRepository;
    this.nomineeRepository = nomineeRepository;
    this.categoryRepository = categoryRepository;
  }

  @GetMapping("/v2/tally")
  public ResponseEntity<?> viewTally() {

    List<Map<String, Object>> results = getResult();

    return new ResponseEntity<>(new ResponseMessage("Success", results), HttpStatus.OK);
  }

  @GetMapping("/winners")
  public ResponseEntity<?> viewWinners() {
    List<Map<String, Object>> winners = getWinners();
    return new ResponseEntity<>(new ResponseMessage("Nice", winners), HttpStatus.OK);
  }

  @GetMapping("/winners/sample")
  public ResponseEntity<?> sampleWieners() throws IOException {
    List<Map<String, Object>> results = hatdog().stream()
        .map(nominee -> {
          Map<String, Object> map = new HashMap<>();
          map.put("title", nominee.getTitle());
          map.put("category", nominee.getCategory());
          map.put("company", nominee.getCompany());
          map.put("tally", nominee.getTally());
          return map;
        })
        .collect(Collectors.toList());
    List<Category> c = new ArrayList<>();
    populate().forEach(nominee -> c.add(new Category(nominee.getCategory())));
    List<String> categories = new ArrayList<>(
        new HashSet<>(c.stream()
                          .map(Category::getKategory)
                          .collect(Collectors.toList())));

    List<Map<String, Object>> winners = new ArrayList<>();
    for (String category : categories) {
      List<Map<String, Object>> set = results.stream()
          .filter(result -> result.get("category").equals(category))
          .collect(Collectors.toList());

      List<Map<String, Object>> tallies = set.stream()
          .sorted((o1, o2) -> getTally(o2).compareTo(getTally(o1)))
          .collect(Collectors.toList());

      Map<String, Object> temp = new HashMap<>();

      for (Map<String, Object> tally : tallies) {
        if (temp.get("tally") == null) {
          winners.add(tally);
          temp = new HashMap<>(tally);
        } else if ((getTally(tally) >= getTally(temp))) {
          winners.add(tally);
        }
      }

    }
    return new ResponseEntity<>(new ResponseMessage("Success", winners), HttpStatus.OK);

  }


  private List<Map<String, Object>> getWinners() {
    List<Map<String, Object>> results = getResult();
    List<String> categories = getCategories();

    List<Map<String, Object>> winners = new ArrayList<>();
    for (String category : categories) {
      List<Map<String, Object>> set = results.stream()
          .filter(result -> result.get("category").equals(category))
          .collect(Collectors.toList());

      List<Map<String, Object>> tallies = set.stream()
          .sorted((o1, o2) -> getTally(o2).compareTo(getTally(o1)))
          .collect(Collectors.toList());

      Map<String, Object> temp = new HashMap<>();

      for (Map<String, Object> tally : tallies) {
        if (temp.get("tally") == null) {
          winners.add(tally);
          temp = new HashMap<>(tally);
        } else if ((getTally(tally) >= getTally(temp))) {
          winners.add(tally);
        }
      }

    }
    return winners;
  }

  private List<Map<String, Object>> getResult() {
    return nomineeRepository.findAll().stream()
        .map(nominee -> {
          Map<String, Object> map = new HashMap<>();
          map.put("title", nominee.getTitle());
          map.put("category", nominee.getCategory());
          map.put("company", nominee.getCompany());
          map.put("tally", nominee.getTally());
          return map;
        })
        .collect(Collectors.toList());
  }

  private ArrayList<String> getCategories() {
    return new ArrayList<>(
        new HashSet<>(categoryRepository.findAll()
                          .stream()
                          .map(Category::getKategory)
                          .collect(Collectors.toList())));
  }

  private Long getTally(Map<String, Object> o1) {
    return Long.valueOf(String.valueOf(o1.get("tally")));
  }

  @GetMapping("/tally")
  public ResponseEntity<?> viewVotes(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    List<Map<String, Object>> votes = voteBoxService.getAllVotes();
    return ResponseHub.defaultFound(votes);
  }


  @GetMapping("/school/{school}")
  public ResponseEntity<?> viewBySchool(@RequestParam(required = false) String token,
                                        @PathVariable String school) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    return ResponseHub.defaultFound(voteBoxService.bySchool(school));
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

  private List<Nominee> hatdog() {
    File file = new File("src/main/resources/results.csv");

    CsvParserSettings settings = new CsvParserSettings();
    settings.getFormat().setLineSeparator("\n");

// creates a CSV parser

    BeanListProcessor<Nominee> processor = new BeanListProcessor<>(Nominee.class);
    settings.detectFormatAutomatically();
    settings.setHeaderExtractionEnabled(true);
    settings.setProcessor(processor);
    CsvParser parser = new CsvParser(settings);

    parser.parse(file);
    return processor.getBeans();
  }

  private List<Nominee> populate() throws IOException {
    File file = new File("src/main/resources/results.json");

    List<Nominee> nominees = new ObjectMapper().readValue(file, new TypeReference<List<Nominee>>() {});

    return new ArrayList<>(new HashSet<>(nominees));
  }
}
