package org.motechproject.osgi.web.settings;

import org.motechproject.osgi.web.domain.LogMapping;

import java.util.List;
import java.util.Objects;

/**
 * Loggers class that holds information about all loggers in our system.
 */

public class Loggers {
    private LogMapping root;
    private List<LogMapping> loggers;
    private List<LogMapping> trash;

    public Loggers() {
        this(null, null);
    }

    public Loggers(List<LogMapping> loggers, LogMapping root) {
        this.loggers = loggers;
        this.root = root;
    }

    public LogMapping getRoot() {
        return root;
    }

    public void setRoot(LogMapping root) {
        this.root = root;
    }

    public List<LogMapping> getLoggers() {
        return loggers;
    }

    public void setLoggers(List<LogMapping> loggers) {
        this.loggers = loggers;
    }

    public List<LogMapping> getTrash() {

        return trash;
    }

    public void setTrash(List<LogMapping> trash) {
        this.trash = trash;
    }

    @Override
    public int hashCode() {
        return Objects.hash(root, loggers, trash);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Loggers other = (Loggers) obj;

        return Objects.equals(this.root, other.root) &&
                Objects.equals(this.loggers, other.loggers) &&
                Objects.equals(this.trash, other.trash);
    }
}
