package rigor.io.paragala.voter.details;

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
import rigor.io.paragala.voter.voting.DatingService;
import rigor.io.paragala.voter.voting.ResponseMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
  private DatingService datingService;

  private String[] titles = DataProvider.titles;
  private String[] companies = DataProvider.companies;
  private String[] categories = DataProvider.categories;

  public DataController(TokenService tokenService,
                        SchoolRepository schoolRepository,
                        CategoryRepository categoryRepository,
                        DatingService datingService,
                        NomineeRepository nomineeRepository) {

    this.datingService = datingService;
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

    if (datingService.isAllowed()) {
      String message = "You're not allowed to modify the existing schools during the voting period.";
      return new ResponseEntity<>(new ResponseMessage("Failed", message), HttpStatus.OK);
    }

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

    if (datingService.isAllowed()) {
      String message = "You're not allowed to modify the existing schools during the voting period.";
      return new ResponseEntity<>(new ResponseMessage("Failed", message), HttpStatus.OK);
    }

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

    if (datingService.isAllowed()) {
      String message = "You're not allowed to modify the existing categories during the voting period.";
      return new ResponseEntity<>(new ResponseMessage("Failed", message), HttpStatus.OK);
    }

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

    if (datingService.isAllowed()) {
      String message = "You're not allowed to modify the existing categories during the voting period.";
      return new ResponseEntity<>(new ResponseMessage("Failed", message), HttpStatus.OK);
    }

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

    if (datingService.isAllowed()) {
      String message = "You're not allowed to modify the existing nominees during the voting period.";
      return new ResponseEntity<>(new ResponseMessage("Failed", message), HttpStatus.OK);
    }

    List<Nominee> createdNominees = nomineeRepository.saveAll(nominees);
    createdNominees.forEach(System.out::println);
    return ResponseHub.defaultCreated(nomineeRepository.findAll());
  }

  @PostMapping("/nominees/upload")
  public ResponseEntity<?> uploadNominees(@RequestParam(required = false) String token,
                                          @RequestPart(name = "file") MultipartFile file) throws IOException {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    if (datingService.isAllowed()) {
      String message = "You're not allowed to modify the existing nominees during the voting period.";
      return new ResponseEntity<>(new ResponseMessage("Failed", message), HttpStatus.OK);
    }

    CsvParser csvParser = getCsvParser();

    List<String[]> strings = csvParser.parseAll(file.getInputStream());

    List<Nominee> nominees = new ArrayList<>();
    strings.forEach(string -> {
      nominees.add(new Nominee(string[0], string[1], string[2]));
    });

    List<Nominee> createdNominees = nomineeRepository.saveAll(nominees);
    createdNominees.forEach(System.out::println);
    return ResponseHub.defaultCreated(createdNominees);
  }

  private CsvParser getCsvParser() {
    CsvParserSettings settings = new CsvParserSettings();
    settings.getFormat().setLineSeparator("\n");
    return new CsvParser(settings);
  }

  // TODO remove
  @PostMapping("/schools/upload")
  public ResponseEntity<?> uploadSchools(@RequestParam(required = false) String token,
                                         @RequestPart(name = "file") MultipartFile file) throws IOException {


    CsvParser csvParser = getCsvParser();

    List<String[]> strings = csvParser.parseAll(file.getInputStream());

    List<School> schools = new ArrayList<>();
    strings.forEach(string -> {
      schools.add(new School(string[0]));
    });

    List<School> createdSchools = schoolRepository.saveAll(schools);
    createdSchools.forEach(System.out::println);
    String[] s = createdSchools.stream().map(School::getName).collect(Collectors.toList()).toArray(new String[0]);
    return ResponseHub.defaultCreated(s);
  }

  @PostMapping("/categories/upload")
  public ResponseEntity<?> uploadCategories(@RequestParam(required = false) String token,
                                            @RequestPart(name = "file") MultipartFile file) throws IOException {


    CsvParser csvParser = getCsvParser();

    List<String[]> strings = csvParser.parseAll(file.getInputStream());

    List<Category> categories = new ArrayList<>();
    strings.forEach(string -> {
      categories.add(new Category(string[0]));
    });

    List<Category> createdCategories = categoryRepository.saveAll(categories);
    createdCategories.forEach(System.out::println);
    String[] c = createdCategories
        .stream()
        .map(Category::getKategory)
        .collect(Collectors.toList())
        .toArray(new String[createdCategories.size()]);
    return ResponseHub.defaultCreated(c);
  }

  /**
   *
   */
  @DeleteMapping("/nominees/{id}")
  public ResponseEntity<?> deleteNominee(@RequestParam(required = false) String token,
                                         @PathVariable String id) {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    if (datingService.isAllowed()) {
      String message = "You're not allowed to modify the existing nominees during the voting period.";
      return new ResponseEntity<>(new ResponseMessage("Failed", message), HttpStatus.OK);
    }

    nomineeRepository.deleteById(Long.parseLong(id));
    return ResponseHub.defaultDeleted();
  }

  // TODO! DEFAULTS
  @GetMapping("/defaults/categories")
  public ResponseEntity<?> defaultCategories(@RequestParam(required = false) String token) throws IOException {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    if (datingService.isAllowed()) {
      String message = "You're not allowed to modify the existing categories during the voting period.";
      return new ResponseEntity<>(new ResponseMessage("Failed", message), HttpStatus.OK);
    }

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

    if (datingService.isAllowed()) {
      String message = "You're not allowed to modify the existing nominees during the voting period.";
      return new ResponseEntity<>(new ResponseMessage("Failed", message), HttpStatus.OK);
    }

    nomineeRepository.deleteAll();
    List<Nominee> nominees = nomineeRepository.saveAll(populate());
    return ResponseHub.defaultCreated(nominees);
  }

  @GetMapping("/defaults/nominees/v2")
  public ResponseEntity<?> defaultNominees() throws IOException {

    if (datingService.isAllowed()) {
      String message = "You're not allowed to modify the existing nominees during the voting period.";
      return new ResponseEntity<>(new ResponseMessage("Failed", message), HttpStatus.OK);
    }

    nomineeRepository.deleteAll();
    List<Nominee> nominees = nomineeRepository.saveAll(populate());
    return ResponseHub.defaultCreated(nominees);
  }

  @GetMapping("/defaults/schools")
  public ResponseEntity<?> defaultSchools(@RequestParam(required = false) String token) throws FileNotFoundException {
    if (!tokenService.isValid(token))
      return ResponseHub.defaultUnauthorizedResponse();

    if (datingService.isAllowed()) {
      String message = "You're not allowed to modify the existing nominees during the voting period.";
      return new ResponseEntity<>(new ResponseMessage("Failed", message), HttpStatus.OK);
    }

    List<School> list = new ArrayList<>();
    schoolRepository.deleteAll();

    CsvParser csvParser = getCsvParser();
    File file = new File("src/main/resources/schools.csv");

    List<String[]> strings = csvParser.parseAll(new FileInputStream(file));
    for (String[] string : strings) {
      String name = string[0];
      list.add(new School(name));
    }

    String[] schools = list.stream().map(School::getName).collect(Collectors.toList()).toArray(new String[0]);
    schoolRepository.saveAll(list);

    return ResponseHub.defaultCreated(schools);
  }

  private List<Nominee> populate() throws IOException {
    File file = new File("src/main/resources/results.json");

    List<Nominee> nominees = new ObjectMapper().readValue(file, new TypeReference<List<Nominee>>() {});
    return nominees;
  }

}
