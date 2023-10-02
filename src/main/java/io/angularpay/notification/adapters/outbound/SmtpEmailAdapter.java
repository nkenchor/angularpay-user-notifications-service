package io.angularpay.notification.adapters.outbound;

import io.angularpay.notification.configurations.AngularPayConfiguration;
import io.angularpay.notification.models.GenericMessageRequest;
import io.angularpay.notification.models.GenericMessageResponse;
import io.angularpay.notification.ports.outbound.EmailPort;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class SmtpEmailAdapter implements EmailPort {

    private final AngularPayConfiguration configuration;

    public SmtpEmailAdapter(AngularPayConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public GenericMessageResponse send(GenericMessageRequest request) {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", this.configuration.getSmtp().getHost());
        properties.put("mail.smtp.port", String.valueOf(this.configuration.getSmtp().getPort()));
        properties.put("mail.smtp.ssl.enable", String.valueOf(this.configuration.getSmtp().isSsl()));
        properties.put("mail.smtp.auth", String.valueOf(this.configuration.getSmtp().isAuth()));

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(configuration.getSmtp().getUsername(), configuration.getSmtp().getPassword());
            }
        });
        session.setDebug(true);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(request.getFrom()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(request.getTo()));
            message.setSubject(request.getSubject());
            message.setText(request.getMessage());

            Transport.send(message);
            return GenericMessageResponse.builder()
                    .success(true)
                    .build();
        } catch (MessagingException exception) {
            return GenericMessageResponse.builder()
                    .success(false)
                    .error(exception)
                    .build();
        }
    }
}
