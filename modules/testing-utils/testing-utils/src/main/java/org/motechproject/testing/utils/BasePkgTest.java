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

public abstract class BasePkgTest {

    private static final Logger LOG = LoggerFactory.getLogger(BasePkgTest.class);

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
        if (script != null && (script.isEmpty()) ) {
            FileUtils.deleteQuietly(new File(script));
        }
    }


    public abstract String getChrootDirProp();


    protected void installScript(String name) throws IOException {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(name)) {
            try (OutputStream os = new FileOutputStream(script)) {
                IOUtils.copy(is, os);
            }
        }

        File scriptFile = new File(script);
        scriptFile.setExecutable(true);
    }

    protected int runScript(String scriptName, boolean passPorts, String... attrs) throws IOException, InterruptedException {
        installScript(scriptName);

        String[] arguments = (String[]) ArrayUtils.addAll(new String[] { script, "-d", chrootDir, "-b", buildDir,
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

    protected String readErrors() throws IOException {
        File errors = new File(errorFile);
        return (errors.exists()) ? FileUtils.readFileToString(errors) : "";
    }

    protected void login() throws InterruptedException, IOException {
        PollingHttpClient defaultHttpClient = new PollingHttpClient();
        HttpPost request = new HttpPost(getBaseUrl() + "/module/server/j_spring_security_check");
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("j_username", MOTECH));
        nvps.add(new BasicNameValuePair("j_password", MOTECH));
        request.setEntity(new UrlEncodedFormEntity(nvps, "UTF8"));

        HttpResponse response = defaultHttpClient.execute(request);
        assertEquals(String.format("Location: %s/module/server/home", getBaseUrl()),
                response.getFirstHeader("Location").toString());
        assertFalse(response.getFirstHeader("Location").toString().contains("error=true"));
    }

    protected void submitBootstrapData() throws IOException, InterruptedException {
        List<NameValuePair> nameValuePairs = new ArrayList<>();

        nameValuePairs.add(new BasicNameValuePair("dbUrl", "http://localhost:5984"));
        nameValuePairs.add(new BasicNameValuePair("configSource", "ui"));

        PollingHttpClient httpClient = new PollingHttpClient();
        HttpPost request = new HttpPost(getBaseUrl() + "/module/server/bootstrap.do");
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

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
        HttpPost request = new HttpPost(getBaseUrl() + "/module/server/startup.do");
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    protected void testInstall() throws IOException, InterruptedException {
        int retVal = runScript("test-install.sh", true);
        if (retVal != 0) {
            LOG.error("Error log: " + readErrors());
        }
        assertEquals("Install: Non-zero exit code returned", 0, retVal);
    }

    protected void testUninstall() throws IOException, InterruptedException {
        int retVal = runScript("test-uninstall.sh", false);
        if (retVal != 0) {
            LOG.error("Error log: " + readErrors());
        }
        assertEquals("Uninstall: Non-zero exit code returned", 0, retVal);
    }

    protected String getBaseUrl() {
        return String.format("http://localhost:%d", TestContext.getPkgTestPort());
    }

    protected String getTenantBaseUrl() {
        return String.format("http://localhost:%d", TestContext.getPkgTenantTestPort());
    }

    protected static Logger getLogger() {
        return LOG;
    }
}
