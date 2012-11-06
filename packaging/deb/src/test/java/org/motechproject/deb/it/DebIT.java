package org.motechproject.deb.it;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static junit.framework.Assert.assertEquals;

public class DebIT {

    private static String chrootDir;
    private static String tmpDir = "/tmp";
    private static String script = "/tmp/motech-osi.it.sh";
    private static String buildDir;

    @BeforeClass
    public static void setUp() {
        // read passed properties
        buildDir = System.getProperty("buildDir");
        if (StringUtils.isBlank(buildDir)) {
            throw new RuntimeException("Build directory not defined");
        }

        chrootDir = System.getProperty("chrootDir");
        if (StringUtils.isBlank(chrootDir)) {
            throw new RuntimeException("Chroot dir needs to be scpecified. Run with -DchrootDir=");
        }

        tmpDir = System.getProperty("tmpDir");
        if (StringUtils.isBlank(tmpDir)) {
            tmpDir = "/tmp";
        }

        script = tmpDir + File.separatorChar + "motech-osi-it.sh";
    }

    @Test
    public void testInstall() throws IOException, InterruptedException {
        installScript("test-install.sh");

        ProcessBuilder pb = new ProcessBuilder(script, "-d", chrootDir, "-b", buildDir)
                            .redirectError(ProcessBuilder.Redirect.INHERIT)
                            .redirectOutput(ProcessBuilder.Redirect.INHERIT);
        Process proc = pb.start();
        proc.waitFor();

        assertEquals("Script test-install.sh exited with non-zero exit code", 0, proc.exitValue());
    }

    private void installScript(String name) throws IOException {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(name)) {
            try (OutputStream os = new FileOutputStream(script)) {
                IOUtils.copy(is, os);
            }
        }

        File scriptFile = new File(script);
        scriptFile.setExecutable(true);
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(new File(script));
    }
}
