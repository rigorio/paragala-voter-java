package rigor.io.paragala.voter.details;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.nominees.Nominee;
import rigor.io.paragala.voter.nominees.NomineeRepository;
import rigor.io.paragala.voter.token.TokenService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
  public ResponseEntity<?> getSchools() {

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
                                      @RequestBody Map<String, String> school) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    School savedSchool = schoolRepository.save(new School(school.get("school")));
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
  public ResponseEntity<?> getCategories() {

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
                                         @RequestBody Map<String, String> category) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    Category savedCategory = categoryRepository.save(new Category(category.get("category")));
    return ResponseHub.defaultCreated(savedCategory);
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
  public ResponseEntity<?> getNominees() {

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
    createdNominees.forEach(System.out::println);
    return ResponseHub.defaultCreated(nomineeRepository.findAll());
  }

  /**
   *
   */
  @DeleteMapping("/nominees/{id}")
  public ResponseEntity<?> deleteNominee(@RequestParam(required = false) String token,
                                         @PathVariable String id) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    nomineeRepository.deleteById(Long.parseLong(id));
    return ResponseHub.defaultDeleted();
  }

  // TODO! DEFAULTS
  @GetMapping("/defaults/categories")
  public ResponseEntity<?> defaultCategories(@RequestParam(required = false) String token) throws IOException {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    List<Category> categories = new ArrayList<>();
    populate().forEach(nominee -> categories.add(new Category(nominee.getCategory())));
    categoryRepository.deleteAll();

    HashSet<Category> categg = new HashSet<>(categories);
    Iterable<Category> categories1 = categoryRepository.saveAll(categg);

    String[] categs = categg
        .stream()
        .map(Category::getKategory)
        .collect(Collectors.toList())
        .toArray(new String[categg.size()]);
    return ResponseHub.defaultCreated(categs);
  }

  /**
   * will empty the repository and provide default data
   *
   * @param token
   * @return
   * @throws IOException
   */
  @GetMapping("/defaults/nominees")
  public ResponseEntity<?> defaultNominees(@RequestParam(required = false) String token) throws IOException {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    nomineeRepository.deleteAll();
    List<Nominee> nominees = nomineeRepository.saveAll(populate());
    return ResponseHub.defaultCreated(nominees);
  }

  @GetMapping("/defaults/schools")
  public ResponseEntity<?> defaultSchools(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    List<School> list = new ArrayList() {{
      add(new School("Holy Angel University"));
      add(new School("Mabalacat City College"));
      add(new School("Tarlac State Univesity"));
    }};
    schoolRepository.deleteAll();
    schoolRepository.saveAll(list);
    String[] schools = list
        .stream()
        .map(School::getName)
        .collect(Collectors.toList())
        .toArray(new String[list.size()]);
    return ResponseHub.defaultCreated(schools);
  }

  private List<Nominee> populate() throws IOException {
    File file = new File("src/main/resources/results.json");

    List<Nominee> nominees = new ObjectMapper().readValue(file, new TypeReference<List<Nominee>>() {});
    return nominees;
  }

}
