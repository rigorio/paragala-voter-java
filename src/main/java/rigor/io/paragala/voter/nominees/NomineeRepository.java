package rigor.io.paragala.voter.nominees;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NomineeRepository extends JpaRepository<Nominee, Long> {
  List<Nominee> findAll();
  void deleteByTitleAndCategory(String title, String category);
}
