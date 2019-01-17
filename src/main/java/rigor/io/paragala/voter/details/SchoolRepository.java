package rigor.io.paragala.voter.details;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SchoolRepository extends CrudRepository<School, Long> {
  List<School> findAll();
  void deleteByName(String name);
}
