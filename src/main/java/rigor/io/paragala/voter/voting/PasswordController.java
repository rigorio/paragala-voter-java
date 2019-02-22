package rigor.io.paragala.voter.voting;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class PasswordController {

  @GetMapping("")
  public String a() {
    return "wat";
  }

  @RequestMapping(value = "/wala", method = RequestMethod.GET)
  public String changePass() {
    System.out.println("wat");
    return "/index.html";
  }

}
