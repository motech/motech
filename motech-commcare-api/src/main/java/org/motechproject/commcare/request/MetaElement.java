package org.motechproject.commcare.request;

public class MetaElement {
    String xmlns;
    String instanceID;
    String timeStart;
    String timeEnd;
    String userID;

    public MetaElement(String xmlns, String instanceID, String timeStart, String timeEnd, String userId) {
        this.xmlns = xmlns;
        this.instanceID = instanceID;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.userID = userId;
    }


    public String getXmlns() {
        return xmlns;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public String getUserID() {
        return userID;
    }
}
