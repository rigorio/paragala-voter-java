package rigor.io.paragala.voter.register;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentRepository extends CrudRepository<Student, Long> {
  List<Student> findAll();
}
