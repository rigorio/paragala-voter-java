package rigor.io.paragala.voter.voting;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Admin {

  private VoterRepository voterRepository;

  public Admin(VoterRepository voterRepository) {
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

}
