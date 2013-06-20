package org.motechproject.commcare.util;

import org.apache.commons.httpclient.HttpClient;
import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.config.SettingsFacade;

import java.util.Properties;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommCareAPIHttpClientTest {
    @Mock
    private HttpClient httpClient;

    @Mock
    private SettingsFacade settingsFacade;

    final String baseUrl = "https://www.commcarehq.org/a";
    final String domain = "somedomain";
    final String apiVersion = "0.4";
    private CommCareAPIHttpClient commCareAPIHttpClient;

    @Before
    public void setUp() {
        initMocks(this);
        Properties properties = new Properties();
        properties.put("commcareBaseUrl", baseUrl);
        properties.put("commcareDomain", domain);
        properties.put("apiVersion", apiVersion);
        when(settingsFacade.getProperties(CommCareAPIHttpClient.COMMCARE_USER_API_FILE_NAME)).thenReturn(properties);

        commCareAPIHttpClient = new CommCareAPIHttpClient(httpClient, settingsFacade);
    }

    @Test
    public void shouldConstructCommcareUserUrl() {
        assertThat(commCareAPIHttpClient.commcareUserUrl(), IsEqual.equalTo(String.format("%s/%s/api/v%s/user/?format=json", baseUrl, domain, apiVersion)));
    }

    @Test
    public void shouldConstructCommcareFormUrl() {
        final String formId = "123";
        assertThat(commCareAPIHttpClient.commcareFormUrl(formId), IsEqual.equalTo(String.format("%s/%s/api/v%s/form/%s/?format=json", baseUrl, domain, apiVersion, formId)));
    }

    @Test
    public void shouldConstructCommcareFixturesUrl() {
        assertThat(commCareAPIHttpClient.commcareFixturesUrl(), IsEqual.equalTo(String.format("%s/%s/api/v%s/fixture/", baseUrl, domain, apiVersion)));
    }

    @Test
    public void shouldConstructCommcareFixtureUrl() {
        String fixtureId = "123";
        assertThat(commCareAPIHttpClient.commcareFixtureUrl(fixtureId), IsEqual.equalTo(String.format("%s/%s/api/v%s/fixture/%s/", baseUrl, domain, apiVersion, fixtureId)));
    }

    @Test
    public void shouldConstructCommcareCasesUrl() {
        assertThat(commCareAPIHttpClient.commcareCasesUrl(), IsEqual.equalTo(String.format("%s/%s/api/v%s/case/", baseUrl, domain, apiVersion)));
    }

    @Test
    public void shouldConstructCommcareCaseUrl() {
        String caseId = "123";
        assertThat(commCareAPIHttpClient.commcareCaseUrl(caseId), IsEqual.equalTo(String.format("%s/%s/api/v%s/case/%s/", baseUrl, domain, apiVersion, caseId)));
    }

    @Test
    public void shouldConstructCommcareCaseUploadUrl() {
        assertThat(commCareAPIHttpClient.commcareCaseUploadUrl(), IsEqual.equalTo(String.format("%s/%s/receiver/", baseUrl, domain, apiVersion)));
    }
}
