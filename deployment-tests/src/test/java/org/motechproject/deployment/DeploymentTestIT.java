package org.motechproject.deployment;

import org.junit.Test;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeploymentTestIT {
    @Test
    public void shouldNotFoundExceptionsDuringStartup() throws Exception {
        for(File log : getFileLogs()) {
            assertTrue(String.format("File not found: %s.", log.getAbsolutePath()), log.exists());
            assertTrue("File cannot be read", log.canRead());
            assertTrue(String.format("Errors appeared during MOTECH project startup. Please check %s", log.getPath()), log.length() == 0);
        }
    }

    private File[] getFileLogs() {
        File[] logs = new File[2];

        String currentDir = new File(".").getAbsolutePath();
        if (!currentDir.contains("deployment-tests")) {
            currentDir += "/deployment-tests/";
        }

        currentDir += "/target/tomcat/logs/";

        logs[0] = new File(currentDir + "catalina.out");
        logs[1] = new File(currentDir + "Motech.log");

        return logs;
    }

}
