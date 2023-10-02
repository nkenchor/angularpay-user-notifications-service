package io.angularpay.notification.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.notification.adapters.outbound.MongoAdapter;
import io.angularpay.notification.domain.Notification;
import io.angularpay.notification.domain.Role;
import io.angularpay.notification.exceptions.CommandException;
import io.angularpay.notification.exceptions.ErrorObject;
import io.angularpay.notification.models.GenericByReferenceCommandRequest;
import io.angularpay.notification.validation.DefaultConstraintValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static io.angularpay.notification.exceptions.ErrorCode.REQUEST_NOT_FOUND;

@Service
public class GetNotificationByReferenceCommand extends AbstractCommand<GenericByReferenceCommandRequest, Notification> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;

    public GetNotificationByReferenceCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator) {
        super("GetNotificationByReferenceCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
    }

    @Override
    protected String getResourceOwner(GenericByReferenceCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected Notification handle(GenericByReferenceCommandRequest request) {
        return this.mongoAdapter.findNotificationByReference(request.getReference())
                .orElseThrow(() -> CommandException.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .errorCode(REQUEST_NOT_FOUND)
                        .message(REQUEST_NOT_FOUND.getDefaultMessage())
                        .build());
    }

    @Override
    protected List<ErrorObject> validate(GenericByReferenceCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}
