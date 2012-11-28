package org.motechproject.testing.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.motechproject.commons.api.MotechException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class BasePkgTest {

    private static final String ERROR_FILENAME = "err.log";

    protected String script;
    protected String chrootDir;
    protected String tmpDir = "/tmp";
    protected String buildDir;
    protected String errorFile;

    @Before
    public void setUp() {
        // read passed properties
        buildDir = System.getProperty("buildDir");
        if (StringUtils.isBlank(buildDir)) {
            throw new MotechException("Build directory not defined");
        }

        chrootDir = System.getProperty("chrootDir");
        if (StringUtils.isBlank(chrootDir)) {
            throw new MotechException("Chroot dir needs to be scpecified. Run with -DchrootDir=");
        }

        tmpDir = System.getProperty("tmpDir");
        if (StringUtils.isBlank(tmpDir)) {
            tmpDir = "/tmp";
        }

        script = tmpDir + File.separatorChar + "motech-osi-it.sh";
        errorFile = buildDir + File.separatorChar + ERROR_FILENAME;
    }

    protected void installScript(String name) throws IOException {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(name)) {
            try (OutputStream os = new FileOutputStream(script)) {
                IOUtils.copy(is, os);
            }
        }

        File scriptFile = new File(script);
        scriptFile.setExecutable(true);
    }

    protected int runScript(String scriptName, String... attrs) throws IOException, InterruptedException {
        installScript(scriptName);

        String[] arguments = (String[]) ArrayUtils.addAll(new String[] { script, "-d", chrootDir, "-b", buildDir,
                                                                        "-e", errorFile }, attrs);

        ProcessBuilder pb = new ProcessBuilder(arguments)
                                .redirectError(ProcessBuilder.Redirect.INHERIT)
                                .redirectOutput(ProcessBuilder.Redirect.INHERIT);
        Process proc = pb.start();
        proc.waitFor();

        return proc.exitValue();
    }

    protected String readErrors() throws IOException {
        File errors = new File(errorFile);
        return (errors.exists()) ? FileUtils.readFileToString(errors) : "";
    }

    @After
    public void cleanUp() {
        FileUtils.deleteQuietly(new File(script));
    }
}
