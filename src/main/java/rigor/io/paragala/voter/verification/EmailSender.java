package rigor.io.paragala.voter.verification;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {

  private String host = "https://murmuring-earth-96219.herokuapp.com";

  public void sendMail(String email, String voterCode) throws MessagingException {
    String username = "paragala.ph@gmail.com";
    String password = "p4r4g4l4";
    Properties props = getGmailProperties();
    Session session = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress("paragala.ph@gmail.com"));
    message.setRecipients(
        Message.RecipientType.TO, InternetAddress.parse(email));
    message.setSubject("Paragala voter registration");
    String link = host + "/api/confirmation?code=" + voterCode;
    String msg = "Your voter code is: " + voterCode + ". " +
        "Please click the link to confirm your registration: " + link;

    MimeBodyPart mimeBodyPart = new MimeBodyPart();
    mimeBodyPart.setContent(msg, "text/html");

    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(mimeBodyPart);

    message.setContent(multipart);

    Transport.send(message);

  }

  private Properties getGmailProperties() {
    Properties props = new Properties();
    props.put("mail.smtp.host", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.auth", "true");
    return props;
  }

}
