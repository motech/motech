package org.motechproject.metrics.builder;

import org.motechproject.metrics.api.HealthCheck;
import org.motechproject.metrics.model.ResultAdapter;

/**
 * Helper class enabling client modules to build instances of health check results to satisfy the HealthCheck contract.
 */
public final class HealthCheckResultBuilder {
    /**
     * Return a healthy check result.
     *
     * @return a healthy check result
     */
    public static HealthCheck.Result healthy() {
        return new ResultAdapter(com.codahale.metrics.health.HealthCheck.Result.healthy());
    }

    /**
     * Return a healthy check result, with status message.
     *
     * @param message the status message
     * @return the healthy check result
     */
    public static HealthCheck.Result healthy(String message) {
        return new ResultAdapter(com.codahale.metrics.health.HealthCheck.Result.healthy(message));
    }

    /**
     * Return a healthy check result, with status message built from a template.
     *
     * @param message a template string
     * @param args values to interpolate into the template string
     * @return the healthy check result
     */
    public static HealthCheck.Result healthy(String message, Object... args) {
        return new ResultAdapter(com.codahale.metrics.health.HealthCheck.Result.healthy(message, args));
    }

    /**
     * Return an unhealthy check result, with status message.
     *
     * @param message the status message
     * @return the unhealthy check result
     */
    public static HealthCheck.Result unhealthy(String message) {
        return new ResultAdapter(com.codahale.metrics.health.HealthCheck.Result.unhealthy(message));
    }

    /**
     * Return an unhealthy check result, with status message built from a template.
     *
     * @param message a template string
     * @param args values to interpolate into the template string
     * @return the unhealthy check result
     */
    public static HealthCheck.Result unhealthy(String message, Object... args) {
        return new ResultAdapter(com.codahale.metrics.health.HealthCheck.Result.unhealthy(message, args));
    }

    /**
     * Return an unhealthy check result with the exception that caused the check to fail.
     *
     * @param error the exception that caused the check to fail
     * @return the unhealthy check result
     */
    public static HealthCheck.Result unhealthy(Throwable error) {
        return new ResultAdapter(com.codahale.metrics.health.HealthCheck.Result.unhealthy(error));
    }

    private HealthCheckResultBuilder() {}
}