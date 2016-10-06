package org.motechproject.server.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.motechproject.osgi.web.service.LocaleService;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.service.UIFrameworkService;
import org.motechproject.server.ui.BundleIconService;
import org.motechproject.server.web.dto.BundleIcon;
import org.motechproject.server.web.dto.ModuleConfig;
import org.motechproject.server.web.dto.ModuleMenu;
import org.motechproject.server.web.form.UserInfo;
import org.motechproject.server.web.helper.MenuBuilder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.commons.api.ClassUtils.filterByClass;
import static org.springframework.util.CollectionUtils.isEmpty;

@Controller
@Api(value = "ModuleController", description = "This controller is to provide information about modules")
public class ModuleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleController.class);

    private static final String NAME_GROUP = "name";
    private static final String DEPENDENCIES_GROUP = "dependencies";
    private static final String MODULE_NAME = String.format(
            "('|\")(?<%s>[^'\"]+)('|\")", NAME_GROUP
    );
    private static final String DEPENDENCIES = String.format(
            "\\[(?<%s>[^\\]]*)\\]", DEPENDENCIES_GROUP
    );
    private static final String REGEXP = String.format(
            "angular\\.module\\(%s(,\\s*%s)?\\s*\\)", MODULE_NAME, DEPENDENCIES
    );
    private static final Pattern PATTERN = Pattern.compile(REGEXP);

    private UIFrameworkService uiFrameworkService;
    private LocaleService localeService;
    private BundleIconService bundleIconService;
    private BundleContext bundleContext;
    private MenuBuilder menuBuilder;

    @RequestMapping(value = "/module/critical/{moduleName}", method = RequestMethod.GET)
    @ResponseBody
    public String getCriticalMessage(@PathVariable String moduleName) {
        String criticalMessage = null;

        if (isNotBlank(moduleName)) {
            ModuleRegistrationData data = uiFrameworkService.getModuleDataByAngular(moduleName);

            if (null != data) {
                criticalMessage = data.getCriticalMessage();
                uiFrameworkService.moduleBackToNormal(moduleName);
            }
        }

        return criticalMessage;
    }

    @RequestMapping(value = "/module/menu", method = RequestMethod.GET)
    @ApiOperation(value = "Returns the module menu for a user")
    @ResponseBody
    public ModuleMenu getMenu(HttpServletRequest request) {
        String username = getUser(request).getUserName();
        return menuBuilder.buildMenu(username);
    }

    @RequestMapping(value = "/module/config", method = RequestMethod.GET)
    @ApiOperation(value = "Returns modules' configuration")
    @ResponseBody
    public List<ModuleConfig> getConfig() throws IOException {
        List<ModuleConfig> configuration = new ArrayList<>();

        for (Bundle bundle : bundleContext.getBundles()) {
            ModuleRegistrationData data = uiFrameworkService.getModuleDataByBundle(bundle);

            if (null != data) {
                Map<String, String> scripts = findScripts(bundle);

                for (Map.Entry<String, String> script : scripts.entrySet()) {
                    addConfig(configuration, script.getKey(), getPath(data, script.getValue()));
                }

                List<String> angularModules = data.getAngularModules();
                String name = isEmpty(angularModules) ? null : angularModules.get(0);

                List<URL> css = filterByClass(URL.class, bundle.findEntries("/webapp/css/", "*.css", true));
                String cssPath = null;

                if (!css.isEmpty() && !bundle.getSymbolicName().equalsIgnoreCase("org.motechproject.motech-platform-server-bundle")) {
                    cssPath = getPath(data, getCSSPath(css.get(0)));
                }

                addConfig(configuration, name, getPath(data, "/js/app.js"), data.getUrl(), cssPath);
            }
        }

        // rest documentation han an Angular module within server-bundle
        configuration.add(restDocConfig());

        return configuration;
    }

    /**
     * Returns the icon associated with the given bundle. Bundles that do not have their own icons will
     * get a default icon.
     *
     * @param bundleId    the id of the bundle for which the icon should be retrieved
     * @param bundleName  the name of the bundle for which the icon should be retrieved
     * @param defaultIcon the name of the default icon which be provided if any standard icon couldn't be searched
     * @param response    the HttpServletResponse, used for writing the icon in its output
     * @throws IOException if there were failures writing the icon to the output
     */
    @RequestMapping(value = "/module/icon", method = RequestMethod.GET)
    @ApiOperation(value = "Returns the icon associated with the given bundle. Bundles that do not have their own icons will\n" +
            "get a default icon.")
    public void getBundleIcon(@RequestParam(required = false) Long bundleId,
                              @RequestParam(required = false) String bundleName,
                              @RequestParam(required = false) String defaultIcon,
                              HttpServletResponse response) throws IOException {
        BundleIcon bundleIcon;
        if (bundleId != null) {
            bundleIcon = bundleIconService.getBundleIconById(bundleId, defaultIcon);
        } else {
            bundleIcon = bundleIconService.getBundleIconByName(bundleName, defaultIcon);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentLength(bundleIcon.getContentLength());
        response.setContentType(bundleIcon.getMime());

        response.getOutputStream().write(bundleIcon.getIcon());
    }

    /**
     * Returns the url for rest documentation spec of the given module
     *
     * @param moduleName the name of the module
     * @return the url at which the REST API spec can be accessed
     */
    @RequestMapping(value = "/module/rest-docs/{moduleName}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Returns the url for rest documentation spec of the given module")
    @ResponseBody
    public String getRestDocsUrl(@PathVariable String moduleName) {
        return uiFrameworkService.getRestDocLinks().get(moduleName);
    }

    public UserInfo getUser(HttpServletRequest request) {
        String lang = localeService.getUserLocale(request).getLanguage();
        boolean securityLaunch = request.getUserPrincipal() != null;
        String userName = securityLaunch ? request.getUserPrincipal().getName() : "Admin Mode";

        return new UserInfo(userName, securityLaunch, lang);
    }

    private Map<String, String> findScripts(Bundle bundle) {
        List<URL> entries = getEntries(bundle);
        Map<String, String> scripts = new HashMap<>();
        List<String> requires = new ArrayList<>();

        for (URL entry : entries) {
            String content = getContent(entry);
            String path = getJSPath(entry);
            String filename = FilenameUtils.getBaseName(path);

            Matcher matcher = PATTERN.matcher(content);

            while (matcher.find()) {
                if (filename.equalsIgnoreCase("app")) {
                    String[] dependencies = getDependencies(matcher);
                    Collections.addAll(requires, dependencies);
                }

                String name = matcher.group(NAME_GROUP);

                if (!scripts.containsKey(name)) {
                    scripts.put(name, path);
                }
            }
        }

        Iterator<String> keyIterator = scripts.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();

            if (!requires.contains(key)) {
                keyIterator.remove();
            }
        }

        return scripts;
    }

    private void addConfig(List<ModuleConfig> configuration, String name, String script) {
        addConfig(configuration, name, script, null, null);
    }

    private void addConfig(List<ModuleConfig> configuration, String name, String script,
                           String template, String css) {
        ModuleConfig config = new ModuleConfig();
        config.setName(name);
        config.setScript(script);
        config.setTemplate(template);
        config.setCss(css);

        if (isNotBlank(name)) {
            if (containsConfig(configuration, name)) {
                throw new IllegalStateException("The angular module name have to be unique");
            }

            configuration.add(config);
        }
    }

    private String getContent(URL url) {
        StringWriter writer = new StringWriter();
        String content;
        try {
            IOUtils.copy(url.openStream(), writer);
            content = writer.toString();
        } catch (IOException e) {
            LOGGER.error("There were problems with read entry: {}", url.getPath(), e);
            content = EMPTY;
        }

        return content.replace("\n", "");
    }

    private String getJSPath(URL url) {
        String path = url.getPath();
        int idx = path.indexOf("/js/");

        if (idx > 0) {
            path = path.substring(idx);
        }

        return path;
    }

    private String getCSSPath(URL url) {
        String path = url.getPath();
        int idx = path.indexOf("/css/");

        if (idx > 0) {
            path = path.substring(idx);
        }

        return path;
    }

    private String getPath(ModuleRegistrationData data, String path) {
        String p = path;

        if (!p.startsWith("/")) {
            p = "/" + p;
        }

        return "../" + data.getResourcePath() + p;
    }

    private List<URL> getEntries(Bundle bundle) {
        Enumeration enumeration = bundle.findEntries("/webapp/js", "*.js", true);
        return filterByClass(URL.class, enumeration);
    }

    private String[] getDependencies(Matcher matcher) {
        String requires = matcher.group(DEPENDENCIES_GROUP);
        requires = requires.replaceAll("('|\"|\\s+)", "");

        return requires.split(",");
    }

    private boolean containsConfig(List<ModuleConfig> configuration, final String name) {
        return CollectionUtils.exists(configuration, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                boolean match = object instanceof ModuleConfig;

                if (match) {
                    ModuleConfig config = (ModuleConfig) object;
                    match = equalsIgnoreCase(name, config.getName());
                }

                return match;
            }
        });
    }

    private ModuleConfig restDocConfig() {
        ModuleConfig config = new ModuleConfig();
        config.setName("rest-docs");
        config.setTemplate("../server/resources/partials/rest-docs-index.html");
        config.setScript("../server/resources/rest-docs/rest-docs-app.js");
        return config;
    }

    @Autowired
    public void setUiFrameworkService(UIFrameworkService uiFrameworkService) {
        this.uiFrameworkService = uiFrameworkService;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Autowired
    public void setLocaleService(LocaleService localeService) {
        this.localeService = localeService;
    }

    @Autowired
    public void setBundleIconService(BundleIconService bundleIconService) {
        this.bundleIconService = bundleIconService;
    }

    @Autowired
    public void setMenuBuilder(MenuBuilder menuBuilder) {
        this.menuBuilder = menuBuilder;
    }
}
