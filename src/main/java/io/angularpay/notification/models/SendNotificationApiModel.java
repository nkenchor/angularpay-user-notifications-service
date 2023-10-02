package io.angularpay.notification.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.angularpay.notification.domain.NotificationChannel;
import io.angularpay.notification.domain.NotificationType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SendNotificationApiModel {

    @NotEmpty
    @JsonProperty("client_reference")
    private String clientReference;

    @NotNull
    private NotificationChannel channel;

    @NotNull
    private NotificationType type;

    private String subject;

    private String from;

    @NotEmpty
    private String to;

    @NotEmpty
    private String message;

    @JsonProperty("send_at")
    private String sendAt;
}
