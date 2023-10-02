package io.angularpay.notification.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("angularpay")
@Data
public class AngularPayConfiguration {
    private int pageSize;
    private int codecSizeInMB;
    private Twilio twilio;
    private SMTP smtp;
    private Redis redis;

    @Data
    public static class Redis {
        private String host;
        private int port;
        private int timeout;
    }

    @Data
    public static class Twilio {
        private String accountSid;
        private String authToken;
        private String from;
        private String sendGridApiKey;
    }

    @Data
    public static class SMTP {
        private String host;
        private int port;
        private boolean ssl;
        private boolean auth;
        private String username;
        private String password;
    }

}
