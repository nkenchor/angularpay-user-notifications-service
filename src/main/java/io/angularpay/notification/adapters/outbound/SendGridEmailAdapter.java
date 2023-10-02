package io.angularpay.notification.adapters.outbound;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import io.angularpay.notification.configurations.AngularPayConfiguration;
import io.angularpay.notification.models.GenericMessageRequest;
import io.angularpay.notification.models.GenericMessageResponse;
import io.angularpay.notification.ports.outbound.EmailPort;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridEmailAdapter implements EmailPort {

    private final AngularPayConfiguration configuration;

    public SendGridEmailAdapter(AngularPayConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public GenericMessageResponse send(GenericMessageRequest request) {
        try {
            Email from = new Email(request.getFrom());
            String subject = request.getSubject();
            Email to = new Email(request.getTo());
            Content content = new Content("text/plain", request.getMessage());
            Mail mail = new Mail(from, subject, to, content);

            SendGrid sendGrid = new SendGrid(System.getenv(this.configuration.getTwilio().getSendGridApiKey()));

            Request sendGridRequest = new Request();
            sendGridRequest.setMethod(Method.POST);
            sendGridRequest.setEndpoint("mail/send");
            sendGridRequest.setBody(mail.build());

            Response response = sendGrid.api(sendGridRequest);

            return GenericMessageResponse.builder()
                    .success(response.getStatusCode() != 0) // TODO determine success status code from Twilio SendGrid docs
                    .messageStatus(String.valueOf(response.getStatusCode()))
                    .build();
        } catch (IOException exception) {
            return GenericMessageResponse.builder()
                    .success(false)
                    .error(exception)
                    .build();
        }
    }
}
