package org.motechproject.osgi.web.ext;

public class BundleName {


    private String bundleSymbolicName;

    public BundleName(String bundleSymbolicName) {
        this.bundleSymbolicName = bundleSymbolicName;
    }

    public String underscore() {
        return bundleSymbolicName.replaceAll("[.-]", "_");
    }

    @Override
    public boolean equals(Object bundle) {
        BundleName other = (BundleName) bundle;
        return bundleSymbolicName.equals(other.bundleSymbolicName);
    }

    @Override
    public int hashCode() {
        return bundleSymbolicName != null ? bundleSymbolicName.hashCode() : 0;
    }
}
