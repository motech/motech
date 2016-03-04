package org.motechproject.admin.bundles;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

/**
 * Extended version of the {@link BundleInformation} class, which adds more fields from the bundle's manifest.
 * This class can be used to generate detailed views describing the given @{link Bundle}.
 */
public class ExtendedBundleInformation extends BundleInformation {

    public static final String BUILT_BY = "Built-By";
    public static final String TOOL = "Tool";
    public static final String CREATED_BY = "Created-By";
    public static final String VENDOR = "Bundle-Vendor";
    public static final String BUILD_JDK = "Build-Jdk";
    public static final String LAST_MODIFIED = "Bnd-LastModified";
    public static final String BUNDLE_ACTIVATOR = "Bundle-Activator";
    public static final String DESCRIPTION = "Bundle-Description";
    public static final String IMPORT_PACKAGE = "Import-Package";
    public static final String EXPORT_PACKAGE = "Export-Package";

    private String builtBy;
    private String tool;
    private String createdBy;
    private String vendor;
    private String buildJDK;
    private DateTime lastModified;
    private String bundleActivator;
    private String description;
    private String importPackageHeader;
    private String exportPackageHeader;

    private List<String> registeredServices = new ArrayList<>();
    private List<String> servicesInUse = new ArrayList<>();

    private List<PackageInfo> bundleImports = new ArrayList<>();
    private List<PackageInfo> bundleExports = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param bundle the bundle about which information will be kept
     */
    public ExtendedBundleInformation(Bundle bundle) {
        super(bundle);
        readServices(bundle);
        readManifest(bundle);
        formatOutput();
    }

    private void readServices(Bundle bundle) {
        BundleContext context = bundle.getBundleContext();

        ServiceReference[] serviceRefs = bundle.getRegisteredServices();
        if (serviceRefs != null) {
            for (ServiceReference ref : serviceRefs) {
                Object service = context.getService(ref);
                registeredServices.add(getServiceClassName(service));
            }
        }

        serviceRefs = bundle.getServicesInUse();
        if (serviceRefs != null) {
            for (ServiceReference ref : serviceRefs) {
                Object service = context.getService(ref);
                servicesInUse.add(getServiceClassName(service));
            }
        }
    }

    private static String getServiceClassName(Object service) {
        String className;

        if (AopUtils.isJdkDynamicProxy(service)) {
            className = ((Advised) service).getTargetClass().getName();
        } else {
            className = service.getClass().getName();
        }

        return className;
    }

    private void readManifest(Bundle bundle) {
        Dictionary<String, String> headers = bundle.getHeaders();

        builtBy = headers.get(BUILT_BY);
        tool = headers.get(TOOL);
        createdBy = headers.get(CREATED_BY);
        vendor = headers.get(VENDOR);
        buildJDK = headers.get(BUILD_JDK);
        bundleActivator = headers.get(BUNDLE_ACTIVATOR);
        description = headers.get(DESCRIPTION);
        importPackageHeader = headers.get(IMPORT_PACKAGE);
        exportPackageHeader = headers.get(EXPORT_PACKAGE);

        String lastModHeader = headers.get(LAST_MODIFIED);
        if (StringUtils.isNotBlank(lastModHeader)) {
            try {
                long lastModMilis = Long.parseLong(lastModHeader);
                lastModified = new DateTime(lastModMilis);
            } catch (NumberFormatException e) {
                lastModified = null;
            }

        }
    }

    private void formatOutput() {
        if (importPackageHeader != null) {
            importPackageHeader = addSpaces(importPackageHeader);
        }
        if (exportPackageHeader != null) {
            exportPackageHeader = addSpaces(exportPackageHeader);
        }
    }

    private String addSpaces(String str) {
        return str.replace(",", ", ");
    }

    /**
     * @return who was this package built by, usually the username of the user that built the bundle
     */
    public String getBuiltBy() {
        return builtBy;
    }

    /**
     * @return the tool used to build this bundle, usually added automatically by bundle generating tools such as Bnd
     */
    public String getTool() {
        return tool;
    }

    /**
     * @return what created the bundle, for example this is set to "Apache Maven Bundle Plugin" in our bundles
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @return vendor of the bundle
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * @return version of the JDK used to build the bundle
     */
    public String getBuildJDK() {
        return buildJDK;
    }

    /**
     * @return the date and time on which this bundle was last modified
     */
    public DateTime getLastModified() {
        return lastModified;
    }

    /**
     * @return Fully qualified name of the activator class for this bundle (activators are not mandatory)
     * @see org.osgi.framework.BundleActivator
     */
    public String getBundleActivator() {
        return bundleActivator;
    }

    /**
     * @return the human friendly description of this bundle
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the list of OSGi services (fully qualified class names) registered by this bundle
     */
    public List<String> getRegisteredServices() {
        return registeredServices;
    }

    /**
     * @return the list of OSGi services (fully qualified class names) used by this bundle
     */
    public List<String> getServicesInUse() {
        return servicesInUse;
    }

    /**
     * @return the list of packages being currently imported by this bundle
     */
    public List<PackageInfo> getBundleImports() {
        return bundleImports;
    }

    /**
     * @param bundleImports the list of packages being currently imported by this bundle
     */
    public void setBundleImports(List<PackageInfo> bundleImports) {
        this.bundleImports = bundleImports;
    }

    /**
     * @return the list of packages being currently exported by this bundle
     */
    public List<PackageInfo> getBundleExports() {
        return bundleExports;
    }

    /**
     * @param bundleExports the list of packages being currently exported by this bundle
     */
    public void setBundleExports(List<PackageInfo> bundleExports) {
        this.bundleExports = bundleExports;
    }

    /**
     * Returns the entire content of the bundle's Import-Package header, which is not synonymous to
     * currently imported packages. The output is formatted with spaces between commas, so that it is more
     * readable.
     * @return the Import-Package header from the OSGi manifest
     */
    public String getImportPackageHeader() {
        return importPackageHeader;
    }

    /**
     * Returns the entire content of the bundle's Export-Package header, which is not synonymous to
     * currently exported packages. The output is formatted with spaces between commas, so that it is more
     * readable.
     * @return the Export-Package header from the OSGi manifest
     */
    public String getExportPackageHeader() {
        return exportPackageHeader;
    }
}
