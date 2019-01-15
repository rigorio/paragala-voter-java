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

}
