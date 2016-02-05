package org.motechproject.metrics.model;

import org.motechproject.metrics.api.HealthCheck;

/**
 * An implementation of a health check result.
 */
public class ResultAdapter implements HealthCheck.Result {
    private final com.codahale.metrics.health.HealthCheck.Result result;

    public ResultAdapter(com.codahale.metrics.health.HealthCheck.Result result) {
        this.result = result;
    }

    /**
     * Get whether or not the health check is healthy
     *
     * @return True if the health check completed successfully, false otherwise.
     */
    @Override
    public boolean isHealthy() {
        return result.isHealthy();
    }

    /**
     * Get the status message associated with the health check.
     *
     * @return the status message
     */
    @Override
    public String getMessage() {
        return result.getMessage();
    }

    /**
     * In the case of an unhealthy check, get the exception that caused the check to fail.
     *
     * @return the thrown exception
     */
    @Override
    public Throwable getError() {
        return result.getError();
    }
}