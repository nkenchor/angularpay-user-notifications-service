package io.angularpay.notification.ports.outbound;

import io.angularpay.notification.domain.Notification;

import java.util.List;
import java.util.Optional;

public interface PersistencePort {
    Notification createNotification(Notification request);
    Notification updateNotification(Notification request);
    Optional<Notification> findNotificationByReference(String reference);
    Optional<Notification> findNotificationByClientReference(String clientReference);
    List<Notification> listNotification();

}
