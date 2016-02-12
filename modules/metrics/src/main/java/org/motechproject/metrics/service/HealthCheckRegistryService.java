package org.motechproject.metrics.service;

import org.motechproject.metrics.api.HealthCheck;

import java.util.SortedSet;

public interface HealthCheckRegistryService {
    /**
     * Registers a healthcheck with the metrics module, allowing the healthcheck to be polled using the supported mechanisms.
     *
     * @param name the name of the healtcheck
     * @param healthCheck an implementation of the healthcheck interface
     */
    void register(String name, HealthCheck healthCheck);

    /**
     * Unregisters a healthcheck by name.
     *
     * @param name the name of the healthcheck
     */
    void unregister(String name);

    /**
     * Returns a set of the name of all healthchecks in sorted order.
     *
     * @return the sorted set of names.
     */
    SortedSet<String> getNames();
}
