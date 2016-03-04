package org.motechproject.mds.test.domain.lookupcomboboxrelation;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.List;

@Entity
public class MessageLog {

    public MessageLog(String info, LogStatus status) {
        this.info = info;
        this.status = status;
    }

    @Field
    private String info;

    @Field
    private LogStatus status;

    @Field
    private List<LogParameters> parameters;

    @Field
    private LogParameters mainParameter;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public LogStatus getStatus() {
        return status;
    }

    public void setStatus(LogStatus status) {
        this.status = status;
    }

    public List<LogParameters> getParameters() {
        return parameters;
    }

    public void setParameters(List<LogParameters> parameters) {
        this.parameters = parameters;
    }

    public LogParameters getMainParameter() {
        return mainParameter;
    }

    public void setMainParameter(LogParameters mainParameter) {
        this.mainParameter = mainParameter;
    }

}
