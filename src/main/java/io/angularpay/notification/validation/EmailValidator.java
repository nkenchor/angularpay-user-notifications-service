package io.angularpay.notification.validation;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

import static io.angularpay.notification.common.Constants.REGEX_EMAIL_ADDRESS;

@Service
public class EmailValidator {

    private final Pattern emailPattern = Pattern.compile(REGEX_EMAIL_ADDRESS, Pattern.CASE_INSENSITIVE);

    public boolean isValid(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        } else {
            return emailPattern.matcher(email).matches();
        }
    }
}
