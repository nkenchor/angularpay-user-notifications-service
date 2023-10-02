package io.angularpay.notification.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.notification.adapters.outbound.MongoAdapter;
import io.angularpay.notification.domain.Notification;
import io.angularpay.notification.domain.Role;
import io.angularpay.notification.exceptions.ErrorObject;
import io.angularpay.notification.models.GetWebContentListCommandRequest;
import io.angularpay.notification.validation.DefaultConstraintValidator;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GetNotificationListCommand extends AbstractCommand<GetWebContentListCommandRequest, List<Notification>> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;

    public GetNotificationListCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator) {
        super("GetWebContentListCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
    }

    @Override
    protected String getResourceOwner(GetWebContentListCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected List<Notification> handle(GetWebContentListCommandRequest request) {
        return this.mongoAdapter.listNotification();
    }

    @Override
    protected List<ErrorObject> validate(GetWebContentListCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}
