package rigor.io.paragala.voter.user;

import org.springframework.stereotype.Service;
import rigor.io.paragala.voter.register.RegistrationForm;
import rigor.io.paragala.voter.register.RegistrationFormRepository;
import rigor.io.paragala.voter.voting.Voter;
import rigor.io.paragala.voter.voting.VoterRepository;

import java.util.*;

@Service
public class Admin {

  private RegistrationFormRepository registrationFormRepository;
  private VoterRepository voterRepository;
  private UserRepository userRepository;
  private static final String[] privileges = new String[]{
      "registrants",
      "voters",
      "nominees",
      "admins"
  };

  public boolean hasPrivileges(User user, String[] privileges) {
    for (String privilege : Arrays.asList(privileges)) {
      if (Arrays.asList(Admin.privileges).contains(privilege))
        return true;
    }
    return false;
  }

  public Admin(RegistrationFormRepository registrationFormRepository, VoterRepository voterRepository, UserRepository userRepository) {
    this.registrationFormRepository = registrationFormRepository;
    this.voterRepository = voterRepository;
    this.userRepository = userRepository;
  }

  public User createSuperUser(Map<String, String> user) {
    return createUser(user, privileges);
  }

  public User createUser(Map<String, String> user, String[] privileges) {
    User newUser = User.builder()
        .email(user.get("email"))
        .password(user.get("password").toCharArray())
        .name(user.get("name"))
        .privileges(privileges)
        .build();
    return userRepository.save(newUser);
  }

  public String[] getPrivileges() {
    return privileges;
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


  public List<RegistrationForm> viewRegistrants() {
    return registrationFormRepository.findAll();
  }

  public List<RegistrationForm> filterBySchool(String school) {
    return registrationFormRepository.findBySchool(school);
  }

  public List<?> matchRegistrants(List<?> registrants) {
    return new ArrayList<>();
  }

  public void confirmRegistrant(Long id) {
    Optional<RegistrationForm> r = registrationFormRepository.findById(id);
    if (!r.isPresent())
      throw new RuntimeException("not found");
    RegistrationForm registrationForm = r.get();
    sendEmail(registrationForm);

  }

  public String secretKey() {
    String code = "paragala";
    return Base64.getEncoder().encodeToString(code.getBytes());
  }

  private void sendEmail(RegistrationForm registrationForm) {
    String name = registrationForm.getName();
    String email = registrationForm.getEmail();
    String school = registrationForm.getSchool();
    // send email
  }

}
