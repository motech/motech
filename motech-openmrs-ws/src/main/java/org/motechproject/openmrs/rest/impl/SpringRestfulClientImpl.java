package org.motechproject.openmrs.rest.impl;

import java.net.URI;

import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;

/**
 * Implementation of {@link RestClient} based on Spring web client
 */
@Component
public class SpringRestfulClientImpl implements RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringRestfulClientImpl.class);

    private final RestOperations restOperations;

    @Autowired
    public SpringRestfulClientImpl(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    interface RestCommand<T> {
        T execute();
    }

    private class GetRestCommand implements RestCommand<String> {
        private URI url;

        GetRestCommand(URI uri) {
            this.url = uri;
        }

        @Override
        public String execute() {
            ResponseEntity<String> response = restOperations.getForEntity(url, String.class);
            return response.getBody();
        }
    }

    private class PostRestCommand implements RestCommand<String> {

        private URI url;
        private String json;

        PostRestCommand(URI uri, String json) {
            this.url = uri;
            this.json = json;
        }

        @Override
        public String execute() {
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(json, header);
            ResponseEntity<String> response = restOperations.postForEntity(url, entity, String.class);
            return response.getBody();
        }

    }

    public String getJson(URI uri) throws HttpException {
        return executeCommand(new GetRestCommand(uri));
    }

    private <T> T executeCommand(RestCommand<T> command) throws HttpException {
        try {
            return command.execute();
        } catch (HttpClientErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            LOGGER.warn("Request failed with client error: " + e.getMessage());
            throw new HttpException(e.getMessage());
        } catch (HttpServerErrorException e) {
            LOGGER.warn("Request failed with server error:" + e.getMessage());
            throw new HttpException(e.getMessage());
        } catch (ResourceAccessException e) {
            LOGGER.warn("Request failed with IOException: " + e.getMessage());
            throw new HttpException(e.getMessage());
        }
    }

    @Override
    public String postForJson(URI uri, String json) throws HttpException {
        PostRestCommand command = new PostRestCommand(uri, json);
        return executeCommand(command);
    }

    @Override
    public void postWithEmptyResponseBody(URI url, String json) throws HttpException {
        executeCommand(new PostCommand(url, json));
    }

    private class PostCommand implements RestCommand<Void> {

        private URI uri;
        private String json;

        public PostCommand(URI uri, String json) {
            this.uri = uri;
            this.json = json;
        }

        @Override
        public Void execute() {
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(json, header);
            restOperations.postForEntity(uri, entity, null);
            return null;
        }
    }

    @Override
    public void delete(URI uri) throws HttpException {
        executeCommand(new DeleteCommand(uri));
    }

    private class DeleteCommand implements RestCommand<Void> {

        private URI uri;

        DeleteCommand(URI uri) {
            this.uri = uri;
        }

        @Override
        public Void execute() {
            restOperations.delete(uri);
            return null;
        }

    }
}
