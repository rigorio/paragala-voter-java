package rigor.io.paragala.voter.voting;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Voter {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String school;
  private String uniqueId;
  @Nullable
  private String voterCode;
  @Setter(AccessLevel.NONE)
  private boolean eligible;

  public Voter(String school, String uniqueId) {
    this.school = school;
    this.uniqueId = uniqueId;
    this.voterCode = "";
    allowVoting();
  }

  public Voter(String school, String uniqueId, @Nullable String voterCode) {
    this.school = school;
    this.uniqueId = uniqueId;
    this.voterCode = voterCode;
    allowVoting();
  }

  public void allowVoting() {
    eligible = true;
  }

  public void denyVoting() {
    eligible = false;
  }

}
