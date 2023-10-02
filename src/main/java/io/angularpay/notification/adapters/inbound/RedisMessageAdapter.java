package io.angularpay.notification.adapters.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.notification.domain.commands.PlatformConfigurationsConverterCommand;
import io.angularpay.notification.domain.commands.SendNotificationCommand;
import io.angularpay.notification.models.AuthenticatedUser;
import io.angularpay.notification.models.SendNotificationApiModel;
import io.angularpay.notification.models.SendNotificationCommandRequest;
import io.angularpay.notification.models.platform.PlatformConfigurationIdentifier;
import io.angularpay.notification.ports.inbound.InboundMessagingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static io.angularpay.notification.helpers.Helper.fromHeaders;
import static io.angularpay.notification.helpers.Helper.serviceAccountHeaders;
import static io.angularpay.notification.models.platform.PlatformConfigurationSource.TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisMessageAdapter implements InboundMessagingPort {

    private final SendNotificationCommand sendNotificationCommand;
    private final ObjectMapper objectMapper;
    private final PlatformConfigurationsConverterCommand converterCommand;

    @Override
    public void onMessage(String message, String topic) {
        try {
            log.info("processing message");
            Map<String, String> headers = serviceAccountHeaders();
            AuthenticatedUser authenticatedUser = fromHeaders(headers);
            SendNotificationApiModel sendNotificationApiModel = objectMapper.readValue(message, SendNotificationApiModel.class);
            SendNotificationCommandRequest sendNotificationCommandRequest = SendNotificationCommandRequest.builder()
                    .sendNotificationApiModel(sendNotificationApiModel)
                    .authenticatedUser(authenticatedUser)
                    .build();
            this.sendNotificationCommand.execute(sendNotificationCommandRequest);
        } catch (JsonProcessingException exception) {
            log.error("An error occurred while processing message", exception);
        }
    }

    @Override
    public void onMessage(String message, PlatformConfigurationIdentifier identifier) {
        this.converterCommand.execute(message, identifier, TOPIC);
    }
}
