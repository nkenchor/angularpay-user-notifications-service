package io.angularpay.notification.adapters.inbound;

import io.angularpay.notification.configurations.AngularPayConfiguration;
import io.angularpay.notification.domain.Notification;
import io.angularpay.notification.domain.commands.*;
import io.angularpay.notification.models.*;
import io.angularpay.notification.ports.inbound.RestApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static io.angularpay.notification.helpers.Helper.fromHeaders;

@RestController
@RequestMapping("/notification/requests")
@RequiredArgsConstructor
public class RestApiAdapter implements RestApiPort {

    private final SendNotificationCommand sendNotificationCommand;
    private final GetNotificationStatusCommand getNotificationStatusCommand;
    private final GetNotificationByReferenceCommand getNotificationByReferenceCommand;
    private final GetNotificationByClientReferenceCommand getNotificationByClientReferenceCommand;
    private final GetNotificationListCommand getNotificationListCommand;

    private final AngularPayConfiguration configuration;

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public SendNotificationResponse sendNotification(
            @RequestBody SendNotificationApiModel sendNotificationApiModel,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        SendNotificationCommandRequest sendNotificationCommandRequest = SendNotificationCommandRequest.builder()
                .sendNotificationApiModel(sendNotificationApiModel)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.sendNotificationCommand.execute(sendNotificationCommandRequest);
    }

    @GetMapping("/{notificationReference}/status")
    @ResponseBody
    @Override
    public NotificationStatusResponse getStatus(
            @PathVariable String notificationReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericByReferenceCommandRequest genericByReferenceCommandRequest = GenericByReferenceCommandRequest.builder()
                .reference(notificationReference)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.getNotificationStatusCommand.execute(genericByReferenceCommandRequest);
    }

    @GetMapping("/{notificationReference}")
    @ResponseBody
    @Override
    public Notification getByReference(
            @PathVariable String notificationReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericByReferenceCommandRequest genericByReferenceCommandRequest = GenericByReferenceCommandRequest.builder()
                .reference(notificationReference)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.getNotificationByReferenceCommand.execute(genericByReferenceCommandRequest);
    }

    @GetMapping("/client/{clientReference}")
    @ResponseBody
    @Override
    public Notification getByClientReference(
            @PathVariable String clientReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericByReferenceCommandRequest genericByReferenceCommandRequest = GenericByReferenceCommandRequest.builder()
                .reference(clientReference)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.getNotificationByClientReferenceCommand.execute(genericByReferenceCommandRequest);
    }

    @GetMapping("list/page/{page}")
    @ResponseBody
    @Override
    public List<Notification> getNotificationList(
            @PathVariable int page,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetWebContentListCommandRequest getWebContentListCommandRequest = GetWebContentListCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .build();
        return this.getNotificationListCommand.execute(getWebContentListCommandRequest);
    }
}
