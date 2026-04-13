package com.helpdesk.util;

import com.helpdesk.config.AppConfig;
import java.util.Properties;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public final class EmailUtil {

    private static final Logger LOGGER = Logger.getLogger(EmailUtil.class.getName());

    private EmailUtil() {
    }

    public static void sendEmail(String to, String subject, String body) {
        if (!AppConfig.getBoolean("mail.enabled", false)) {
            LOGGER.info("Email sending is disabled. Skipping message.");
            return;
        }

        String recipient = InputValidator.normalize(to);
        if (recipient == null || recipient.isBlank()) {
            LOGGER.warning("Skipping email because the recipient is empty.");
            return;
        }

        final String fromEmail = AppConfig.get("mail.from");
        final String fromName = AppConfig.get("mail.fromName");
        final String replyTo = AppConfig.get("mail.replyTo");
        final String password = AppConfig.get("mail.password");
        final String host = AppConfig.get("mail.host");
        final String port = AppConfig.get("mail.port");

        if (fromEmail == null || fromEmail.isBlank() || password == null || password.isBlank() || host == null || host.isBlank()) {
            LOGGER.warning("Email is enabled but SMTP configuration is incomplete. Skipping message.");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(AppConfig.getBoolean("mail.starttls", true)));
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", host);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            String senderName = fromName == null || fromName.isBlank() ? "HelpDesk System" : fromName;
            try {
                message.setFrom(new InternetAddress(fromEmail, senderName));
            } catch (UnsupportedEncodingException ex) {
                LOGGER.log(Level.WARNING, "Unable to encode sender name, falling back to raw from address.", ex);
                message.setFrom(new InternetAddress(fromEmail));
            }
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            if (replyTo != null && !replyTo.isBlank()) {
                try {
                    message.setReplyTo(InternetAddress.parse(replyTo));
                } catch (AddressException ex) {
                    LOGGER.log(Level.WARNING, "Ignoring invalid reply-to address: " + replyTo, ex);
                }
            }
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            LOGGER.info("Email sent to " + recipient);
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send email to " + recipient, e);
        }
    }
}
