package rigor.io.paragala.voter.voting.machine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rigor.io.paragala.voter.nominees.Nominee;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteForm {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private Long nomineeId;
  private Long voterId;

  public VoteForm(Long voterId, Long nomineeId) {
    this.nomineeId = nomineeId;
    this.voterId = voterId;
  }
}
