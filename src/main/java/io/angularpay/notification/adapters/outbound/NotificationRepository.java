package io.angularpay.notification.adapters.outbound;

import io.angularpay.notification.domain.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    Optional<Notification> findByReference(String reference);
    Optional<Notification> findByClientReference(String clientReference);
}
