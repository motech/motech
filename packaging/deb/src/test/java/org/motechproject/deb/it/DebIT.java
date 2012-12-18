package org.motechproject.deb.it;

import org.junit.Test;
import org.motechproject.testing.utils.BasePkgTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class DebIT extends BasePkgTest {

    private static final Logger LOG = LoggerFactory.getLogger(DebIT.class);

    @Test
    public void testInstall() throws IOException, InterruptedException {
        int retVal = runScript("test-install.sh");
        if (retVal != 0) {
            LOG.error("Error log: " + readErrors());
        }
        assertEquals("Non-zero exit code returned", 0, retVal);
    }

    @Override
    public String getChrootDirProp() {
        return "debChrootDir";
    }
}
