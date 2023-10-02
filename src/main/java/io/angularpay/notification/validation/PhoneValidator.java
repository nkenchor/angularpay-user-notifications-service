package io.angularpay.notification.validation;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

import static io.angularpay.notification.common.Constants.REGEX_INTERNATIONAL_PHONE_NUMBER;

@Service
public class PhoneValidator {

    private final Pattern phonePattern = Pattern.compile(REGEX_INTERNATIONAL_PHONE_NUMBER);

    public boolean isValid(String phone) {
        if (!StringUtils.hasText(phone)) {
            return false;
        } else {
            return phonePattern.matcher(phone).matches();
        }
    }
}
