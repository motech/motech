package org.motechproject.mds.test.domain.lookupcomboboxrelation;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.List;

@Entity
public class LogParameters {

    public LogParameters(String paramName, LogStatus paramStatus, List<String> values, List<LogAttribute> logAttribute) {
        this.paramName = paramName;
        this.paramStatus = paramStatus;
        this.values = values;
        this.logAttribute = logAttribute;
    }

    @Field
    private String paramName;

    @Field
    private LogStatus paramStatus;

    @Field
    private List<String> values;

    @Field
    private List<LogAttribute> logAttribute;

    public List<LogAttribute> getLogAttribute() {
        return logAttribute;
    }

    public void setLogAttribute(List<LogAttribute> logAttribute) {
        this.logAttribute = logAttribute;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public LogStatus getParamStatus() {
        return paramStatus;
    }

    public void setParamStatus(LogStatus paramStatus) {
        this.paramStatus = paramStatus;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

}
