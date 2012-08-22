package org.motechproject.commcare.request;

public class MetaElement {
    private String xmlns;
    private String instanceID;
    private String timeStart;
    private String timeEnd;
    private String userID;

    public MetaElement(String xmlns, String instanceID, String timeStart,
            String timeEnd, String userId) {
        this.xmlns = xmlns;
        this.instanceID = instanceID;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.userID = userId;
    }

    public String getXmlns() {
        return this.xmlns;
    }

    public String getInstanceID() {
        return this.instanceID;
    }

    public String getTimeStart() {
        return this.timeStart;
    }

    public String getTimeEnd() {
        return this.timeEnd;
    }

    public String getUserID() {
        return this.userID;
    }
}
