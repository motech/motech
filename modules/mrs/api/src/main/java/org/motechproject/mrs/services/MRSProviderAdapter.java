package org.motechproject.mrs.services;

import org.motechproject.mrs.domain.MRSProvider;

/**
 * An interface to persist providers
 */
public interface MRSProviderAdapter {

    /**
     * Persists a provider
     * @param provider The provider to save
     * @return The saved provider object
     */
    MRSProvider saveProvider(MRSProvider provider);

    /**
     * Retrieves a provider by the provider's motech id
     * @param motechId The motech id of the provider
     * @return The provider given by the motech id
     */
    MRSProvider getProviderByProviderId(String motechId);

    void removeProvider(String motechId);

}
