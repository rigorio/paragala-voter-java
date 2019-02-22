package rigor.io.paragala.voter.voting.password;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;


@Controller
@CrossOrigin
public class PasswordController {

  private PasswordTokenService passwordTokenService;

  public PasswordController(PasswordTokenService passwordTokenService) {
    this.passwordTokenService = passwordTokenService;
  }

  @GetMapping("/password/reset")
  public String changePass(@RequestParam String code) {
    String username;
    try {
      username = new String(Base64.getDecoder().decode(code));
    } catch (IllegalArgumentException e) {
      return "secret";
    }
    System.out.println("imasu ka? " + username);
    if (!passwordTokenService.isValid(username))
      return "secret";


    return "/index.html";
  }

}
