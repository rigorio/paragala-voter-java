package rigor.io.paragala.voter.voting.machine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FullNomineeTally {
  private String title;
  private String category;
  private String company;
  private Long tally;
}