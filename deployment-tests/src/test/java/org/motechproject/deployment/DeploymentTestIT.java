package org.motechproject.deployment;

import org.junit.Test;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class DeploymentTestIT {
    @Test
    public void shouldNotFoundExceptionsDuringStartup() throws Exception {
        for(File log : getFileLogs()) {
            assertTrue(String.format("File not found: %s.", log.getAbsolutePath()), log.exists());
            assertTrue("File cannot be read", log.canRead());
            assertTrue(String.format("Errors appeared during MoTeCH project startup. Please check %s", log.getPath()), log.length() == 0);
        }
    }

    private File[] getFileLogs() {
        File[] logs = new File[2];
        StringBuilder currentDir = new StringBuilder(System.getProperty("user.dir"));
        Pattern pattern = Pattern.compile("(.*/)(deployment-tests)(/)?$");
        Matcher matcher = pattern.matcher(currentDir);

        if (matcher.matches() && matcher.group(3) == null) {
            currentDir.append('/');
        } else {
            if (currentDir.charAt(currentDir.length() - 1) != '/') {
                currentDir.append('/');
            }

            currentDir.append("deployment-tests/");
        }

        currentDir.append("target/tomcat/logs/");

        logs[0] = new File(currentDir.toString() + "catalina.out");
        logs[1] = new File(currentDir.toString() + "Motech.log");

        return logs;
    }

}
