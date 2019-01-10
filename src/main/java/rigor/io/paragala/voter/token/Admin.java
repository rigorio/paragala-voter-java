package rigor.io.paragala.voter.token;

import org.springframework.stereotype.Service;
import rigor.io.paragala.voter.register.RegistrationForm;
import rigor.io.paragala.voter.register.RegistrationFormRepository;
import rigor.io.paragala.voter.voting.Voter;
import rigor.io.paragala.voter.voting.VoterRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Admin {

  private RegistrationFormRepository registrationFormRepository;

  private VoterRepository voterRepository;

  public Admin(RegistrationFormRepository registrationFormRepository, VoterRepository voterRepository) {
    this.registrationFormRepository = registrationFormRepository;
    this.voterRepository = voterRepository;
  }

  public Optional<Voter> validateVoter(String uniqueId,
                                       String voterCode,
                                       String school) {
    return voterRepository.findAll()
        .stream()
        .parallel()
        .filter(voter -> voter.getUniqueId().equals(uniqueId)
            && voter.getVoterCode().equals(voterCode)
            && voter.getSchool().equals(school))
        .findAny();
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
