package io.angularpay.notification.helpers;

import io.angularpay.notification.adapters.outbound.MongoAdapter;
import io.angularpay.notification.domain.Notification;
import io.angularpay.notification.exceptions.CommandException;
import io.angularpay.notification.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static io.angularpay.notification.exceptions.ErrorCode.DUPLICATE_ENTRY_ERROR;
import static io.angularpay.notification.exceptions.ErrorCode.REQUEST_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CommandHelper {

    public static Notification getRequestByReferenceOrThrow(MongoAdapter mongoAdapter, String reference) {
        return mongoAdapter.findNotificationByReference(reference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
    }

    private static CommandException commandException(HttpStatus status, ErrorCode errorCode) {
        return CommandException.builder()
                .status(status)
                .errorCode(errorCode)
                .message(errorCode.getDefaultMessage())
                .build();
    }

    public static void validateNotExistOrThrow(MongoAdapter mongoAdapter, String  clientReference) {
        mongoAdapter.findNotificationByClientReference(clientReference).ifPresent(
                (x) -> {
                    throw commandException(HttpStatus.NOT_FOUND, DUPLICATE_ENTRY_ERROR);
                }
        );
    }

}
