package rigor.io.paragala.voter.user;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
  List<User> findAll();
  Optional<User> findByUsernameAndPassword(String username, String password);
  Optional<User> findByUsername(String username);
  void deleteByUsername(String username);
}
