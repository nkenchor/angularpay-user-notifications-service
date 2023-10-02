package io.angularpay.notification.adapters.outbound;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import io.angularpay.notification.configurations.AngularPayConfiguration;
import io.angularpay.notification.models.GenericMessageRequest;
import io.angularpay.notification.models.GenericMessageResponse;
import io.angularpay.notification.ports.outbound.SmsPort;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsAdapter implements SmsPort {

    private final AngularPayConfiguration configuration;

    public TwilioSmsAdapter(AngularPayConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public GenericMessageResponse send(GenericMessageRequest request) {
        try {
            Twilio.init(this.configuration.getTwilio().getAccountSid(), this.configuration.getTwilio().getAuthToken());
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(request.getTo()),
                    new com.twilio.type.PhoneNumber(request.getFrom()),
                    request.getMessage())
                    .create();

            Message.Status status = message.getStatus();
            boolean success = status != Message.Status.CANCELED
                    && status != Message.Status.FAILED
                    && status != Message.Status.PARTIALLY_DELIVERED
                    && status != Message.Status.UNDELIVERED;

            return GenericMessageResponse.builder()
                    .success(success)
                    .messageStatus(status.name())
                    .build();
        } catch (ApiException exception) {
            return GenericMessageResponse.builder()
                    .success(false)
                    .error(exception)
                    .build();
        }
    }
}
