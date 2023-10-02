package io.angularpay.notification.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SendNotificationCommandRequest extends AccessControl {

    @NotNull
    @Valid
    private SendNotificationApiModel sendNotificationApiModel;

    SendNotificationCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
