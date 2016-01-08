package org.motechproject.rpm.it;

import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.testing.utils.BasePkgTest;

import java.io.IOException;

public class RpmIT extends BasePkgTest {

    @Ignore
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
