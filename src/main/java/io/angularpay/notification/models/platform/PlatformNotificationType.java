
package io.angularpay.notification.models.platform;

import lombok.Data;

@Data
public class PlatformNotificationType {

    private String code;
    private boolean enabled;
    private String reference;

}
