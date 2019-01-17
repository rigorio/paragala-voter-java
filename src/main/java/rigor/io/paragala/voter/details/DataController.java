package rigor.io.paragala.voter.details;

import com.google.common.collect.Lists;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.nominees.Nominee;
import rigor.io.paragala.voter.nominees.NomineeRepository;
import rigor.io.paragala.voter.token.TokenService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/data")
@SuppressWarnings("all")
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

  /**
   *
   */
  @GetMapping("/schools")
  public ResponseEntity<?> getSchools(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    List<School> all = schoolRepository.findAll();
    String[] schools = all
        .stream()
        .map(School::getName)
        .collect(Collectors.toList())
        .toArray(new String[all.size()]);
    return ResponseHub.defaultFound(schools);
  }

  /**
   *
   */
  @PostMapping("/schools")
  public ResponseEntity<?> addSchools(@RequestParam(required = false) String token,
                                      @RequestBody String school) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    School savedSchool = schoolRepository.save(new School(school));
    return ResponseHub.defaultCreated(savedSchool);
  }

  /**
   *
   */
  @DeleteMapping("/schools/{school}")
  public ResponseEntity<?> deleteSchool(@RequestParam(required = false) String token,
                                        @PathVariable String school) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    schoolRepository.deleteByName(school);
    return ResponseHub.defaultDeleted();
  }

  /**
   *
   */
  @GetMapping("/categories")
  public ResponseEntity<?> getCategories(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    List<Category> all = categoryRepository.findAll();
    String[] categories = all
        .stream()
        .map(Category::getKategory)
        .collect(Collectors.toList())
        .toArray(new String[all.size()]);
    return ResponseHub.defaultFound(categories);
  }

  /**
   *
   */
  @PostMapping("/categories")
  public ResponseEntity<?> addCategories(@RequestParam(required = false) String token,
                                         @RequestBody String[] categories) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    List<Category> categoryList = Arrays.stream(categories).map(Category::new).collect(Collectors.toList());
    Iterable<Category> iterable = categoryRepository.saveAll(categoryList);
    List<Category> createdCategories = Lists.newArrayList(iterable);
    return ResponseHub.defaultCreated(createdCategories);
  }

  /**
   *
   */
  @DeleteMapping("/categories/{category}")
  public ResponseEntity<?> deleteCategory(@RequestParam(required = false) String token,
                                          @PathVariable String category) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    categoryRepository.deleteByKategory(category);
    return ResponseHub.defaultDeleted();
  }

  /**
   *
   */
  @GetMapping("/nominees")
  public ResponseEntity<?> getNominees(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    List<Nominee> nominees = nomineeRepository.findAll();
    return ResponseHub.defaultFound(nominees);
  }



  /**
   * TODO add filter for existing nominees
   */
  @PostMapping("/nominees")
  public ResponseEntity<?> addNominees(@RequestParam(required = false) String token,
                                       @RequestBody List<Nominee> nominees) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    List<Nominee> createdNominees = nomineeRepository.saveAll(nominees);
    return ResponseHub.defaultCreated(createdNominees);
  }

  /**
   * Http DELETE does not accept body
   * TODO check if id can also be retrieved, delete by id instead
   */
  @PostMapping("/delete/nominees")
  public ResponseEntity<?> deleteNominee(@RequestParam(required = false) String token,
                                         @RequestBody Map<String, String> data) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    String title = data.get("title");
    String category = data.get("category");
    nomineeRepository.deleteByTitleAndCategory(title, category);
    return ResponseHub.defaultDeleted();
  }


  // TODO! DEFAULTS: DO NOT USE !!
  @GetMapping("/defaults/categories")
  public ResponseEntity<?> defaultCategories(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    //    categoryRepository.deleteAll();
    return new ResponseEntity<>(
        new HashSet<>(Arrays.asList(categories)),
        HttpStatus.OK
    );
  }

  @GetMapping("/defaults/nominees")
  public ResponseEntity<?> defaultNominees(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    nomineeRepository.deleteAll();
    return new ResponseEntity<>(nomineeRepository.saveAll(populate()), HttpStatus.OK);
  }

  @GetMapping("/defaults/schools")
  public ResponseEntity<?> defaultSchools(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    return new ResponseEntity<>(new HashSet<String>() {{
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
