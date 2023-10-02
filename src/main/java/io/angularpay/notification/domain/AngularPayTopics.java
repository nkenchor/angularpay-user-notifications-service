package io.angularpay.notification.domain;

public enum AngularPayTopics {
    NOTIFICATIONS("notifications");

    private final String topic;

    public String topic() {
        return this.topic;
    }

    AngularPayTopics(String topic) {
        this.topic = topic;
    }
}
