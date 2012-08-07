package org.motechproject.server.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import java.util.Objects;

public class BundleInformation {
    private enum State {
        UNINSTALLED(1),
        INSTALLED(2),
        RESOLVED(4),
        STARTING(8),
        STOPPING(16),
        ACTIVE(32),
        UNKNOWN(0);

        private int stateId;

        State(int stateId) {
            this.stateId = stateId;
        }

        public static State fromInt(int stateId) {
            for (State state : values()) {
                if (stateId == state.stateId) {
                    return state;
                }
            }
            return UNKNOWN;
        }
    }

    protected static final String BUNDLE_NAME = "Bundle-Name";

    private long bundleId;
    private Version version;
    private String symbolicName;
    private String name;
    private String location;
    private State state;

    public BundleInformation(Bundle bundle) {
        this.bundleId = bundle.getBundleId();
        this.version = bundle.getVersion();
        this.symbolicName = bundle.getSymbolicName();
        this.location = bundle.getLocation();
        this.state = State.fromInt(bundle.getState());
        this.name = (String) bundle.getHeaders().get(BUNDLE_NAME);
    }

    public long getBundleId() {
        return bundleId;
    }

    public Version getVersion() {
        return version;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public String getLocation() {
        return location;
    }

    public State getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object arg0) {
        boolean equal = false;
        if (arg0 instanceof BundleInformation) {
            BundleInformation other = (BundleInformation) arg0;
            equal = Objects.equals(state, other.getState()) && Objects.equals(bundleId, other.getBundleId()) &&
                    Objects.equals(version, other.version) && Objects.equals(symbolicName, other.getSymbolicName()) &&
                    Objects.equals(location, other.getLocation()) && Objects.equals(name, other.getName());
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, version, symbolicName, location, bundleId, name);
    }
}
