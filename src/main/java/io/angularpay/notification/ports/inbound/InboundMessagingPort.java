package io.angularpay.notification.ports.inbound;

import io.angularpay.notification.models.platform.PlatformConfigurationIdentifier;

public interface InboundMessagingPort {
    void onMessage(String message, String topic);
    void onMessage(String message, PlatformConfigurationIdentifier identifier);
}
