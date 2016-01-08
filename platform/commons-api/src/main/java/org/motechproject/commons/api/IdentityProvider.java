package org.motechproject.commons.api;

/**
 * Interface for classes providing identity.
 */
public interface IdentityProvider {

    /**
     * Returns identity of class implementing this interface.
     *
     * @return the identity
     */
    String getIdentity();
}
