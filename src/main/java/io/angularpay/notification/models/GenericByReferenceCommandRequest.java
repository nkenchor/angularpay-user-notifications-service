package io.angularpay.notification.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GenericByReferenceCommandRequest extends AccessControl {

    @NotEmpty
    private String reference;

    GenericByReferenceCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
