package rigor.io.paragala.voter.verification;

import org.springframework.stereotype.Service;
import rigor.io.paragala.voter.registration.Registrant;
import rigor.io.paragala.voter.provider.Student;
import rigor.io.paragala.voter.provider.StudentRepository;

import java.util.Optional;

@Service
public class RegistrantVerifier {

  private StudentRepository studentRepository;

  public RegistrantVerifier(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }

  public boolean verifyRegistrant(Registrant registrant) {
    String school = registrant.getSchool();
    String uniqueId = registrant.getUniqueId();
    Optional<Student> student = studentRepository.findByUniqueIdAndSchool(uniqueId, school);
    return student.isPresent();
  }

  public void sendEmail(Registrant registrant) {

  }

  public void confirmRegistration(String token) {
    // get data from generated base64 token
    // Student student = studentRepository.findByUniqueIdAndSchool(registrant.getUniqueId(), registrant.getSchool()).get();
    // create voter
  }

}
