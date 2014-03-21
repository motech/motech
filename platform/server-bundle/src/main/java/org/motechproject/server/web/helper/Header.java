package org.motechproject.server.web.helper;

import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.commons.api.CastUtils;
import org.motechproject.osgi.web.util.BundleHeaders;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public final class Header {
    private static final Logger LOGGER = LoggerFactory.getLogger(Header.class);

    private Header() {
    }

    public static class HeaderOrder {
        private List<ElementOrder> lib;
        private List<ElementOrder> js;
        private List<ElementOrder> css;

        public List<ElementOrder> getLib() {
            return lib;
        }

        public void setLib(List<ElementOrder> lib) {
            this.lib = lib;
        }

        public List<ElementOrder> getJs() {
            return js;
        }

        public void setJs(List<ElementOrder> js) {
            this.js = js;
        }

        public List<ElementOrder> getCss() {
            return css;
        }

        public void setCss(List<ElementOrder> css) {
            this.css = css;
        }
    }

    public static class ElementOrder {
        private String path;
        private String order;
        private String before;
        private String after;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getOrder() {
            return order;
        }

        public void setOrder(String order) {
            this.order = order;
        }

        public String getBefore() {
            return before;
        }

        public void setBefore(String before) {
            this.before = before;
        }

        public String getAfter() {
            return after;
        }

        public void setAfter(String after) {
            this.after = after;
        }
    }

    public static String generateHeader(Bundle bundle) {
        InputStream stream = Header.class.getResourceAsStream("/header-order.json");
        HeaderOrder order;

        if (null != stream) {
            try {
                order = new ObjectMapper().readValue(stream, HeaderOrder.class);
            } catch (IOException e) {
                LOGGER.error("There were problems with read header-order.json", e);
                order = new HeaderOrder();
            }
        } else {
            order = new HeaderOrder();
        }

        String resourcePath = new BundleHeaders(bundle).getResourcePath();
        StringBuilder builder = new StringBuilder();

        addCSS(builder, resourcePath, bundle, order.getCss());
        addScripts(builder, bundle, resourcePath, "lib", order.getLib());
        addScripts(builder, bundle, resourcePath, "js", order.getJs());

        return builder.toString();
    }

    private static void addScripts(StringBuilder builder, Bundle bundle, String resourcePath,
                                   String folderName, List<ElementOrder> order) {
        List<String> js = get(bundle, folderName, "*.js", order);

        for (String entryPath : js) {
            String path = "../" + resourcePath + entryPath;
            String script = createScript(path);

            builder.append(script);
            builder.append("\n");
        }
    }

    private static void addCSS(StringBuilder builder, String resourcePath, Bundle bundle,
                               List<ElementOrder> order) {
        List<String> css = get(bundle, "css", "*.css", order);

        for (String entryPath : css) {
            String path = "../" + resourcePath + entryPath;
            String script = createCSS(path);

            builder.append(script);
            builder.append("\n");
        }

        builder.append("\n");
    }

    private static List<String> get(Bundle bundle, String folderName, String filePattern,
                                    List<ElementOrder> order) {
        String entriesPath = String.format("/webapp/%s/", folderName);
        Enumeration entries = bundle.findEntries(entriesPath, filePattern, true);
        List<URL> urls = CastUtils.cast(URL.class, entries);
        List<String> paths = new ArrayList<>(urls.size());

        for (URL url : urls) {
            paths.add(getPath(url));
        }

        if (!paths.isEmpty() && null != order) {
            changeOrder(paths, order, folderName);
        }

        return paths;
    }

    private static void changeOrder(List<String> paths, List<ElementOrder> order,
                                    String folderName) {
        for (ElementOrder o : order) {
            String path = String.format("/%s/%s", folderName, o.getPath());

            // remove path from list. It will be added later in the specific index
            paths.remove(path);

            if (isNotBlank(o.getOrder())) {
                // the resource should be in the specific place
                switch (o.getOrder()) {
                    case "first":
                        paths.add(0, path);
                        break;
                    case "last":
                        paths.add(path);
                        break;
                    default:
                        int idx = Integer.parseInt(o.getOrder());
                        paths.add(idx, path);
                }
            } else if (isNotBlank(o.getAfter())) {
                // the resource should be after other resource
                String after = String.format("/%s/%s", folderName, o.getAfter());
                int idx = paths.indexOf(after);
                paths.add(idx + 1, path);
            } else if (isNotBlank(o.getBefore())) {
                // the resource should be before other resource
                String before = String.format("/%s/%s", folderName, o.getBefore());
                int idx = paths.indexOf(before);
                paths.add(idx - 1, path);
            }
        }
    }

    private static String createScript(String url) {
        return String.format("<script type=\"text/javascript\" src=\"%s\"></script>", url);
    }

    private static String createCSS(String url) {
        return String.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\"/>", url);
    }

    private static String getPath(URL url) {
        String path = url.getPath();

        if (path.contains("/webapp/")) {
            path = path.replace("/webapp/", "");
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        return path;
    }

}
