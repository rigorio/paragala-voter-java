package rigor.io.paragala.voter.registration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Registrant {
  private String school;
  private String uniqueId;
  private String email;
}
