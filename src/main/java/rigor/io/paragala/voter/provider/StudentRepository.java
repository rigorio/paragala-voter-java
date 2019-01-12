package rigor.io.paragala.voter.provider;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {
  List<Student> findAll();
  List<Student> findAllByUniqueId(String uniqueId);
  List<Student> findAllBySchool(String school);
  Optional<Student> findByUniqueIdAndSchool(String uniqueId, String school);
}
