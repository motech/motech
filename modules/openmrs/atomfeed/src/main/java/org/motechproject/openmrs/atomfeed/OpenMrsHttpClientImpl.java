package org.motechproject.openmrs.atomfeed;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.motechproject.MotechException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OpenMrsHttpClientImpl implements OpenMrsHttpClient {
    private static final Logger LOGGER = Logger.getLogger(OpenMrsHttpClientImpl.class);
    private static final String ATOM_FEED_MODULE_PATH = "/moduleServlet/atomfeed/atomfeed";

    private final HttpClient httpClient;
    private final String openmrsPath;

    @Autowired
    public OpenMrsHttpClientImpl(@Value("${openmrs.url}") String openmrsUrl) throws URIException {
        Validate.notEmpty(
                openmrsUrl,
                "Did not find property for OpenMRS Url (openmrs.url). Cannot use the Motech Atom Feed module until this property is set");
        httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        URI uri = new URI(openmrsUrl, false);
        openmrsPath = uri.getPath();
        httpClient.getHostConfiguration().setHost(uri);
    }

    @Override
    public String getOpenMrsAtomFeed() {
        GetMethod get = new GetMethod(openmrsPath + ATOM_FEED_MODULE_PATH);
        return executeGetMethod(get);
    }

    private String executeGetMethod(GetMethod get) {
        LOGGER.debug("Making HTTP request to fetch OpenMRS Atom Feed: "
                + httpClient.getHostConfiguration().getHostURL() + get.getPath() + get.getQueryString());
        try {
            int responseCode = httpClient.executeMethod(get);
            if (responseCode == HttpStatus.SC_OK) {
                LOGGER.debug("Successfully made HTTP request to OpenMRS");
                return get.getResponseBodyAsString();
            } else {
                LOGGER.warn("OpenMRS Atom Feed module returned non 200 status: " + get.getStatusCode());
                return "";
            }
        } catch (IOException e) {
            LOGGER.error("Motech OpenMRS Atom Feed module could not communicate with the OpenMRS");
            throw new MotechException(e.getMessage());
        }
    }

    @Override
    public String getOpenMrsAtomFeedSinceDate(String lastUpdateTime) {
        GetMethod get = new GetMethod(openmrsPath + ATOM_FEED_MODULE_PATH);
        NameValuePair[] params = new NameValuePair[1];
        params[0] = new NameValuePair("asOfDate", lastUpdateTime);
        get.setQueryString(params);

        return executeGetMethod(get);
    }
}
