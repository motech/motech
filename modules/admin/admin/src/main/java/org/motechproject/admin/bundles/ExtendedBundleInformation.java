package org.motechproject.admin.bundles;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.server.api.BundleInformation;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class ExtendedBundleInformation extends BundleInformation {

    public static final String BUILT_BY = "Built-By";
    public static final String TOOL = "Tool";
    public static final String CREATED_BY = "Created-By";
    public static final String VENDOR = "Bundle-Vendor";
    public static final String BUILD_JDK = "Build-Jdk";
    public static final String LAST_MODIFIED = "Bnd-LastModified";
    public static final String BUNDLE_ACTIVATOR = "Bundle-Activator";
    public static final String DESCRIPTION = "Bundle-Description";
    public static final String DOC_URL = "Bundle-DocURL";
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
    private String docURL;
    private String importPackageHeader;
    private String exportPackageHeader;

    private List<String> registeredServices = new ArrayList<>();
    private List<String> servicesInUse = new ArrayList<>();

    private List<PackageInfo> bundleImports = new ArrayList<>();
    private List<PackageInfo> bundleExports = new ArrayList<>();

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
        docURL = headers.get(DOC_URL);
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

    public String getBuiltBy() {
        return builtBy;
    }

    public String getTool() {
        return tool;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getVendor() {
        return vendor;
    }

    public String getBuildJDK() {
        return buildJDK;
    }

    public DateTime getLastModified() {
        return lastModified;
    }

    public String getBundleActivator() {
        return bundleActivator;
    }

    public String getDescription() {
        return description;
    }

    public String getDocURL() {
        return docURL;
    }

    public List<String> getRegisteredServices() {
        return registeredServices;
    }

    public List<String> getServicesInUse() {
        return servicesInUse;
    }

    public List<PackageInfo> getBundleImports() {
        return bundleImports;
    }

    public void setBundleImports(List<PackageInfo> bundleImports) {
        this.bundleImports = bundleImports;
    }

    public List<PackageInfo> getBundleExports() {
        return bundleExports;
    }

    public void setBundleExports(List<PackageInfo> bundleExports) {
        this.bundleExports = bundleExports;
    }

    public String getImportPackageHeader() {
        return importPackageHeader;
    }

    public String getExportPackageHeader() {
        return exportPackageHeader;
    }
}
