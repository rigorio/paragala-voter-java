package rigor.io.paragala.voter.voting;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoterRepository extends CrudRepository<Voter, Long> {
  List<Voter> findAll();
  List<Voter> findAllByUniqueId(String uniqueId);
  List<Voter> findAllBySchool(String school);
  Optional<Voter> findByUniqueIdAndSchool(String uniqueId, String school);
}
