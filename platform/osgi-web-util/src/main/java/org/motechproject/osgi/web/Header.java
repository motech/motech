package org.motechproject.osgi.web;

import org.apache.commons.lang.ArrayUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Enumeration;

import static org.apache.commons.lang.StringUtils.removeStart;

public class Header {
    private static final String CSS_FILE_TYPE = "css";
    private static final String JS_FILE_TYPE = "js";

    private Bundle bundle;
    private String resourcePath;

    @Autowired
    public Header(BundleContext bundleContext) {
        if (bundleContext == null) {
            throw new IllegalArgumentException("The bundleContext parameter is required.");
        }

        bundle = bundleContext.getBundle();
        resourcePath = new BundleHeaders(bundle).getResourcePath();
    }

    public String asString() {
        String css = createEntries("webapp/css/");

        String jquery = createEntries("webapp/lib/jquery/", new String[]{"jquery.js"});
        String angular = createEntries("webapp/lib/angular/", new String[]{"angular.min.js"});
        String other = createEntries("webapp/lib/", null, new String[]{"jquery.js", "angular.min.js"});

        String module = createEntries("webapp/js/");

        return String.format("%s\n%s\n%s\n%s\n%s", css, jquery, angular, other, module);
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    private String createEntries(String path) {
        return createEntries(path, null);
    }

    private String createEntries(String path, String[] include) {
        return createEntries(path, include, null);
    }

    private String createEntries(String path, String[] include, String[] excludes) {
        Enumeration subPaths = bundle.getEntryPaths(path);
        StringBuilder builder = null;

        if (subPaths != null) {
            builder = new StringBuilder();

            while (subPaths.hasMoreElements()) {
                String subPath = subPaths.nextElement().toString();

                if (subPath.endsWith("/")) {
                    builder.append(createEntries(subPath, include, excludes));
                } else {
                    String fileName = subPath.substring(subPath.lastIndexOf('/') + 1);

                    if ((include == null || ArrayUtils.contains(include, fileName))
                            && !ArrayUtils.contains(excludes, fileName)) {
                        String type = fileName.substring(fileName.lastIndexOf('.') + 1);
                        String url = createURL(removeStart(subPath, "webapp/"));
                        String str = createHTML(url, type);
                        builder.append(str);
                        builder.append("\n");
                    }
                }
            }
        }

        return builder == null ? "" : builder.toString();
    }

    private String createURL(String path) {
        return String.format("../%s/%s", resourcePath, path);
    }

    private String createHTML(String url, String type) {
        String entry;

        switch (type) {
            case CSS_FILE_TYPE:
                entry = String.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\"/>", url);
                break;
            case JS_FILE_TYPE:
                entry = String.format("<script type=\"text/javascript\" src=\"%s\"></script>", url);
                break;
            default:
                entry = "";
        }

        return entry;
    }

}
