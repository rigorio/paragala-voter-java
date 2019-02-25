package rigor.io.paragala.voter.verification;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Base64;
import java.util.Properties;

@SuppressWarnings("all")
public class EmailSender {

  private String host = "https://shrouded-caverns-92003.herokuapp.com";

  private String username = "paragala.ph@gmail.com";
  private String password = "p4r4g4l4";
  private Properties props;
  private Session session;

  private Message message;

  public EmailSender() throws MessagingException {
    props = getGmailProperties();
    session = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });
    message = new MimeMessage(session);
    message.setFrom(new InternetAddress("paragala.ph@gmail.com"));
  }

  public void sendNormalMail(String email) throws MessagingException {
    message.setRecipients(
        Message.RecipientType.TO, InternetAddress.parse(email));
    MimeBodyPart mimeBodyPart = new MimeBodyPart();

    message.setSubject("Change password");

    String username = email.split("@")[0];
    String code = new String(Base64.getEncoder().withoutPadding()
                                 .encode(username.getBytes()));

    String link = host + "/password/reset?code=" + code;
    String msg = "Please click on the following link to reset your password: " + link;
    sendMessage(mimeBodyPart, msg);

  }

  public void sendEmail(String email, String message) throws MessagingException {

    this.message.setRecipients(
        Message.RecipientType.TO, InternetAddress.parse(email));
    MimeBodyPart mimeBodyPart = new MimeBodyPart();

    this.message.setSubject("Change password");

    sendMessage(mimeBodyPart, message);
  }

  public void sendMail(String email, String voterCode) throws MessagingException {
    message.setRecipients(
        Message.RecipientType.TO, InternetAddress.parse(email));
    MimeBodyPart mimeBodyPart = new MimeBodyPart();

    message.setSubject("Paragala voter registration");
    String link = host + "/api/account/confirmation?code=" + voterCode;
    String msg = "Your voter code is: " + voterCode + ". " +
        "Please click the link to confirm your registration: " + link;

    sendMessage(mimeBodyPart, msg);
  }

  public void sendAdminEmail(String email, String key) throws MessagingException {
    message.setRecipients(
        Message.RecipientType.TO, InternetAddress.parse(email));
    MimeBodyPart mimeBodyPart = new MimeBodyPart();


    message.setSubject("Account confirmation");
    String code = new String(Base64.getEncoder().withoutPadding()
                                 .encode((email + "@@" + key).getBytes()));
    String link = host + "/api/users/confirmation?code=" + code;
    String msg = "Please click the link to confirm your registration: " + link;
    sendMessage(mimeBodyPart, msg);

  }

  private void sendMessage(MimeBodyPart mimeBodyPart, String msg) throws MessagingException {
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

  public void sendAdminEmail(String email, String wantedPassword, Boolean isSuper) throws MessagingException {

    message.setRecipients(
        Message.RecipientType.TO, InternetAddress.parse(email));
    MimeBodyPart mimeBodyPart = new MimeBodyPart();

    String wantedUserName = email.split("@")[0];
    message.setSubject("Account confirmation");
    String code = new String(Base64.getEncoder().withoutPadding()
                                 .encode((wantedUserName + "@@" + wantedPassword + "@@" + isSuper).getBytes()));
    String link = host + "/api/users/confirmation?code=" + code;
    String msg = "Please click the link to confirm your registration: " + link;
    sendMessage(mimeBodyPart, msg);
  }
}
