package org.motechproject.server.api;

import org.osgi.framework.Bundle;

/**
 * Interface for custom bundle loading processes
 *
 * @author Ricky Wang
 */
public interface BundleLoader {

    /**
     * @param bundle
     * @throws BundleLoadingException
     */
    void loadBundle(Bundle bundle) throws BundleLoadingException;

}
