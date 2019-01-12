package rigor.io.paragala.voter.register;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Match {
  private Student student;
  private RegistrationForm registrationForm;
}
