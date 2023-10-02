package io.angularpay.notification.ports.outbound;

import io.angularpay.notification.models.GenericMessageRequest;
import io.angularpay.notification.models.GenericMessageResponse;

public interface EmailPort {

    GenericMessageResponse send(GenericMessageRequest request);
}
