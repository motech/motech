package org.motechproject.admin.bundles;

import org.osgi.service.packageadmin.ExportedPackage;

import java.util.Objects;

/**
 * A class representing a bundle import within the OSGi framework. Contains information about the exporter,
 * the importer and the package name itself.
 */
public class PackageInfo {

    private String name;
    private String from;
    private String version;

    /**
     * Constructor.
     *
     * @param exportedPackage the package about which information will be kept
     */
    public PackageInfo(ExportedPackage exportedPackage) {
        this(exportedPackage.getName(), exportedPackage.getExportingBundle().getSymbolicName(),
                exportedPackage.getVersion().toString());
    }

    /**
     * Constructor.
     *
     * @param name tha name of the package
     * @param from the symbolic name of the bundle from which the package is exported
     * @param version the version of the package
     */
    public PackageInfo(String name, String from, String version) {
        this.name = name;
        this.from = from;
        this.version = version;
    }

    /**
     * @return the name of the package
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the package
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return symbolic name of the bundle from which the package is exported
     */
    public String getFrom() {
        return from;
    }

    /**
     * @param from symbolic name of the bundle from which the package is exported
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * @return the version of the package
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version of the package
     */
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PackageInfo that = (PackageInfo) o;

        return Objects.equals(name, that.name) && Objects.equals(version, that.version)
                && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
