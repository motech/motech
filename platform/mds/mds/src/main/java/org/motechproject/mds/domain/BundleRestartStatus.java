package org.motechproject.mds.domain;

/**
 * Represents the status of the restart during after bundle fails.
 *
 * @see BundleFailureReport
 */
public enum BundleRestartStatus {

    IN_PROGRESS,

    ERROR,

    SUCCESS,
}
