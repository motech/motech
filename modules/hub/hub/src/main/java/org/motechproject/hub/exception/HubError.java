package org.motechproject.hub.exception;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.motechproject.hub.util.HubUtils;

@XmlRootElement(name = "Error")
public class HubError {

    // transactionId
    private String errorCode;
    private String errorMessage;
    private String application;
    private Date timeStamp;
    private String hostName;

    // HubErrorDetail

    public HubError() {
        timeStamp = HubUtils.getCurrentDateTime();
        hostName = HubUtils.getNetworkHostName();
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

}
