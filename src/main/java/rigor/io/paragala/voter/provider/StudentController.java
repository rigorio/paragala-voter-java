package rigor.io.paragala.voter.provider;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.token.TokenService;

import java.util.List;

@RestController
public class StudentController {

  private StudentRepository studentRepository;
  private TokenService tokenService;

  public StudentController(StudentRepository studentRepository, TokenService tokenService) {
    this.studentRepository = studentRepository;
    this.tokenService = tokenService;
  }

  @PostMapping("/students")
  public ResponseEntity<?> addStudents(@RequestParam String token,
                                       @RequestBody List<Student> students) {
    return tokenService.isValid(token)
        ? new ResponseEntity<>(studentRepository.saveAll(students), HttpStatus.OK)
        : ResponseHub.defaultUnauthorizedResponse();
  }


}
