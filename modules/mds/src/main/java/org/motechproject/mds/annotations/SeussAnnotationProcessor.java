package org.motechproject.mds.annotations;

import org.osgi.framework.BundleContext;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

/**
 * The <code>SeussAnnotationProcessor</code> class is responsible for scanning bundle contexts and
 * looking for classes, fields and methods containing Seuss annotations, as well as processing them.
 *
 * @see org.motechproject.mds.annotations.Lookup
 * @see org.motechproject.mds.annotations.Entity
 */
public class SeussAnnotationProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeussAnnotationProcessor.class);

    public void findAnnotations(BundleContext bundleContext) {
        LOGGER.debug("Starting to scan bundle " + bundleContext.getBundle().getSymbolicName() + " for MDS annotations.");

        String location = bundleContext.getBundle().getLocation();
        URL resolved = null;
        LOGGER.debug("Resolved the following file location for bundle: " + location);

        try {
            resolved = new URL(location);
        } catch (MalformedURLException e) {
            LOGGER.error("Failed to resolve URL from file: " + location);
        }

        Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(resolved));

        LOGGER.debug("Initialized Reflections for resolved file location. Starting scanning for annotations.");

        Set<String> lookupSet = reflections.getStore().getTypesAnnotatedWith(Lookup.class.getName());

        for (String className : lookupSet) {
            try {
                Class clazz = bundleContext.getBundle().loadClass(className);
                processLookupAnnotatedClass(clazz);
            } catch (ClassNotFoundException e) {
                LOGGER.error("Failed to load class " + className + " from bundle" +
                        bundleContext.getBundle().getSymbolicName());
            }
        }

        Set<String> entitySet = reflections.getStore().getTypesAnnotatedWith(Entity.class.getName());

        for (String className : entitySet) {
            try {
                Class clazz = bundleContext.getBundle().loadClass(className);
                processEntityAnnotatedClass(clazz);
            } catch (ClassNotFoundException e) {
                LOGGER.error("Failed to load class " + className + " from bundle" +
                        bundleContext.getBundle().getSymbolicName());
            }
        }
    }

    private void processLookupAnnotatedClass(Class clazz) {
        //TODO: process classes
        LOGGER.debug("Processing Lookup annotated class: " + clazz.getName() );
    }

    private void processEntityAnnotatedClass(Class clazz) {
        //TODO: process classes
        LOGGER.debug("Processing Entity annotated class: " + clazz.getName() );
    }

}
