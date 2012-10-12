package org.motechproject.server.ui.impl;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.WordUtils;
import org.motechproject.server.ui.LocaleSettings;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.TreeMap;

@Service
public class LocaleSettingsImpl implements LocaleSettings, BundleContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocaleSettings.class);
    private static final String I18N_RESOURCES_PATH = "webapp/resources/messages/";

    @Autowired
    private CookieLocaleResolver cookieLocaleResolver;

    @Autowired
    private BundleContext bundleContext;

    @Override
    public Locale getUserLocale(final HttpServletRequest requst) {
        return cookieLocaleResolver.resolveLocale(requst);
    }

    @Override
    public void setUserLocale(final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
        cookieLocaleResolver.setLocale(request, response, locale);
    }

    @Override
    public NavigableMap<String, String> getAvailableLanguages() {
        NavigableMap<String, String> languages = new TreeMap<>();
        Enumeration<String> resources = bundleContext.getBundle().getEntryPaths(I18N_RESOURCES_PATH);

        while (resources.hasMoreElements()) {
            String file = resources.nextElement().replace(I18N_RESOURCES_PATH, "");

            int underscore = file.indexOf('_');
            int dot = file.lastIndexOf('.');

            if (underscore != -1 && dot != -1) {
                String langShort = file.substring(underscore + 1, dot);
                Locale locale = LocaleUtils.toLocale(langShort);
                String langFull = WordUtils.capitalize(locale.getDisplayLanguage(locale));
                languages.put(langShort, langFull);
            }
        }

        if (!languages.containsKey("en")) {
            languages.put("en", "English");
        }

        return languages;
    }

    @Override
    public void setBundleContext(final BundleContext context) {
        this.bundleContext = context;
    }
}
