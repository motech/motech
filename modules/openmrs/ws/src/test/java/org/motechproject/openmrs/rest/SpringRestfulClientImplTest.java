package org.motechproject.openmrs.rest;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.openmrs.rest.impl.SpringRestfulClientImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;

public class SpringRestfulClientImplTest {

    @Mock
    private RestOperations restOperations;

    private SpringRestfulClientImpl impl;
    private URI uri;

    public SpringRestfulClientImplTest() throws URISyntaxException {
        uri = new URI("http://www.openmrs.org");
    }

    @Before
    public void setUp() {
        initMocks(this);
        impl = new SpringRestfulClientImpl(restOperations);
    }

    @Test(expected = HttpException.class)
    public void shouldThrowHttpExceptionOnGetClientError() throws HttpException {
        when(restOperations.getForEntity(eq(uri), eq(String.class))).thenThrow(
                new HttpClientErrorException(HttpStatus.NOT_FOUND));

        impl.getJson(uri);
    }

    @Test(expected = HttpException.class)
    public void shouldThrowHttpExceptionOnGetServerError() throws HttpException {
        when(restOperations.getForEntity(eq(uri), eq(String.class))).thenThrow(
                new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        impl.getJson(uri);
    }

    @Test(expected = HttpException.class)
    public void shouldThrowHttpExceptionOnGetResourceAccessException() throws HttpException {
        when(restOperations.getForEntity(eq(uri), eq(String.class))).thenThrow(new ResourceAccessException("IO Error"));

        impl.getJson(uri);
    }

    @Test(expected = HttpException.class)
    public void shouldThrowHttpExceptionOnPostClientError() throws HttpException {
        when(restOperations.postForEntity(eq(uri), anyObject(), eq(String.class))).thenThrow(
                new HttpClientErrorException(HttpStatus.NOT_FOUND));

        impl.postForJson(uri, "");
    }

    @Test(expected = HttpException.class)
    public void shouldThrowHttpExceptionOnPostServerError() throws HttpException {
        when(restOperations.postForEntity(eq(uri), anyObject(), eq(String.class))).thenThrow(
                new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        impl.postForJson(uri, "");
    }

    @Test(expected = HttpException.class)
    public void shouldThrowHttpExceptionOnPostResourceAccessException() throws HttpException {
        when(restOperations.postForEntity(eq(uri), anyObject(), eq(String.class))).thenThrow(
                new ResourceAccessException("IO Error"));

        impl.postForJson(uri, "");
    }
}
