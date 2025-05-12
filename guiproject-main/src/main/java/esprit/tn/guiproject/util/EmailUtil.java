package esprit.tn.guiproject.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {
    private static final String EMAIL = "smartmove1101@gmail.com"; // Votre email
    private static final String PASSWORD = "epwgfnytkdbgpbew"; // Mot de passe d'application Gmail

    private static Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        return props;
    }

    public static void sendPasswordResetEmail(String toEmail, String resetCode) {
        Properties props = getMailProperties();
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Réinitialisation de votre mot de passe - Smart Move");
            message.setText("Bonjour,\n\n"
                    + "Vous avez demandé la réinitialisation de votre mot de passe.\n"
                    + "Votre code de réinitialisation est : " + resetCode + "\n\n"
                    + "Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email.\n\n"
                    + "Cordialement,\n"
                    + "L'équipe Smart Move");

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }

    public static void sendApprovalEmail(String toEmail, String userName) {
        Properties props = getMailProperties();
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Compte Approuvé - Smart Move");
            message.setText("Bonjour " + userName + ",\n\n"
                    + "Votre compte a été approuvé avec succès.\n"
                    + "Vous pouvez maintenant vous connecter à votre compte.\n\n"
                    + "Cordialement,\n"
                    + "L'équipe Smart Move");

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }
}