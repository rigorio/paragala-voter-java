package rigor.io.paragala.voter.details;

import jdk.nashorn.internal.parser.Token;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.nominees.Nominee;
import rigor.io.paragala.voter.nominees.NomineeRepository;
import rigor.io.paragala.voter.token.TokenService;

import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/data")
public class DataController {

  private TokenService tokenService;
  private SchoolRepository schoolRepository;
  private CategoryRepository categoryRepository;
  private NomineeRepository nomineeRepository;

  private String[] titles = DataProvider.titles;
  private String[] companies = DataProvider.companies;
  private String[] categories = DataProvider.categories;

  public DataController(TokenService tokenService,
                        SchoolRepository schoolRepository,
                        CategoryRepository categoryRepository,
                        NomineeRepository nomineeRepository) {
    this.tokenService = tokenService;
    this.schoolRepository = schoolRepository;
    this.categoryRepository = categoryRepository;
    this.nomineeRepository = nomineeRepository;
  }

  @GetMapping("/schools")
  public ResponseEntity<?> getSchools(@RequestParam(required = false) String token) {
//    if (!tokenService.isValid(token))
//      return ResponseHub.defaultUnauthorizedResponse();

    List<School> schools = schoolRepository.findAll();
    return new ResponseEntity<>(
        schools
            .stream()
            .map(School::getSchool)
            .collect(Collectors.toList())
            .toArray(new String[schools.size()]),
        HttpStatus.OK
    );
  }

  @PostMapping("/schools")
  public ResponseEntity<?> addSchools(@RequestParam(required = false) String token,
                                      @RequestBody String[] schools) {
    schoolRepository.deleteAll();
    List<School> schoolList = Arrays.stream(schools).map(School::new).collect(Collectors.toList());
    return new ResponseEntity<>(schoolRepository.saveAll(schoolList), HttpStatus.OK);
  }

  @GetMapping("/categories")
  public ResponseEntity<?> getCategories(@RequestParam(required = false) String token) {

    List<Category> categories = categoryRepository.findAll();
    return new ResponseEntity<>(
        categories
            .stream()
            .map(Category::getCategory)
            .collect(Collectors.toList())
            .toArray(new String[categories.size()]),
        HttpStatus.OK
    );
  }

  @PostMapping("/categories")
  public ResponseEntity<?> addCategories(@RequestParam(required = false) String token,
                                         @RequestBody String[] categories) {
    categoryRepository.deleteAll();
    List<Category> categoryList = Arrays.stream(categories).map(Category::new).collect(Collectors.toList());
    Iterable<Category> extractedBecauseMeNoLikeSeeingYellowLines = categoryRepository.saveAll(categoryList);
    return new ResponseEntity<>(extractedBecauseMeNoLikeSeeingYellowLines, HttpStatus.OK);
  }

  @GetMapping("/nominees")
  public ResponseEntity<?> getNominees(@RequestParam(required = false) String token,
                                       @RequestBody List<String> nominees) {

    return new ResponseEntity<>(nomineeRepository.findAll(), HttpStatus.OK);
  }

  @PostMapping("/nominees")
  public ResponseEntity<?> addNominees(@RequestParam(required = false) String token,
                                       @RequestBody List<Nominee> nominees) {

    nomineeRepository.deleteAll();
    return new ResponseEntity<>(nomineeRepository.saveAll(nominees), HttpStatus.OK);
  }


  // DEFAULTS
  @GetMapping("/defaults/categories")
  public ResponseEntity<?> defaultCategories(@RequestParam(required = false) String token) {

//    categoryRepository.deleteAll();
    return new ResponseEntity<>(
        new HashSet<>(Arrays.asList(categories)),
        HttpStatus.OK
    );
  }

  @GetMapping("/defaults/nominees")
  public ResponseEntity<?> defaultNominees(@RequestParam(required = false) String token) {

    nomineeRepository.deleteAll();
    return new ResponseEntity<>(nomineeRepository.saveAll(populate()), HttpStatus.OK);
  }

  @GetMapping("/defaults/schools")
  public ResponseEntity<?> defaultSchools(@RequestParam(required = false) String token) {

    return new ResponseEntity<>(new HashSet<String>(){{
      add("Holy Angel University");
      add("Angeles University Foundation");
      add("Mabalacat City College");
      add("Tarlac State Univesity");
      add("Benguet State University");
    }}, HttpStatus.OK);
  }

  private List<Nominee> populate() {
    List<Nominee> nominees = new ArrayList<>();
    for (int i = 0; i < titles.length; i++) {
      nominees.add(new Nominee(
          titles[i],
          companies[i],
          categories[i]
      ));
    }
    return nominees;
  }


}
