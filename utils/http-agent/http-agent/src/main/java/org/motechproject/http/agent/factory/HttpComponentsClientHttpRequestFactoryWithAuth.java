package org.motechproject.http.agent.factory;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.net.URI;

/**
 * A factory used by a RestTemplate. This factory provides authentication details
 * per request.
 */
public class HttpComponentsClientHttpRequestFactoryWithAuth extends HttpComponentsClientHttpRequestFactory {

    private final String username;
    private final String password;

    public HttpComponentsClientHttpRequestFactoryWithAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        return createHttpContext();
    }

    private HttpContext createHttpContext() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        BasicHttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.CREDS_PROVIDER, credentialsProvider);

        return localContext;
    }
}
