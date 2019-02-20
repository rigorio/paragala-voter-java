package rigor.io.paragala.voter.voting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vote {
  private List<Long> votes;
  private String voterCode;

}
