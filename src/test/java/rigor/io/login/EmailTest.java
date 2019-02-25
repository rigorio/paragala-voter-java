package rigor.io.login;

import org.junit.Test;
import rigor.io.paragala.voter.verification.EmailSender;

import javax.mail.*;
import javax.mail.internet.*;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.Properties;

public class EmailTest {

  @Test
  public void testMails() throws NamingException {

    String hostName = "what";
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
      if (attr == null)
        throw new NamingException
            ("No match for name '" + hostName + "'");
    }
    System.out.println("hatdog?");

  }


  @Test
  public void testSendEmail() throws MessagingException {
    EmailSender sender = new EmailSender();
    sender.sendMail("rigosarmiento4@gmail.com", "asdf");
  }

}
