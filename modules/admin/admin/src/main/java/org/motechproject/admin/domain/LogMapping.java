package org.motechproject.admin.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.Objects;

@TypeDiscriminator("doc.type === 'LogMapping'")
public class LogMapping extends MotechBaseDataObject {
    private static final long serialVersionUID = 1804218094644285698L;

    private String logName;
    private String logLevel;

    public LogMapping() {
        this(null, null);
    }

    public LogMapping(String logName, String logLevel) {
        this.logName = logName;
        this.logLevel = logLevel;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(logName, logLevel);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final LogMapping other = (LogMapping) obj;

        return Objects.equals(this.logName, other.logName) && Objects.equals(this.logLevel, other.logLevel);
    }

    @Override
    public String toString() {
        return String.format("LogMapping{logName='%s', logLevel='%s'}", logName, logLevel);
    }
}
