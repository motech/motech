package org.motechproject.server.osgi;

import org.osgi.framework.Bundle;

/**
 * Interface for custom bundle loading processes
 *
 * @author Ricky Wang
 */
public interface BundleLoader {

    /**
     * @param bundle
     * @throws Exception
     */
    void loadBundle(Bundle bundle) throws Exception;

}
