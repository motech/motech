package org.motechproject.osgi.web.domain;

import java.util.Objects;

/**
 * The <code>LogMapping</code> class is used for mapping logger objects
 * from the saved log4j properties.
 */
public class LogMapping {

    private String logName;
    private String logLevel;

    public LogMapping() {
        this(null, null);
    }

    public LogMapping(String logName, String logLevel) {
        this.logName = logName;
        this.logLevel = logLevel;
    }

    /**
     * @return the name of the logger represented by this mapping
     */
    public String getLogName() {
        return logName;
    }

    /**
     * @param logName the name of the logger represented by this mapping
     */
    public void setLogName(String logName) {
        this.logName = logName;
    }

    /**
     * @return the log level for the logger represented by this mapping
     */
    public String getLogLevel() {
        return logLevel;
    }

    /**
     * @param logLevel the log level for the logger represented by this mapping
     */
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
