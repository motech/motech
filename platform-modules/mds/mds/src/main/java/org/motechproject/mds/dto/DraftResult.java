package org.motechproject.mds.dto;

import java.io.Serializable;

/**
 * After users do draft changes an instance of this class is returned.
 * It contains information about the draft state.
 */
public class DraftResult implements Serializable {
    private static final long serialVersionUID = 8702623278079827861L;

    private boolean changesMade;
    private boolean outdated;

    public DraftResult(boolean changesMade, boolean outdated) {
        this.changesMade = changesMade;
        this.outdated = outdated;
    }

    public boolean isChangesMade() {
        return changesMade;
    }

    public void setChangesMade(boolean changesMade) {
        this.changesMade = changesMade;
    }

    public boolean isOutdated() {
        return outdated;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }
}
