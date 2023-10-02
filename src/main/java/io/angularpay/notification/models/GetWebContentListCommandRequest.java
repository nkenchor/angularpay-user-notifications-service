package io.angularpay.notification.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GetWebContentListCommandRequest extends AccessControl {

    private Paging paging;

    GetWebContentListCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
