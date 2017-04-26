package org.motechproject.server.it;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.hamcrest.core.Is;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.testing.tomcat.BaseTomcatIT;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class RestAPIAuthenticationIT extends BaseTomcatIT {

    @Before
    public void setUp() throws Exception {
        waitForTomcat();
        createAdminUser();
        logout();
    }

    @Test
    public void testThatItShouldAllowRestApiAccessAfterFormAuthentication() throws IOException, JSONException,
            InterruptedException {

        HttpGet statusRequest =
                new HttpGet(String.format("http://%s:%d/motech-platform-server/server/web-api/status", HOST, PORT));

        HttpResponse response = HTTP_CLIENT.execute(statusRequest, HttpStatus.SC_UNAUTHORIZED);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());

        Header authenticateHeader = response.getFirstHeader(HttpHeaders.WWW_AUTHENTICATE);
        assertNotNull(authenticateHeader);

        String authenticateHeaderValue = authenticateHeader.getValue();
        assertThat(authenticateHeaderValue, Is.is("Basic realm=\"MOTECH\""));

        EntityUtils.consume(response.getEntity());

        login();

        HttpResponse statusResponse = HTTP_CLIENT.execute(statusRequest);
        assertEquals(HttpStatus.SC_OK, statusResponse.getStatusLine().getStatusCode());
    }
}
