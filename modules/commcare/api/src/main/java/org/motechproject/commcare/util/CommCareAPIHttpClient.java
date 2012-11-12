package org.motechproject.commcare.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.motechproject.commcare.exception.CaseParserException;
import org.motechproject.commcare.parser.OpenRosaResponseParser;
import org.motechproject.commcare.response.OpenRosaResponse;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Component
public class CommCareAPIHttpClient {
    private static final String COMMCARE_USER_API_FILE_NAME = "commcareUserApi.properties";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private HttpClient commonsHttpClient;
    private SettingsFacade settingsFacade;

    @Autowired
    public CommCareAPIHttpClient(final HttpClient commonsHttpClient, final SettingsFacade settingsFacade) {
        this.commonsHttpClient = commonsHttpClient;
        this.settingsFacade = settingsFacade;
    }

    public OpenRosaResponse caseUploadRequest(String caseXml)
            throws CaseParserException {
        return this.postRequest(commcareCaseUploadUrl(), caseXml);
    }

    public String usersRequest() {
        return this.getRequest(commcareUserUrl(), null);
    }

    public String formRequest(String formId) {
        return this.getRequest(commcareFormUrl(formId), null);
    }

    public String casesRequest(NameValuePair[] queryParams) {
        return this.getRequest(baseCommcareUrl(), queryParams);
    }

    private HttpMethod buildRequest(String url, NameValuePair[] queryParams) {
        HttpMethod requestMethod = new GetMethod(url);

        authenticate();

        if (queryParams != null) {
            requestMethod.setQueryString(queryParams);
        }

        return requestMethod;
    }

    private String getRequest(String requestUrl, NameValuePair[] queryParams) {

        HttpMethod getMethod = buildRequest(requestUrl, queryParams);

        String response = null;

        try {
            commonsHttpClient.executeMethod(getMethod);
            response = getMethod.getResponseBodyAsString();
        } catch (HttpException e) {
            logger.warn("HttpException while sending request to CommCare: " + e.getMessage());
            return null;
        } catch (IOException e) {
            logger.warn("IOException while sending request to CommCare: " + e.getMessage());
            return null;
        }

        return response;
    }

    private void authenticate() {
        commonsHttpClient.getParams().setAuthenticationPreemptive(true);

        commonsHttpClient.getState().setCredentials(
                new AuthScope(null, -1, null, null),
                new UsernamePasswordCredentials(getUsername(), getPassword()));
    }

    private OpenRosaResponse postRequest(String requestUrl, String body)
            throws CaseParserException {

        PostMethod postMethod = new PostMethod(requestUrl);

        StringRequestEntity stringEntity = null;

        try {
            stringEntity = new StringRequestEntity(body, "text/xml",
                    "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            logger.warn("UnsupportedEncodingException, this should not occur: " + e.getMessage()); //This exception cannot happen here
        }

        postMethod.setRequestEntity(stringEntity);

        authenticate();

        String response = "";

        int status = 0;

        try {
            status = commonsHttpClient.executeMethod(postMethod);
            response = postMethod.getResponseBodyAsString();
        } catch (HttpException e) {
            logger.warn("HttpException while posting case xml to CommCareHQ: " + e.getMessage());
        } catch (IOException e) {
            logger.warn("IOException while posting case xml to CommcareHQ: " + e.getMessage());
        }

        OpenRosaResponseParser responseParser = new OpenRosaResponseParser();

        OpenRosaResponse openRosaResponse = responseParser
                .parseResponse(response);

        if (openRosaResponse == null) {
            openRosaResponse = new OpenRosaResponse();
        }

        openRosaResponse.setStatus(status);

        return openRosaResponse;

    }

    private String commcareUserUrl() {
        return String.format("%s/%s/api/v0.1/user/?format=json", getCommcareBaseUrl(), getCommcareDomain());
    }

    private String commcareFormUrl(String formId) {
        return String.format("%s/%s/api/v0.1/form/%s/?format=json", getCommcareBaseUrl(), getCommcareDomain(), formId);
    }

    private String baseCommcareUrl() {
        return String.format("%s/%s/cloudcare/api/cases/", getCommcareBaseUrl(), getCommcareDomain());
    }

    private String commcareCaseUploadUrl() {
        return String.format("%s/%s/receiver/", getCommcareBaseUrl(), getCommcareDomain());
    }

    private String getCommcareBaseUrl() {
        String commcareBaseUrl = settingsFacade.getProperties(COMMCARE_USER_API_FILE_NAME).getProperty("commcareBaseUrl");

        if (commcareBaseUrl.endsWith("/")) {
            commcareBaseUrl = commcareBaseUrl.substring(0, commcareBaseUrl.length() - 1);
        }

        return commcareBaseUrl;
    }

    private String getCommcareDomain() {
        return settingsFacade.getProperties(COMMCARE_USER_API_FILE_NAME).getProperty("commcareDomain");
    }

    private String getUsername() {
        return settingsFacade.getProperties(COMMCARE_USER_API_FILE_NAME).getProperty("username");
    }

    private String getPassword() {
        return settingsFacade.getProperties(COMMCARE_USER_API_FILE_NAME).getProperty("password");
    }
}
