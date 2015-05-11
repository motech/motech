package org.motechproject.mds.service;

/**
 * The <code>MdsBundleRegenerationService</code> interface provides methods for regenerating MDS Entities Bundle
 * and commands all Motech instances to do the same.
 */
public interface MdsBundleRegenerationService {

    /**
     * Constructs entities, builds and starts the MDS Entities Bundle, commands other Motech instances to
     * regenerate their MDS Entities Bundle.
     */
    void regenerateMdsDataBundle();

    /**
     * Constructs entities, builds and starts the MDS Entities Bundle, commands other Motech instances to
     * regenerate their MDS Entities Bundle. This method should be used after DDE enhancement. It will build all
     * DDE classes and refresh modules from which the DDE being enhanced comes from.
     *
     * @param moduleNames modules names of the entities from which the enhanced DDE comes from
     */
    void regenerateMdsDataBundleAfterDdeEnhancement(String... moduleNames);
}
