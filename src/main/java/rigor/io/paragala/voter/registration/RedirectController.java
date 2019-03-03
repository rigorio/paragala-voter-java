package rigor.io.paragala.voter.registration;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rigor.io.paragala.voter.user.User;
import rigor.io.paragala.voter.user.UserRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

@Controller
public class RedirectController {

  private UserRepository userRepository;

  public RedirectController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/api/redirect")
  public void redirect(HttpServletResponse response) {
    response.setHeader("Location", "https://paragala-ph.firebaseapp.com/");
    response.setStatus(302);
  }

  @GetMapping("/api/users/confirmation")
  public String confirmation(@RequestParam String code) {

    String[] confCode = new String(Base64.getDecoder().decode(code)).split("@@");
    String username = confCode[0];
    String password = confCode[1];
    boolean isSuper = Boolean.parseBoolean(confCode[2]);

    User user = new User(username, password);
    user.setSuperAdmin(isSuper);
    userRepository.save(user);
    return "redirect:/api/redirect";
  }


}
