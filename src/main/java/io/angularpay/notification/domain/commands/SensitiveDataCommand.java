package io.angularpay.notification.domain.commands;

public interface SensitiveDataCommand<T> {
    T mask(T raw);
}
