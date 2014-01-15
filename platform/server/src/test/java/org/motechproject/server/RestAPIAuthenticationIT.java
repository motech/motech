package org.motechproject.server;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.hamcrest.core.Is;
import org.json.JSONException;
import org.junit.Test;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class RestAPIAuthenticationIT {

    private static final String HOST = "localhost";
    private static final int PORT = TestContext.getTomcatPort();

    @Test
    public void testThatItShouldAllowRestApiAccessAfterFormAuthentication() throws IOException, JSONException, InterruptedException {

        PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 120);
        httpClient.setCookieStore(new BasicCookieStore());


        HttpGet statusRequest =
                new HttpGet(String.format("http://%s:%d/motech-platform-server/module/server/web-api/status", HOST, PORT));

        HttpResponse response = httpClient.execute(statusRequest);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());

        Header authenticateHeader = response.getFirstHeader(HttpHeaders.WWW_AUTHENTICATE);
        assertNotNull(authenticateHeader);

        String authenticateHeaderValue = authenticateHeader.getValue();
        assertThat(authenticateHeaderValue, Is.is("Basic realm=\"MOTECH\""));

        EntityUtils.consume(response.getEntity());

        login(httpClient);

        HttpResponse statusResponse = httpClient.execute(statusRequest);
        assertEquals(HttpStatus.SC_OK, statusResponse.getStatusLine().getStatusCode());
    }


    private void login(PollingHttpClient httpClient) throws IOException, InterruptedException {
        final HttpPost loginPost =
                new HttpPost(String.format("http://%s:%d/motech-platform-server/module/server/motech-platform-server/j_spring_security_check", HOST, PORT));

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("j_username", "motech"));
        nvps.add(new BasicNameValuePair("j_password", "motech"));

        loginPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF8"));

        final HttpResponse response = httpClient.execute(loginPost);
        EntityUtils.consume(response.getEntity());
    }
}
