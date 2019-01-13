package rigor.io.paragala.voter.voting.machine;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NomineeTally {
  private Long nomineeId;
  private Long tally;
}