package io.angularpay.notification.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_MESSAGE_ERROR("The message format read from the given topic is invalid"),
    VALIDATION_ERROR("The request has validation errors"),
    REQUEST_NOT_FOUND("The requested resource was NOT found"),
    GENERIC_ERROR("Generic error occurred. See stacktrace for details"),
    AUTHORIZATION_ERROR("You do NOT have adequate permission to access this resource"),
    DUPLICATE_ENTRY_ERROR("Duplicate entry detected."),
    MESSAGE_SERVICE_ERROR("An error occurred while sending the message."),
    SMS_SERVICE_ERROR("An error occurred while sending SMS message."),
    INVALID_SCHEDULE_DATE_ERROR("You cannot schedule a task in the past. You must provide a future date"),
    NO_PRINCIPAL("Principal identifier NOT provided", 500);

    private final String defaultMessage;
    private final int defaultHttpStatus;

    ErrorCode(String defaultMessage) {
        this(defaultMessage, 400);
    }

    ErrorCode(String defaultMessage, int defaultHttpStatus) {
        this.defaultMessage = defaultMessage;
        this.defaultHttpStatus = defaultHttpStatus;
    }
}
