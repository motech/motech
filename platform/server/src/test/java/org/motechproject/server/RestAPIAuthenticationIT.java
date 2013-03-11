package org.motechproject.server;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class RestAPIAuthenticationIT {


    @Test
    public void testThatItShouldAllowRestApiAccessAfterFormAuthentication() throws IOException, JSONException, InterruptedException {

        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.setCookieStore(new BasicCookieStore());


        HttpGet statusRequest = new HttpGet("http://localhost:9090/motech-platform-server/module/admin/api/web-api/status");
        HttpResponse response = httpClient.execute(statusRequest);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());

        EntityUtils.consume(response.getEntity());

        login(httpClient);

        HttpResponse statusResponse = httpClient.execute(statusRequest);
        assertEquals(HttpStatus.SC_OK, statusResponse.getStatusLine().getStatusCode());
    }


    private void login(DefaultHttpClient defaultHttpClient) throws IOException {
        final HttpPost loginPost = new HttpPost("http://localhost:9090/motech-platform-server/module/server/motech-platform-server/j_spring_security_check");

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("j_username", "motech"));
        nvps.add(new BasicNameValuePair("j_password", "motech"));

        loginPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF8"));

        final HttpResponse response = defaultHttpClient.execute(loginPost);
        EntityUtils.consume(response.getEntity());
    }
}
