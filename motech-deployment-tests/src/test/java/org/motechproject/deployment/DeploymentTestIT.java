package org.motechproject.deployment;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeploymentTestIT {
    private static final String EXCEPTION_PATTERN = "^([A-Za-z]+\\.)+[A-Za-z]+(Exception|Error):";
    private static final String LOG_FILE;

    static {
        StringBuilder currentDir = new StringBuilder(System.getProperty("user.dir"));
        Pattern pattern = Pattern.compile("(.*/)(motech-deployment-tests)(/)?$");
        Matcher matcher = pattern.matcher(currentDir);

        if (matcher.matches() && matcher.group(3) == null) {
            currentDir.append('/');
        } else {
            if (currentDir.charAt(currentDir.length() - 1) != '/') {
                currentDir.append('/');
            }

            currentDir.append("motech-deployment-tests/");
        }

        currentDir.append("target/tomcat/logs/catalina.out");

        LOG_FILE = currentDir.toString();
    }

    @Test
    public void shouldNotFoundExceptionsDuringStartup() throws Exception {
        List<String> errors = new ArrayList<>();
        File file = new File(LOG_FILE);

        assertTrue(String.format("File not found: %s.", file.getAbsolutePath()), file.exists());

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Pattern pattern = Pattern.compile(EXCEPTION_PATTERN);
            String currentLine = "";

            while ((currentLine = reader.readLine()) != null) {
                if (pattern.matcher(currentLine).find()) {
                    errors.add(currentLine);
                }
            }
        }

        assertEquals("Errors appeared during MoTeCH project startup: " + createErrorsList(errors), 0, errors.size());
    }

    private String createErrorsList(List<String> errors) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < errors.size(); ++i) {
            sb.append("[ ").append(errors.get(i)).append(" ]");

            if (i < errors.size() - 1) {
                sb.append(" ; ");
            }
        }

        return sb.toString();
    }

}
