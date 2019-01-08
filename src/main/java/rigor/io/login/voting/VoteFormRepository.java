package rigor.io.login.voting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteFormRepository extends JpaRepository<VoteForm, Long> {
}
