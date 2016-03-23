package org.motechproject.tasks.ex;

/**
 * Signals that provider with a given id was not found.
 */
public class ProviderNotFoundException extends IllegalArgumentException {

    public ProviderNotFoundException(long providerId) {
        super("Unable to migrate task. Data provider with id " + providerId + " not found");
    }
}
