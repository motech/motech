package org.motechproject.commcare.domain;
/**
 * Class that represents the close
 * element of case xml. Including this
 * in a CaseTask will generate the close
 * element in the xml, regardless of
 * the boolean's value.
 *
 */
public class CloseTask {

    private boolean close;

    public CloseTask(boolean close) {
        this.close = close;
    }

    public boolean isClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

}
