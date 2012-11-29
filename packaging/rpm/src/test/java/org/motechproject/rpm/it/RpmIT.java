package org.motechproject.rpm.it;

import org.junit.Test;
import org.motechproject.testing.utils.BasePkgTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RpmIT extends BasePkgTest {

    private static final Logger LOG = LoggerFactory.getLogger(RpmIT.class);

    @Test
    public void testInstall() throws IOException, InterruptedException {
        int retVal = runScript("test-install.sh");
        if (retVal != 0) {
            LOG.error("Error log: " + readErrors());
        }
        assertEquals("Script returned a non-zero exit code", 0, retVal);
    }

    @Override
    public String getChrootDirProp() {
        return "rpmChrootDir";
    }
}
