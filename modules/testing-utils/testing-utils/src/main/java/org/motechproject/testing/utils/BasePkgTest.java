package org.motechproject.testing.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.junit.After;
import org.junit.Before;
import org.motechproject.commons.api.MotechException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * This serves as a base for all tests testing packages such as debs or RPMs in a chrooted environment.
 */
public abstract class BasePkgTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasePkgTest.class);

    private static final String ERROR_FILENAME = "err.log";
    private static final String MOTECH = "motech";

    private String script;
    private String chrootDir;
    private String buildDir;
    private String errorFile;

    @Before
    public void setUp() {
        // read passed properties
        buildDir = System.getProperty("buildDir");
        if (StringUtils.isBlank(buildDir)) {
            throw new MotechException("Build directory not defined");
        }

        chrootDir = System.getProperty(getChrootDirProp());
        if (StringUtils.isBlank(chrootDir)) {
            throw new MotechException("Chroot dir needs to be scpecified. Run with -DchrootDir=");
        }

        String tmpDir = System.getProperty("tmpDir");
        if (StringUtils.isBlank(tmpDir)) {
            tmpDir = "/tmp";
        }

        script = tmpDir + File.separatorChar + "motech-osi-it.sh";
        errorFile = buildDir + File.separatorChar + ERROR_FILENAME;
    }

    @After
    public void cleanUp() {
        if (script != null && (script.isEmpty())) {
            FileUtils.deleteQuietly(new File(script));
        }
    }

    /**
     * Returns the name of the system property denoting the chroot directory. This property will be used for
     * determining the directory of the chroot.
     * @return the name of the system property identifying the chroot directory
     */
    public abstract String getChrootDirProp();

    /**
     * Reads the script with the given name from the classpath and installs it into the temp directory.
     * @param name the name of this script on the classpath
     * @throws IOException if there was an error while copying the file
     */
    protected void installScript(String name) throws IOException {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(name)) {
            try (OutputStream os = new FileOutputStream(script)) {
                IOUtils.copy(is, os);
            }
        }

        File scriptFile = new File(script);
        scriptFile.setExecutable(true);
    }

    /**
     * Executes the given script as a process. The script is first installed into the temporary directory.
     * This method waits for the script to finish.
     * @param scriptName the name of the script from the classpath
     * @param passPorts whether to pass the test ports as arguments to the script
     * @param attrs the parameters passed to the script, this array will be directly passed to the process builder
     * @return the exit code of the script
     * @throws IOException if there was an error copying the script into the temp directory
     * @throws InterruptedException if we were interrupted while waiting for the script to finish
     */
    protected int runScript(String scriptName, boolean passPorts, String... attrs) throws IOException, InterruptedException {
        installScript(scriptName);

        String[] arguments = (String[]) ArrayUtils.addAll(new String[] {script, "-d", chrootDir, "-b", buildDir,
                "-e", errorFile }, attrs);

        if (passPorts) {
            arguments = (String[]) ArrayUtils.addAll(arguments, new String[] {
                    "-p", String.valueOf(TestContext.getPkgTestPort()),
                    "-t", String.valueOf(TestContext.getPkgTenantTestPort())});
        }


        ProcessBuilder pb = new ProcessBuilder(arguments)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT);
        Process proc = pb.start();
        proc.waitFor();

        return proc.exitValue();
    }

    /**
     * Reads errors from the error file in the build directory. Script errors should get logged into that file.
     * @return the contents of the error file as a string
     * @throws IOException if we were unable to read the file
     */
    protected String readErrors() throws IOException {
        File errors = new File(errorFile);
        return (errors.exists()) ? FileUtils.readFileToString(errors) : "";
    }

    /**
     * Logs in into the Motech instance available under the output of {@link #getBaseUrl} through HTTP (the login screen).
     * This method logs in with the credentials motech/motech.
     * @throws InterruptedException if we were interrupted while logging in through HTTP
     * @throws IOException if there were issues executing the HTTP request
     */
    protected void login() throws InterruptedException, IOException {
        PollingHttpClient defaultHttpClient = new PollingHttpClient();
        HttpPost request = new HttpPost(getBaseUrl() + "/server/j_spring_security_check");
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("j_username", MOTECH));
        nvps.add(new BasicNameValuePair("j_password", MOTECH));
        request.setEntity(new UrlEncodedFormEntity(nvps, "UTF8"));

        HttpResponse response = defaultHttpClient.execute(request);
        assertEquals(String.format("Location: %s/server/home", getBaseUrl()),
                response.getFirstHeader("Location").toString());
        assertFalse(response.getFirstHeader("Location").toString().contains("error=true"));
    }

    /**
     * Submits bootstrap data in the first page the user of Motech encounters. The Motech instance is expected
     * to be at the pre-bootstrap level and available at {@link #getBaseUrl()}. This bootstrap configuration
     * preps the instance for the test.
     * @throws IOException if there was an HTTP error
     * @throws InterruptedException if we were interrupted while submitting data through HTTP
     */
    protected void submitBootstrapData() throws IOException, InterruptedException {
        List<NameValuePair> nameValuePairs = new ArrayList<>();

        nameValuePairs.add(new BasicNameValuePair("configSource", "ui"));

        PollingHttpClient httpClient = new PollingHttpClient();
        HttpPost request = new HttpPost(getBaseUrl() + "/server/bootstrap.do");
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatusLine().getStatusCode());
    }

    /**
     * Submits the startup data on the second screen a new user of Motech sees. An admin user with
     * the credentials of motech/motech is created and repository mode is chosen as the security option.
     * This is done through HTTP.
     * @throws IOException if there was an HTTP error
     * @throws InterruptedException if we were interrupted while submitting data through HTTP
     */
    protected void submitStartupData() throws IOException, InterruptedException {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("queueUrl", "tcp://localhost:61616"));
        nameValuePairs.add(new BasicNameValuePair("schedulerUrl", "asd"));
        nameValuePairs.add(new BasicNameValuePair("loginMode", "repository"));
        nameValuePairs.add(new BasicNameValuePair("adminLogin", MOTECH));
        nameValuePairs.add(new BasicNameValuePair("adminPassword", MOTECH));
        nameValuePairs.add(new BasicNameValuePair("adminConfirmPassword", MOTECH));
        nameValuePairs.add(new BasicNameValuePair("adminEmail", "w@da.pl"));
        nameValuePairs.add(new BasicNameValuePair("providerName", ""));
        nameValuePairs.add(new BasicNameValuePair("providerUrl", ""));
        nameValuePairs.add(new BasicNameValuePair("language", "en"));

        PollingHttpClient httpClient = new PollingHttpClient();
        HttpPost request = new HttpPost(getBaseUrl() + "/server/startup.do");
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    /**
     * Runs the <b>test-install.sh</b> script that tests whether Motech was installed properly.
     * This script should be available on the classpath, it should install Motech and then make sure
     * it installed properly. It will be executed with root rights in the test chroot. It should log all errors
     * to its stderr and return an exit code different from zero if it wishes to fail the build.
     * @throws IOException if there were problems handling the script file
     * @throws InterruptedException if we were interrupted while waiting for the script to finish
     */
    protected void testInstall() throws IOException, InterruptedException {
        int retVal = runScript("test-install.sh", true);
        if (retVal != 0) {
            LOGGER.error("Error log: " + readErrors());
        }
        assertEquals("Install: Non-zero exit code returned", 0, retVal);
    }

    /**
     * Runs the <b>test-uninstall.sh</b> script that tests whether Motech was installed properly.
     * This script should be available on the classpath, it should remove Motech and then make sure
     * it was uninstalled properly. It will be executed with root rights in the test chroot. It should log all errors
     * to its stderr and return an exit code different from zero if it wishes to fail the build.
     * @throws IOException if there were problems handling the script file
     * @throws InterruptedException if we were interrupted while waiting for the script to finish
     */
    protected void testUninstall() throws IOException, InterruptedException {
        int retVal = runScript("test-uninstall.sh", false);
        if (retVal != 0) {
            LOGGER.error("Error log: " + readErrors());
        }
        assertEquals("Uninstall: Non-zero exit code returned", 0, retVal);
    }

    /**
     * Returns the base HTTP url to the Motech instance being tested.
     * @return the base url to the Motech instance
     */
    protected String getBaseUrl() {
        return String.format("http://localhost:%d", TestContext.getPkgTestPort());
    }

    /**
     * Returns the base HTTP url to the Motech tenant instance being tested.
     * @return the base url to the Motech tenant instance
     */
    protected String getTenantBaseUrl() {
        return String.format("http://localhost:%d", TestContext.getPkgTenantTestPort());
    }

    /**
     * Returns an SLF4J logger instantiated per instance of this class.
     * @return the SLF$J logger
     */
    protected static Logger getLogger() {
        return LOGGER;
    }
}
