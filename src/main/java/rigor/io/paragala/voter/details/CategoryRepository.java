package rigor.io.paragala.voter.details;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {
  List<Category> findAll();
  @Transactional
  void deleteByKategory(String kategory);
}
