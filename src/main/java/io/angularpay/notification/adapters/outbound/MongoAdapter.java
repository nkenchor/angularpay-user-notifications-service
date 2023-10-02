package io.angularpay.notification.adapters.outbound;

import io.angularpay.notification.domain.Notification;
import io.angularpay.notification.ports.outbound.PersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MongoAdapter implements PersistencePort {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification createNotification(Notification request) {
        request.setCreatedOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        request.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return notificationRepository.save(request);
    }

    @Override
    public Notification updateNotification(Notification request) {
        request.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return notificationRepository.save(request);
    }

    @Override
    public Optional<Notification> findNotificationByReference(String reference) {
        return notificationRepository.findByReference(reference);
    }

    @Override
    public Optional<Notification> findNotificationByClientReference(String clientReference) {
        return notificationRepository.findByClientReference(clientReference);
    }

    @Override
    public List<Notification> listNotification() {
        return notificationRepository.findAll();
    }
}
