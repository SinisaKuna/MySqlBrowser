package Servis;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class SendMail {
    public static void sendMail(String email, Integer kontrola) {
        final String username = "ivansku*na@gmail.com".replace("*","");
        final String password = "icajnk*ygzodckjms".replace("*","");

        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.host", "smtp.gm*ail.com".replace("*",""));
        props.put("mail.smtp.port", "5*87".replace("*",""));
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                }

        );
        session.getProperties().put("mail.smtp.starttls.enable", "true");

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("ivanskuna@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject("Potvrda registracije");
            message.setText("Vaš autorizacijski kod: " + kontrola.toString());
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
