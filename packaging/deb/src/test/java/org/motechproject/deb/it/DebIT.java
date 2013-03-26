package org.motechproject.deb.it;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.motechproject.testing.utils.BasePkgTest;
import org.motechproject.testing.utils.PollingHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class DebIT extends BasePkgTest {

    private static final Logger LOG = LoggerFactory.getLogger(DebIT.class);

    @Test
    public void testMotechDebInstallation() throws IOException, InterruptedException {
        testInstall();
        testLoginAndMainPage();
        cleanUp();
        testUninstall();
    }

    private void testInstall() throws IOException, InterruptedException {
        int retVal = runScript("test-install.sh");
        if (retVal != 0) {
            LOG.error("Error log: " + readErrors());
        }
        assertEquals("Non-zero exit code returned", 0, retVal);
    }

    private void testLoginAndMainPage() throws InterruptedException, IOException {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("queueUrl", "tcp://localhost:61616"));
        nameValuePairs.add(new BasicNameValuePair("loginMode", "repository"));
        nameValuePairs.add(new BasicNameValuePair("adminLogin", "motech"));
        nameValuePairs.add(new BasicNameValuePair("adminPassword", "motech"));
        nameValuePairs.add(new BasicNameValuePair("adminConfirmPassword", "motech"));
        nameValuePairs.add(new BasicNameValuePair("adminEmail", "w@da.pl"));

        PollingHttpClient httpClient = new PollingHttpClient();
        HttpPost request = new HttpPost("http://localhost:8099/module/server/startup.do");
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        login();
    }

    private void testUninstall() throws IOException, InterruptedException {
        int retVal = runScript("test-uninstall.sh");
        if (retVal != 0) {
            LOG.error("Error log: " + readErrors());
        }
        assertEquals("Non-zero exit code returned", 0, retVal);
    }

    private void login() throws InterruptedException, IOException {
        PollingHttpClient defaultHttpClient = new PollingHttpClient();
        HttpPost request = new HttpPost("http://localhost:8099/module/server/j_spring_security_check");
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("j_username", "motech"));
        nvps.add(new BasicNameValuePair("j_password", "motech"));
        request.setEntity(new UrlEncodedFormEntity(nvps, "UTF8"));

        HttpResponse response = defaultHttpClient.execute(request);
        assertEquals("Location: http://localhost:8099/module/server/home", response.getFirstHeader("Location").toString());
        assertFalse(response.getFirstHeader("Location").toString().contains("error=true"));
    }

    @Override
    public String getChrootDirProp() {
        return "debChrootDir";
    }
}
