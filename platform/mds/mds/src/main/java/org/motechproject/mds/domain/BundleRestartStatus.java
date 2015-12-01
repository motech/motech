package org.motechproject.mds.domain;

/**
 * Represents the status of the restart during after bundle fails.
 *
 * @see org.motechproject.mds.domain.BundleFailsReport
 */
public enum BundleRestartStatus {

    IN_PROGRESS,

    ERROR,

    SUCCESS,
}
