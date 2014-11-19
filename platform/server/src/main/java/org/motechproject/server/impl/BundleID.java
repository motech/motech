package org.motechproject.server.impl;

import java.util.Objects;

/**
 * Internal bundle representation.
 * Allows distinction of bundles before they installed, although underneath
 * it is just a POJO containing bundle symbolic name and version.
 */
public class BundleID {

    private final String symbolicName;
    private final String version;

    public BundleID(String symbolicName, String version) {
        this.symbolicName = symbolicName;
        this.version = version;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof BundleID)) {
            return false;
        } else {
            BundleID bundleID = (BundleID) o;
            return Objects.equals(bundleID.symbolicName, symbolicName) && Objects.equals(bundleID.version, version);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbolicName, version);
    }
}
