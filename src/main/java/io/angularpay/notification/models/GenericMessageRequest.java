package io.angularpay.notification.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenericMessageRequest {

    private String subject;
    private String from;
    private String to;
    private String message;
}
