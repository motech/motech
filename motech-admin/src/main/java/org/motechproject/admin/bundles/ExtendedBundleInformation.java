package org.motechproject.admin.bundles;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.server.osgi.BundleInformation;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class ExtendedBundleInformation extends BundleInformation {

    private static final String BUILT_BY = "Built-By";
    private static final String TOOL = "Tool";
    private static final String CREATED_BY = "Created-By";
    private static final String VENDOR = "Bundle-Vendor";
    private static final String BUILD_JDK = "Build-Jdk";
    private static final String LAST_MODIFIED = "Bnd-LastModified";
    private static final String BUNDLE_ACTIVATOR = "Bundle-Activator";
    private static final String DESCRIPTION = "Bundle-Description";
    private static final String DOC_URL = "Bundle-DocURL";
    private static final String IMPORT_PACKAGE = "Import-Package";
    private static final String EXPORT_PACKAGE = "Export-Package";

    private String builtBy;
    private String tool;
    private String createdBy;
    private String vendor;
    private String buildJDK;
    private String location;
    private DateTime lastModified;
    private String bundleActivator;
    private String description;
    private String docURL;
    private String importPackage;
    private String exportPackage;
    private List<String> registeredServices = new ArrayList<>();
    private List<String> servicesInUse = new ArrayList<>();


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
                registeredServices.add(service.getClass().getName());
            }
        }

        serviceRefs = bundle.getServicesInUse();
        if (serviceRefs != null) {
            for (ServiceReference ref : serviceRefs) {
                Object service = context.getService(ref);
                servicesInUse.add(service.getClass().getName());
            }
        }
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
        importPackage = headers.get(IMPORT_PACKAGE);
        exportPackage = headers.get(EXPORT_PACKAGE);

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
        if (importPackage != null) {
            importPackage = addSpaces(importPackage);
        }
        if (exportPackage != null) {
            exportPackage = addSpaces(exportPackage);
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

    public String getImportPackage() {
        return importPackage;
    }

    public String getExportPackage() {
        return exportPackage;
    }

    public List<String> getRegisteredServices() {
        return registeredServices;
    }

    public List<String> getServicesInUse() {
        return servicesInUse;
    }
}
