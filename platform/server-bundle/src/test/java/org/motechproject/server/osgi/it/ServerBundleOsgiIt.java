package org.motechproject.server.osgi.it;

import org.eclipse.gemini.blueprint.test.platform.Platforms;
import org.motechproject.testing.osgi.BaseOsgiIT;

public class ServerBundleOsgiIt extends BaseOsgiIT{
    public void testStartServer() {
        System.out.print("test");
    }

    @Override
    protected String getPlatformName() {
        return Platforms.EQUINOX;
    }
}
