
package io.angularpay.notification.models;

import io.angularpay.notification.domain.NotificationStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationStatusResponse {
    private final NotificationStatus status;
}
