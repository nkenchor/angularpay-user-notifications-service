
package io.angularpay.notification.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document("notifications")
public class Notification {

    @Id
    private String id;
    @Version
    private int version;
    private String reference;
    @JsonProperty("client_reference")
    private String clientReference;
    @JsonProperty("created_on")
    private String createdOn;
    @JsonProperty("last_modified")
    private String lastModified;
    private NotificationChannel channel;
    private NotificationType type;
    private NotificationStatus status;
    private String subject;
    private String from;
    private String to;
    private String message;
    @JsonProperty("send_at")
    private String sendAt;

}
