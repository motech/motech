package org.motechproject.server.i18n;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.WordUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Loads i18n resource from starting bundles and keeps track of languages and
 * messages. Uses a {@link BundleTracker} underneath to track bundles and scan
 * them for i18n resources.
 */
@Component
public class I18nRepository implements BundleTrackerCustomizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(I18nRepository.class);

    private static final String[] I18N_RESOURCES_PATHS = {"webapp/messages/", "webapp/bundles/",
            "webapp/resources/messages"};

    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    @Autowired
    private BundleContext bundleContext;

    private NavigableMap<String, String> languages = new TreeMap<>();
    private Map<String, String> defaultMsgs = new HashMap<>();
    private Map<Locale, Map<String, String>> msgs = new HashMap<>();

    private BundleTracker bundleTracker;

    /**
     * Initializes the instance by opening the bundle tracker. All already active bundles will get processed.
     */
    @PostConstruct
    public void init() {
        bundleTracker = new BundleTracker(bundleContext, Bundle.STARTING, this);
        bundleTracker.open();

        // the default language
        languages.put(DEFAULT_LOCALE.toString(), DEFAULT_LOCALE.getDisplayLanguage(DEFAULT_LOCALE));

        // go through already running bundles
        for (Bundle bundle : bundleContext.getBundles()) {
            if (bundle.getState() == Bundle.STARTING || bundle.getState() == Bundle.ACTIVE) {
                processBundle(bundle);
            }
        }
    }

    /**
     * Closes the bundle tracker.
     */
    @PreDestroy
    public void preDestroy() {
        if (bundleTracker != null) {
            bundleTracker.close();
        }
    }

    /**
     * Returns all available languages.
     * @return a map of available lanaguages, keys are language codes and values language descriptions
     */
    public NavigableMap<String, String> getLanguages() {
        return languages;
    }

    /**
     * Returns messages for the given locale. These messages will be merged with messages from the
     * default locale. If we don't have any messages for this locale, the default locale will be used.
     * @param locale the locale to fetch the messages
     * @return a map of messages, keys are messages keys and values are messages
     */
    public Map<String, String> getMessages(Locale locale) {
        // first fill with defaults
        Map<String, String> msgsForUser = new HashMap<>(defaultMsgs);

        if (locale != null && !DEFAULT_LOCALE.equals(locale) && msgs.containsKey(locale)) {
            msgsForUser.putAll(msgs.get(locale));
        }
        return msgsForUser;
    }

    @Override
    public Object addingBundle(Bundle bundle, BundleEvent bundleEvent) {
        LOGGER.debug("Bundle {} added", bundle);
        processBundle(bundle);
        return null;
    }

    @Override
    public void modifiedBundle(Bundle bundle, BundleEvent bundleEvent, Object o) {
        LOGGER.debug("Bundle {} modified", bundle);
        processBundle(bundle);
    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent bundleEvent, Object o) {
        LOGGER.debug("Bundle {} removed", bundle);
        processBundle(bundle);
    }

    private synchronized void processBundle(Bundle bundle) {
        for (String path : I18N_RESOURCES_PATHS) {
            Enumeration resources = bundle.getEntryPaths(path);

            if (resources != null) {
                while (resources.hasMoreElements()) {
                    try {
                        Object resource = resources.nextElement();

                        String fullPath = resource.toString();
                        String file = fullPath.replace(path, "");

                        int underscore = file.indexOf('_');
                        int dot = file.indexOf('.', underscore);

                        if ("messages.properties".equals(file)) {
                            addMsgs(DEFAULT_LOCALE, loadFromResource(bundle.getResource(fullPath)));
                        } else if (underscore != -1 && dot != -1) {
                            String fileName = FilenameUtils.getBaseName(file);
                            String parts[] = fileName.split("_", 2);

                            String langLong = parts[1];

                            // we want to handle locale such as zh_TW.Big5
                            String forLocaleUtils = langLong.replaceAll("\\.", "_");
                            Locale locale = LocaleUtils.toLocale(forLocaleUtils);

                            String langFull = WordUtils.capitalize(locale.getDisplayLanguage(locale));

                            languages.put(langLong, langFull);
                            addMsgs(locale, loadFromResource(bundle.getResource(fullPath)));
                        }
                    } catch (IOException e) {
                        LOGGER.error("While reading resource from path {} from bundle {}", path, bundle);
                    }
                }
            }
        }
    }

    private Properties loadFromResource(URL resourceUrl) throws IOException {
        Properties props = new Properties();

        try (InputStream in = resourceUrl.openStream()) {
            props.load(in);
        }

        return props;
    }

    private void addMsgs(Locale locale, Properties newMsgs) {
        if (locale == null || DEFAULT_LOCALE.equals(locale)) {
            defaultMsgs.putAll((Map) newMsgs);
        } else {

            if (!msgs.containsKey(locale)) {
                msgs.put(locale, new HashMap<String, String>());
            }

            Map<String, String> msgMapForLocale = msgs.get(locale);

            msgMapForLocale.putAll((Map) newMsgs);
        }
    }
}
