package rigor.io.paragala.voter.register;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RegistrationFormRepository extends CrudRepository<RegistrationForm, Long> {
  List<RegistrationForm> findAll();
  List<RegistrationForm> findBySchool(String school);
}
