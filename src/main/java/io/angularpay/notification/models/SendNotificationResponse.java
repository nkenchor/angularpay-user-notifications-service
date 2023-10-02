package io.angularpay.notification.models;

import io.angularpay.notification.domain.NotificationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendNotificationResponse {
    private String reference;
    private NotificationStatus status;
}
