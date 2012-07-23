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

    protected final static String BUILT_BY = "Built-By";
    protected final static String TOOL = "Tool";
    protected final static String CREATED_BY = "Created-By";
    protected final static String VENDOR = "Bundle-Vendor";
    protected final static String BUILD_JDK = "Build-Jdk";
    protected final static String LAST_MODIFIED = "Bnd-LastModified";
    protected final static String BUNDLE_ACTIVATOR = "Bundle-Activator";
    protected final static String DESCRIPTION = "Bundle-Description";
    protected final static String DOC_URL = "Bundle-DocURL";
    protected final static String IMPORT_PACKAGE = "Import-Package";
    protected final static String EXPORT_PACKAGE = "Export-Package";

    protected String builtBy;
    protected String tool;
    protected String createdBy;
    protected String vendor;
    protected String buildJDK;
    protected String location;
    protected DateTime lastModified;
    protected String bundleActivator;
    protected String description;
    protected String docURL;
    protected String importPackage;
    protected String exportPackage;
    protected List<String> registeredServices = new ArrayList<>();
    protected List<String> servicesInUse = new ArrayList<>();


    public ExtendedBundleInformation(Bundle bundle) {
        super(bundle);
        readServices(bundle);
        readManifest(bundle);
        formatOutput();
    }

    protected void readServices(Bundle bundle) {
        BundleContext context = bundle.getBundleContext();

        for (ServiceReference ref : bundle.getRegisteredServices()) {
            Object service = context.getService(ref);
            registeredServices.add(service.getClass().getName());
        }

        for (ServiceReference ref : bundle.getServicesInUse()) {
            Object service = context.getService(ref);
            servicesInUse.add(service.getClass().getName());
        }
    }

    protected void readManifest(Bundle bundle) {
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
                // leave the lastModified field empty
            }

        }
    }

    protected void formatOutput() {
        if (importPackage != null) {
            importPackage = addSpaces(importPackage);
        }
        if (exportPackage != null) {
            exportPackage = addSpaces(exportPackage);
        }
    }

    protected String addSpaces(String str) {
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
