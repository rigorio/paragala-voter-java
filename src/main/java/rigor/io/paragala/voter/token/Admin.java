package rigor.io.paragala.voter.token;

import rigor.io.paragala.voter.register.RegistrationForm;
import rigor.io.paragala.voter.register.RegistrationFormRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Admin {

  private RegistrationFormRepository registrationFormRepository;

  public Admin(RegistrationFormRepository registrationFormRepository) {
    this.registrationFormRepository = registrationFormRepository;
  }

  List<RegistrationForm> viewRegistrants() {
    return registrationFormRepository.findAll();
  }

  List<RegistrationForm> filterBySchool(String school) {
    return registrationFormRepository.findBySchool(school);
  }

  List<?> matchRegistrants(List<?> registrants) {
    return new ArrayList<>();
  }

  void confirmRegistrant(Long id) {
    Optional<RegistrationForm> r = registrationFormRepository.findById(id);
    if (!r.isPresent())
      throw new RuntimeException("not found");
    RegistrationForm registrationForm = r.get();
    sendEmail(registrationForm);

  }

  private void sendEmail(RegistrationForm registrationForm) {
    String name = registrationForm.getName();
    String email = registrationForm.getEmail();
    String school = registrationForm.getSchool();
    // send email
  }

}
