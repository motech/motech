package org.motechproject.metrics.api;

/**
 * Represents a view of the health of a system component.
 */
public interface HealthCheck {
    /**
     * An interface representing the result of a completed health check.
     */
    interface Result {
        /**
         * Get whether the result is healthy or unhealthy.
         *
         * @return true if the health check was successful, false otherwise.
         */
        boolean isHealthy();

        /**
         * Get the status message of a health check.
         *
         * @return the status message.
         */
        String getMessage();

        /**
         * Get the thrown error for a health check that resulted in an error.
         *
         * @return the thrown exception.
         */
        Throwable getError();
    }

    /**
     * Initiate the health check.
     *
     * @return the result of performing the health check.
     *
     * @throws Exception resulting from a misbehaving health check
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    Result check() throws Exception;
}
