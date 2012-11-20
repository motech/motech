package org.motechproject.rpm.it;

import org.junit.Test;
import org.motechproject.testing.utils.BasePkgTest;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RpmIT extends BasePkgTest {

    @Test
    public void testInstall() throws IOException, InterruptedException {
        int retVal = runScript("test-install.sh");
        assertEquals("Script returned a non-zero exit code", 0, retVal);
    }
}
