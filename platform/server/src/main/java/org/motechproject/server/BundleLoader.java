package org.motechproject.server;

import org.motechproject.server.ex.BundleLoadingException;
import org.osgi.framework.Bundle;

/**
 * Interface for custom bundle loading processes
 *
 * @author Ricky Wang
 */
public interface BundleLoader {

    /**
     * @param bundle the bundle to process
     * @throws BundleLoadingException if there were issues while loading the bundle
     */
    void loadBundle(Bundle bundle) throws BundleLoadingException;

}
