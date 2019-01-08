package rigor.io.login.voting;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Admin {

    public Optional<Voter> validateVoter(String uniqueId,
                                  String voterCode,
                                  String school) {
        return Optional.of(
                new Voter(
                        "Rigo Sarmiento",
                        uniqueId,
                        voterCode,
                        school,
                        true
                ));
    }

}
