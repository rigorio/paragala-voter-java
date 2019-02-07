package rigor.io.paragala.voter.details;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface SchoolRepository extends JpaRepository<School, Long> {
  List<School> findAll();
  @Transactional
  void deleteByName(String name);
}
