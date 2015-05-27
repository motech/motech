package org.motechproject.osgi.web.ext;

/**
 * A wrapper for a bundle symbolic name. Provides a convenience method for converting the name
 * to a form that can be used as a system variable name by replacing dashes and dots with underscores.
 */
public class BundleName {

    private String bundleSymbolicName;

    public BundleName(String bundleSymbolicName) {
        this.bundleSymbolicName = bundleSymbolicName;
    }

    /**
     * Converts the symbolic name represented by this object to a form that can be used as a system variable name.
     * It will replace dots and dashes with underscores. For example <code>org.motechproject.cms-lite</code> will
     * will be converted to org_motechproject_cms_lite.
     * @return the symbolic name in a form that can be used as a system variable name
     */
    public String underscore() {
        return bundleSymbolicName.replaceAll("[.-]", "_");
    }

    @Override
    public boolean equals(Object bundle) {
        if (!(bundle instanceof BundleName)) {
            return false;
        }
        BundleName other = (BundleName) bundle;
        return bundleSymbolicName.equals(other.bundleSymbolicName);
    }

    @Override
    public int hashCode() {
        return bundleSymbolicName != null ? bundleSymbolicName.hashCode() : 0;
    }
}
