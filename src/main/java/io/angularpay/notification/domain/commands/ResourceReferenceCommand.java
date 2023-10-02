package io.angularpay.notification.domain.commands;

public interface ResourceReferenceCommand<T, R> {

    R map(T referenceResponse);
}
