package org.motechproject.openmrs.rest;

import java.net.URI;

public interface RestClient {

    String getJson(URI uri) throws HttpException;

    String postForJson(URI url, String json) throws HttpException;

    void postWithEmptyResponseBody(URI url, String json) throws HttpException;

    void delete(URI uri) throws HttpException;
}
