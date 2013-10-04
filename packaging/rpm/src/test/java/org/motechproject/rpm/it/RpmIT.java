package org.motechproject.rpm.it;

import org.junit.Test;
import org.motechproject.testing.utils.BasePkgTest;

import java.io.IOException;

public class RpmIT extends BasePkgTest {

    @Test
    public void testMotechRpmInstallation() throws IOException, InterruptedException {
        testInstall();
        submitBootstrapData();
        submitStartupData();
        login();
        cleanUp();
        testUninstall();
    }

    @Override
    public String getChrootDirProp() {
        return "rpmChrootDir";
    }
}
