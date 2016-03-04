package org.motechproject.admin.bundles;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import java.util.Objects;

/**
 * Class acting as a DTO for a {@link Bundle} in the system.
 * Aggregates information about a single bundle.
 */
public class BundleInformation {

    /**
     * Represents the bundle state.
     */
    public enum State {
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
        public int getStateId() {
            return stateId;
        }
    }

    protected static final String BUNDLE_NAME = "Bundle-Name";
    public static final String DOC_URL = "Bundle-DocURL";

    private long bundleId;
    private Version version;
    private String symbolicName;
    private String name;
    private String location;
    private State state;
    private String settingsURL;
    private String moduleName;
    private String angularModule;
    private String docURL;

    /**
     * Constructor.
     *
     * @param bundle  the bundle which this BundleInformation instance will represent
     */
    public BundleInformation(Bundle bundle) {
        this.bundleId = bundle.getBundleId();
        this.version = bundle.getVersion();
        this.symbolicName = bundle.getSymbolicName();
        this.location = bundle.getLocation();
        this.state = State.fromInt(bundle.getState());
        this.name = bundle.getHeaders().get(BUNDLE_NAME);
        this.docURL = bundle.getHeaders().get(DOC_URL);
    }

    /**
     * @return the module name
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Sets the module name.
     *
     * @param moduleName the name of the module
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * @return the id of the bundle
     */
    public long getBundleId() {
        return bundleId;
    }

    /**
     * @return the version of the bundle
     */
    public Version getVersion() {
        return version;
    }

    /**
     * @return the symbolic name of the bundle
     */
    public String getSymbolicName() {
        return symbolicName;
    }

    /**
     * @return the bundle's location identifier
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return a string representation of the state of the bundle
     */
    public State getState() {
        return state;
    }

    /**
     * @return the name of the bundle
     */
    public String getName() {
        return name;
    }

    /**
     * @return the url to the settings page of the bundle
     */
    public String getSettingsURL() {
        return settingsURL;
    }

    /**
     * Sets the url to the settings page.
     *
     * @param settingsURL the url to the settings page
     */
    public void setSettingsURL(String settingsURL) {
        this.settingsURL = settingsURL;
    }

    /**
     * @return the name of the angular module
     */
    public String getAngularModule() {
        return angularModule;
    }

    /**
     * Sets the angular module name.
     *
     * @param angularModule the name of the angular module
     */
    public void setAngularModule(String angularModule) {
        this.angularModule = angularModule;
    }

    /**
     * @return the documentation URL for this bundle
     */
    public String getDocURL() {
        return docURL;
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

    public boolean hasStatus(int status) {
        return state.getStateId() == status ? true : false;
    }
}
