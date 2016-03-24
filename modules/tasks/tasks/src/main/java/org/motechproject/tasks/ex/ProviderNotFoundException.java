package org.motechproject.tasks.ex;

/**
 * Signals that provider with a given id was not found.
 */
public class ProviderNotFoundException extends IllegalArgumentException {

    private static final long serialVersionUID = -5419738008480651643L;

    public ProviderNotFoundException(String taskName, long providerId) {
        super("Unable to migrate task: " + taskName + ". Data provider with id " + providerId + " not found");
    }
}
