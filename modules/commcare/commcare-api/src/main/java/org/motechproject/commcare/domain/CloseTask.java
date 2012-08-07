package org.motechproject.commcare.domain;

/**
 * A domain object to be included in a CaseTask in order to generate a close
 * element in case XML.
 */
public class CloseTask {
    private boolean close;

    public CloseTask(boolean close) {
        this.close = close;
    }

    public boolean isClose() {
        return this.close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }
}
