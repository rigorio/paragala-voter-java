package rigor.io.paragala.voter.details;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  List<Category> findAll();
  @Transactional
  void deleteByKategory(String kategory);
}
