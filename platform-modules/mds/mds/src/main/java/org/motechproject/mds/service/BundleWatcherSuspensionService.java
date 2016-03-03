package org.motechproject.mds.service;

/**
 * An OSGi service, allowing to temporarily disable bundle processing by MDS. When processing
 * gets suspended, MDS will still listen to bundle events, but instead of processing them,
 * they will be queued and processed after the processing gets restored. This allows, besides
 * others, to install/uninstall a larger amount of bundles at one time, without facing annotation
 * processing problems.
 */
public interface BundleWatcherSuspensionService {

    /**
     * Temporarily suspends bundle processing. MDS will queue bundle events and process them
     * once the processing gets restored.
     */
    void suspendBundleProcessing();

    /**
     * Restores suspended bundle processing. All bundle events, that happened while
     * processing was suspended, will get processed.
     */
    void restoreBundleProcessing();
}
