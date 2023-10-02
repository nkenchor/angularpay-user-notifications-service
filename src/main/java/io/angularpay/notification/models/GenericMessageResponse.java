package io.angularpay.notification.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenericMessageResponse {

    private boolean success;
    private String messageStatus;
    private Throwable error;
}
