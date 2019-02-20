package rigor.io.paragala.voter.voting;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface VoterCodeRepository extends JpaRepository<VoterCode, Long> {
  Optional<VoterCode> findByCode(String code);
  @Transactional
  void deleteVoterCodeByCode(String code);
}
