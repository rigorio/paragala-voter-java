package rigor.io.login;

import org.junit.Test;
import rigor.io.paragala.voter.verification.EmailSender;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailTest {

  @Test
  public void testSendEmail() throws MessagingException {
    EmailSender sender = new EmailSender();
    sender.sendMail("rigosarmiento4@gmail.com", "asdf");
  }

}
