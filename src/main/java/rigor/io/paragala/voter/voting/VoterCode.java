package rigor.io.paragala.voter.voting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VoterCode {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String code;

  public VoterCode(String code) {
    this.code = code;
  }
}
