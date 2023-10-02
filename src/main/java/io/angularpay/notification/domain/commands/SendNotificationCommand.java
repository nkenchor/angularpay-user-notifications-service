package io.angularpay.notification.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.angularpay.notification.adapters.outbound.MongoAdapter;
import io.angularpay.notification.adapters.outbound.SmtpEmailAdapter;
import io.angularpay.notification.adapters.outbound.TwilioSmsAdapter;
import io.angularpay.notification.configurations.AngularPayConfiguration;
import io.angularpay.notification.domain.*;
import io.angularpay.notification.exceptions.CommandException;
import io.angularpay.notification.exceptions.ErrorCode;
import io.angularpay.notification.exceptions.ErrorObject;
import io.angularpay.notification.models.GenericMessageRequest;
import io.angularpay.notification.models.GenericMessageResponse;
import io.angularpay.notification.models.SendNotificationCommandRequest;
import io.angularpay.notification.models.SendNotificationResponse;
import io.angularpay.notification.ports.outbound.EmailPort;
import io.angularpay.notification.ports.outbound.SmsPort;
import io.angularpay.notification.validation.DefaultConstraintValidator;
import io.angularpay.notification.validation.EmailValidator;
import io.angularpay.notification.validation.PhoneValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;

import static io.angularpay.notification.common.Constants.ERROR_SOURCE;
import static io.angularpay.notification.exceptions.ErrorCode.*;
import static io.angularpay.notification.helpers.CommandHelper.validateNotExistOrThrow;
import static io.angularpay.notification.helpers.Helper.*;

@Slf4j
@Service
public class SendNotificationCommand extends AbstractCommand<SendNotificationCommandRequest, SendNotificationResponse>
        implements SensitiveDataCommand<SendNotificationCommandRequest> {

    private final DefaultConstraintValidator validator;
    private final MongoAdapter mongoAdapter;
    private final AngularPayConfiguration configuration;
    private final SmsPort smsPort;
    private final EmailPort emailPort;
    private final EmailValidator emailValidator;
    private final PhoneValidator phoneValidator;

    public SendNotificationCommand(
            ObjectMapper mapper,
            DefaultConstraintValidator validator,
            MongoAdapter mongoAdapter,
            AngularPayConfiguration configuration,
            TwilioSmsAdapter twilioSmsAdapter,
            SmtpEmailAdapter smtpEmailAdapter,
            EmailValidator emailValidator,
            PhoneValidator phoneValidator) {
        super("SendNotificationCommand", mapper);
        this.validator = validator;
        this.mongoAdapter = mongoAdapter;
        this.configuration = configuration;
        this.smsPort = twilioSmsAdapter;
        this.emailPort = smtpEmailAdapter;
        this.emailValidator = emailValidator;
        this.phoneValidator = phoneValidator;
    }

    @Override
    protected String getResourceOwner(SendNotificationCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected SendNotificationResponse handle(SendNotificationCommandRequest request) {
        validateNotExistOrThrow(this.mongoAdapter, request.getSendNotificationApiModel().getClientReference());

        if (request.getSendNotificationApiModel().getType() == NotificationType.SCHEDULED
                && Instant.parse(request.getSendNotificationApiModel().getSendAt()).isBefore(Instant.now())) {
            throw CommandException.builder()
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .errorCode(ErrorCode.INVALID_SCHEDULE_DATE_ERROR)
                    .message(ErrorCode.INVALID_SCHEDULE_DATE_ERROR.getDefaultMessage())
                    .build();
        }

        GenericMessageRequest genericMessageRequest = GenericMessageRequest.builder()
                .to(request.getSendNotificationApiModel().getTo())
                .message(request.getSendNotificationApiModel().getMessage())
                .build();

        GenericMessageResponse genericMessageResponse = GenericMessageResponse.builder().success(false).build();
        if (request.getSendNotificationApiModel().getType() == NotificationType.INSTANT) {
            if (request.getSendNotificationApiModel().getChannel() == NotificationChannel.SMS) {
                genericMessageRequest.setFrom(this.configuration.getTwilio().getFrom());
                genericMessageResponse = this.smsPort.send(genericMessageRequest);
            }
            if (request.getSendNotificationApiModel().getChannel() == NotificationChannel.EMAIL) {
                genericMessageRequest.setFrom(request.getSendNotificationApiModel().getFrom());
                genericMessageRequest.setSubject(request.getSendNotificationApiModel().getSubject());
                genericMessageResponse = this.emailPort.send(genericMessageRequest);
            }
        }

        Notification notification = Notification.builder()
                .reference(UUID.randomUUID().toString())
                .clientReference(request.getSendNotificationApiModel().getClientReference())
                .channel(request.getSendNotificationApiModel().getChannel())
                .type(request.getSendNotificationApiModel().getType())
                .subject(request.getSendNotificationApiModel().getSubject())
                .from(request.getSendNotificationApiModel().getFrom())
                .to(request.getSendNotificationApiModel().getTo())
                .message(request.getSendNotificationApiModel().getMessage())
                .sendAt(request.getSendNotificationApiModel().getSendAt())
                .status(genericMessageResponse.isSuccess() ? NotificationStatus.SENT : NotificationStatus.FAILED)
                .build();

        Notification response = this.mongoAdapter.createNotification(notification);

        if (!genericMessageResponse.isSuccess()) {
            if (Objects.nonNull(genericMessageResponse.getError())) {
                throw CommandException.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .errorCode(SMS_SERVICE_ERROR)
                        .message(SMS_SERVICE_ERROR.getDefaultMessage())
                        .cause(genericMessageResponse.getError())
                        .build();
            } else {
                throw CommandException.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .errorCode(MESSAGE_SERVICE_ERROR)
                        .message(MESSAGE_SERVICE_ERROR.getDefaultMessage())
                        .cause(genericMessageResponse.getError())
                        .build();
            }
        }

        return SendNotificationResponse.builder()
                .reference(response.getReference())
                .status(response.getStatus())
                .build();
    }

    @Override
    protected List<ErrorObject> validate(SendNotificationCommandRequest request) {
        List<ErrorObject> errors = new ArrayList<>();
        if (request.getSendNotificationApiModel().getChannel() == NotificationChannel.EMAIL) {
            if (!StringUtils.hasText(request.getSendNotificationApiModel().getFrom())) {
                errors.add(ErrorObject.builder()
                        .code(VALIDATION_ERROR)
                        .message("from must not be empty for EMAIL notification channel")
                        .source(ERROR_SOURCE)
                        .build());
            }
            if (!StringUtils.hasText(request.getSendNotificationApiModel().getSubject())) {
                errors.add(ErrorObject.builder()
                        .code(VALIDATION_ERROR)
                        .message("subject must not be empty for EMAIL notification channel")
                        .source(ERROR_SOURCE)
                        .build());
            }
            if (!this.emailValidator.isValid(request.getSendNotificationApiModel().getTo())) {
                errors.add(ErrorObject.builder()
                        .code(VALIDATION_ERROR)
                        .message("to must not be a valid email address when notification channel is EMAIL")
                        .source(ERROR_SOURCE)
                        .build());
            }
        }

        if (request.getSendNotificationApiModel().getChannel() == NotificationChannel.SMS) {
            if (!this.phoneValidator.isValid(request.getSendNotificationApiModel().getTo())) {
                errors.add(ErrorObject.builder()
                        .code(VALIDATION_ERROR)
                        .message("to must not be a valid phone number when notification channel is SMS")
                        .source(ERROR_SOURCE)
                        .build());
            }
        }

        if (Objects.nonNull(request.getSendNotificationApiModel().getType())
                && request.getSendNotificationApiModel().getType() == NotificationType.SCHEDULED) {
            try {
                Instant.parse(request.getSendNotificationApiModel().getSendAt());
            } catch (DateTimeParseException exception) {
                errors.add(ErrorObject.builder()
                        .code(VALIDATION_ERROR)
                        .message("send_at must be a valid date")
                        .source(ERROR_SOURCE)
                        .build());
            }
        }
        errors.addAll(this.validator.validate(request));
        return errors;
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }

    @Override
    public SendNotificationCommandRequest mask(SendNotificationCommandRequest raw) {
        try {
            JsonNode node = mapper.convertValue(raw, JsonNode.class);

            JsonNode authenticatedUser = node.get("authenticatedUser");
            ((ObjectNode) authenticatedUser).put("username", maskUsername(raw.getAuthenticatedUser().getUsername()));
            ((ObjectNode) authenticatedUser).put("userReference", maskUserReference(raw.getAuthenticatedUser().getUserReference()));

            JsonNode sendNotificationApiModel = node.get("sendNotificationApiModel");
            if (raw.getSendNotificationApiModel().getChannel() == NotificationChannel.EMAIL) {
                if (StringUtils.hasText(raw.getSendNotificationApiModel().getFrom())) {
                    ((ObjectNode) sendNotificationApiModel).put("from", tryMaskEmail(raw.getSendNotificationApiModel().getFrom()));
                }
                if (StringUtils.hasText(raw.getSendNotificationApiModel().getTo())) {
                    ((ObjectNode) sendNotificationApiModel).put("to", maskEmail(raw.getSendNotificationApiModel().getTo()));
                }
            }
            if (raw.getSendNotificationApiModel().getChannel() == NotificationChannel.SMS) {
                if (StringUtils.hasText(raw.getSendNotificationApiModel().getFrom())) {
                    ((ObjectNode) sendNotificationApiModel).put("from", maskPhone(raw.getSendNotificationApiModel().getFrom()));
                }
                if (StringUtils.hasText(raw.getSendNotificationApiModel().getTo())) {
                    ((ObjectNode) sendNotificationApiModel).put("to", maskPhone(raw.getSendNotificationApiModel().getTo()));
                }
            }

            return mapper.treeToValue(node, SendNotificationCommandRequest.class);
        } catch (JsonProcessingException exception) {
            return raw;
        }
    }
}
