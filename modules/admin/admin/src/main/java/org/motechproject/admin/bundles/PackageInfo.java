package org.motechproject.admin.bundles;

import org.osgi.service.packageadmin.ExportedPackage;

import java.util.Objects;

public class PackageInfo {

    private String name;
    private String from;
    private String version;

    public PackageInfo(ExportedPackage exportedPackage) {
        this(exportedPackage.getName(), exportedPackage.getExportingBundle().getSymbolicName(),
                exportedPackage.getVersion().toString());
    }

    public PackageInfo(String name, String from, String version) {
        this.name = name;
        this.from = from;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getVersion() {
        return version;
    }

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
