package org.motechproject.http.agent.service;

import org.springframework.http.ResponseEntity;

public interface HttpAgent {

    void execute(String url, Object data, Method method);

    void executeSync(String url, Object data, Method method);
    
    /**
     * Executes the Http Request and returns the response
     */
    ResponseEntity<?> executeWithReturnTypeSync(String url, Object data, Method method);

    /**
     * Executes the Http Request and returns the response, takes additional parameter for number of retries
     */
    ResponseEntity<?> executeWithReturnTypeSync(String url, Object data, Method method, Integer retryCount);

    /**
     * Executes the Http Request and returns the response, takes additional parameters for number of retries and interval between two retries
     */
    ResponseEntity<?> executeWithReturnTypeSync(String url, Object data, Method method, Integer retryCount, Long retryInterval);
}
