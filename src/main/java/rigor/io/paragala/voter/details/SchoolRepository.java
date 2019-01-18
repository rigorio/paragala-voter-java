package rigor.io.paragala.voter.details;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface SchoolRepository extends CrudRepository<School, Long> {
  List<School> findAll();
  @Transactional
  void deleteByName(String name);
}
