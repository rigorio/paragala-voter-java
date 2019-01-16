package rigor.io.paragala.voter.verification;

import org.springframework.stereotype.Service;
import rigor.io.paragala.voter.registration.Registrant;
import rigor.io.paragala.voter.voting.Voter;
import rigor.io.paragala.voter.voting.VoterRepository;

import javax.mail.MessagingException;
import java.util.Base64;
import java.util.Optional;

@Service
public class RegistrantVerifier {

  private VoterRepository voterRepository;

  public RegistrantVerifier(VoterRepository voterRepository) {
    this.voterRepository = voterRepository;
  }

  public boolean verifyRegistrant(Registrant registrant) {
    Optional<Voter> voter = getStudent(registrant);
    return voter.isPresent();
  }

  // TODO todo WHAT?!
  public void sendEmail(Registrant registrant) throws MessagingException {
    String voterCode = generateVoterCode(registrant.getSchool(), registrant.getUniqueId());
    EmailSender sender = new EmailSender();
    sender.sendMail(registrant.getEmail(), voterCode);
  }

  public boolean confirmRegistration(String token) {
    String decodedString = new String(Base64.getDecoder().decode(token));
    String[] data = decodedString.split(":");
    if (data.length!=2)
      return false;
    String school = data[0];
    String uniqueId = data[1];
    Optional<Voter> v = voterRepository.findByUniqueIdAndSchool(uniqueId, school);
    if (!v.isPresent())
      return false;
    Voter voter = v.get();
    String voterCode = generateVoterCode(school, uniqueId);
    voter.allowVoting();
    voter.setVoterCode(voterCode);
    voterRepository.save(voter);
    return true;
  }

  private Optional<Voter> getStudent(Registrant registrant) {
    String school = registrant.getSchool();
    String uniqueId = registrant.getUniqueId();
    return voterRepository.findByUniqueIdAndSchool(uniqueId, school);
  }

  private String generateVoterCode(String school, String uniqueId) {
    return new String(Base64.getEncoder()
                          .withoutPadding()
                          .encode(
                              (school + ":" + uniqueId).getBytes()
                          ));
  }

}
