package rigor.io.paragala.voter.voting;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseMessage {
  private String status;
  private Object message;
}
