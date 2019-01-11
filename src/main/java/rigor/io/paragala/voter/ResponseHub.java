package rigor.io.paragala.voter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHub {
  private static Map<String, String> response = new HashMap<>();
  private static ResponseEntity<?> responseEntity;
  static {
    response.put("status", "Unauthorized");
    response.put("message", "You do not have the correct privileges to access this feature");
    responseEntity = new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

  public static ResponseEntity<?> defaultUnauthorizedResponse() {
    return responseEntity;
  }
}
