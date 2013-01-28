package org.motechproject.mrs.services;

import org.motechproject.mrs.domain.Provider;

/**
 * An interface to persist providers
 */
public interface ProviderAdapter {

    /**
     * Persists a provider
     * @param provider The provider to save
     * @return The saved provider object
     */
    Provider saveProvider(Provider provider);

    /**
     * Retrieves a provider by the provider's motech id
     * @param motechId The motech id of the provider
     * @return The provider given by the motech id
     */
    Provider getProviderByProviderId(String motechId);

}
