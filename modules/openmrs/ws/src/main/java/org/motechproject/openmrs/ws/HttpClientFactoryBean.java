package org.motechproject.openmrs.ws;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.FactoryBean;

/**
 * Factory bean to create an HTTP client with BASIC authentication
 */
public class HttpClientFactoryBean implements FactoryBean<HttpClient> {

    private static final String OPENMRS_PASSWORD = "openmrs.password";
    private static final String OPENMRS_USER = "openmrs.user";
    private String user;
    private String password;
    private HttpClient httpClient;
    private SettingsFacade settingsFacade;

    public HttpClientFactoryBean(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    public void readSettings() {
        user = settingsFacade.getProperty(OPENMRS_USER);
        password = settingsFacade.getProperty(OPENMRS_PASSWORD);
    }

    @Override
    public HttpClient getObject() throws Exception {
        if (httpClient == null) {
            initializeHttpClient();
        }

        return httpClient;
    }

    private void initializeHttpClient() {
        httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        httpClient.getState().setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(user, password));
    }

    @Override
    public Class<?> getObjectType() {
        return HttpClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
