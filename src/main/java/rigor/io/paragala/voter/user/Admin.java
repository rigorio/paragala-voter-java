package rigor.io.paragala.voter.user;

import org.springframework.stereotype.Service;
import rigor.io.paragala.voter.register.*;
import rigor.io.paragala.voter.voting.Voter;
import rigor.io.paragala.voter.voting.VoterRepository;

import java.util.*;

@Service
public class Admin {

  private RegistrationFormRepository registrationFormRepository;
  private VoterRepository voterRepository;
  private UserRepository userRepository;
  private StudentRepository studentRepository;
  private static final String[] privileges = new String[]{
      "registrants",
      "voters",
      "nominees",
      "admins"
  };

  public Admin(RegistrationFormRepository registrationFormRepository, VoterRepository voterRepository, UserRepository userRepository, StudentRepository studentRepository) {
    this.registrationFormRepository = registrationFormRepository;
    this.voterRepository = voterRepository;
    this.userRepository = userRepository;
    this.studentRepository = studentRepository;
  }

  public List<Student> addStudents(List<Student> students) {
    studentRepository.saveAll(students);
    return students;
  }

  public boolean hasPrivileges(User user, String[] privileges) {
    for (String privilege : Arrays.asList(privileges)) {
      if (Arrays.asList(Admin.privileges).contains(privilege))
        return true;
    }
    return false;
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

  public List<Match> viewMatches() {
    List<Match> matches = new ArrayList<>();
    List<RegistrationForm> registrants = registrationFormRepository.findAll();
    List<Student> students = studentRepository.findAll();
    // find a way to optimize search
    for (Student student : students) {
      for (RegistrationForm registrant : registrants) {
        if (student.getUniqueId().equals(registrant.getUniqueId())
            && student.getSchool().equals(registrant.getSchool()))
          matches.add(new Match(student, registrant));
      }
    }
    return matches;
  }

  // turn to list
  public void confirmRegistrant(Long id) {
    Optional<RegistrationForm> r = registrationFormRepository.findById(id);
    if (!r.isPresent())
      throw new RuntimeException("not found");
    RegistrationForm registrationForm = r.get();
    sendEmail(registrationForm);

  }

  public Optional<RegistrationForm> getRegistrationForm(String token) {
    byte[] decodedBytes = Base64.getDecoder().decode(token);
    String decodedString = new String(decodedBytes);
    String[] s = decodedString.split(":");
    Long id = Long.parseLong(s[0]);
    return registrationFormRepository.findById(id);
  }

  public Voter createVoter(RegistrationForm registrationForm) {
    String voterCode = new String(Base64.getEncoder().withoutPadding().encode(
        (registrationForm.getName() + "+" + registrationForm.getUniqueId()).getBytes()
    ));
    Voter voter = Voter.builder()
        .name(registrationForm.getName())
        .school(registrationForm.getSchool())
        .uniqueId(registrationForm.getUniqueId())
        .status(true)
        .voterCode(voterCode)
        .build();
    return voterRepository.save(voter);
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
