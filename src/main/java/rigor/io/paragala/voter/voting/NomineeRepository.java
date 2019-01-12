package rigor.io.paragala.voter.voting;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NomineeRepository extends CrudRepository<Nominee, Long> {
  List<Nominee> findAll();
}
