package io.angularpay.notification.ports.inbound;

import io.angularpay.notification.domain.Notification;
import io.angularpay.notification.models.NotificationStatusResponse;
import io.angularpay.notification.models.SendNotificationApiModel;
import io.angularpay.notification.models.SendNotificationResponse;

import java.util.List;
import java.util.Map;

public interface RestApiPort {

    SendNotificationResponse sendNotification(SendNotificationApiModel sendNotificationApiModel, Map<String, String> headers);
    NotificationStatusResponse getStatus(String notificationReference, Map<String, String> headers);
    Notification getByReference(String notificationReference, Map<String, String> headers);
    Notification getByClientReference(String clientReference, Map<String, String> headers);
    List<Notification> getNotificationList(int page, Map<String, String> headers);
}
