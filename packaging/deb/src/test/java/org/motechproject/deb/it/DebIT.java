package org.motechproject.deb.it;

import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.testing.utils.BasePkgTest;

import java.io.IOException;

public class DebIT extends BasePkgTest {

    @Ignore
    @Test
    public void testMotechDebInstallation() throws IOException, InterruptedException {
        testInstall();
        submitBootstrapData();
        submitStartupData();
        login();
        cleanUp();
        testUninstall();
    }

    @Override
    public String getChrootDirProp() {
        return "debChrootDir";
    }
}
