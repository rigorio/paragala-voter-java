package rigor.io.paragala.voter.voting.password;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.user.User;
import rigor.io.paragala.voter.user.UserRepository;
import rigor.io.paragala.voter.verification.EmailSender;
import rigor.io.paragala.voter.voting.RandomStringGenerator;
import rigor.io.paragala.voter.voting.ResponseMessage;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Base64;
import java.util.Hashtable;
import java.util.Optional;


@Controller
@CrossOrigin
@RequestMapping("/api/password")
public class PasswordController {

  private PasswordTokenService passwordTokenService;
  private UserRepository userRepository;
  private PasswordToken passwordToken;

  public PasswordController(PasswordTokenService passwordTokenService, UserRepository userRepository, PasswordToken passwordToken) {
    this.passwordTokenService = passwordTokenService;
    this.userRepository = userRepository;
    this.passwordToken = passwordToken;
  }

  @GetMapping("")
  @SuppressWarnings("all")
  public ResponseEntity<?> requestChange(@RequestParam String email) throws MessagingException, NamingException {
    String username = email.split("@")[0];
    Optional<User> u = userRepository.findByUsername(username);
    if (!u.isPresent())
      return new ResponseEntity<>(new ResponseMessage("Failed", "User was not found"), HttpStatus.OK);

    if (!checkEmail(email))
      return new ResponseEntity<>(new ResponseMessage("Failed", "Invalid email"), HttpStatus.OK);

    String token = passwordToken.createToken(u.get());

    EmailSender sender = new EmailSender();
    String message = "To reset your password, please use the following code: " + token;
    sender.sendEmail(email, message);

    return new ResponseEntity<>(new ResponseMessage("Success", "Please enter the Reset Code sent to your email"), HttpStatus.OK);
  }

  @GetMapping("/reset")
  public ResponseEntity<?> changePass(@RequestParam String password,
                                      @RequestParam String code) {

    if (!passwordToken.isValid(code))
      return new ResponseEntity<>(new ResponseMessage("Failed", "Invalid code"), HttpStatus.OK);

    User user = passwordToken.fetchUser(code);
    user.setPassword(password);
    userRepository.save(user);

    passwordToken.delete(code);

    return new ResponseEntity<>(new ResponseMessage("Success", "Password was changed"), HttpStatus.OK);
  }

  private boolean checkEmail(String email) throws NamingException {
    String[] strings = email.split("@");
    String hostName = "";
    if (strings.length > 1)
      hostName = strings[0];
    Hashtable env = new Hashtable();
    env.put("java.naming.factory.initial",
            "com.sun.jndi.dns.DnsContextFactory");
    DirContext ictx = new InitialDirContext(env);
    Attributes attrs = ictx.getAttributes
        (hostName, new String[]{"MX"});
    Attribute attr = attrs.get("MX");
    if ((attr == null) || (attr.size() == 0)) {
      attrs = ictx.getAttributes(hostName, new String[]{"A"});
      attr = attrs.get("A");
      return attr != null;
    }
    return true;
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
