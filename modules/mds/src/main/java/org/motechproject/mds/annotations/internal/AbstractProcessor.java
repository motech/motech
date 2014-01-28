package org.motechproject.mds.annotations.internal;

import org.osgi.framework.Bundle;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

abstract class AbstractProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProcessor.class);

    private Bundle bundle;

    protected abstract Class<? extends Annotation> getAnnotation();

    protected abstract List<? extends AnnotatedElement> getElements();

    protected abstract void process(AnnotatedElement element);

    public void execute(Bundle bundle) {
        this.bundle = bundle;
        Class<? extends Annotation> annotation = getAnnotation();

        for (AnnotatedElement element : getElements()) {
            LOGGER.debug("Processing: Annotation: {} Object: {}", annotation.getName(), element);

            process(element);

            LOGGER.debug("Processed: Annotation: {} Object: {}", annotation.getName(), element);
        }
    }

    protected Bundle getBundle() {
        return bundle;
    }

    void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    protected Reflections configureReflection(Scanner... scanners) {
        ConfigurationBuilder configuration = new ConfigurationBuilder();
        configuration.addUrls(resolveLocation());
        configuration.setScanners(scanners);

        LOGGER.debug("Initialized Reflections for resolved file location.");
        return new Reflections(configuration);
    }

    protected URL resolveLocation() {
        LOGGER.debug(
                "Resolving the following file location for bundle: {}",
                bundle.getSymbolicName()
        );
        URL resolved;

        try {
            resolved = new URL(bundle.getLocation());
        } catch (MalformedURLException e) {
            LOGGER.error("Failed to resolve URL from file: {}", bundle.getLocation());
            resolved = null;
        }

        LOGGER.debug(
                "Resolved the following file location for bundle: {}",
                bundle.getSymbolicName()
        );

        return resolved;
    }

    protected List<Class> getClasses(Class<? extends Annotation> annotation) {
        LOGGER.debug("Searching for classes with annotations: {}", annotation.getName());

        Reflections reflections = configureReflection(new TypeAnnotationsScanner());
        Set<String> set = reflections.getStore().getTypesAnnotatedWith(annotation.getName());
        List<Class> classes = new ArrayList<>(set.size());

        for (String className : set) {
            try {
                classes.add(bundle.loadClass(className));
            } catch (ClassNotFoundException e) {
                LOGGER.error(
                        "Failed to load class {} from bundle {}",
                        className, bundle.getSymbolicName()
                );
            }
        }

        LOGGER.debug("Searched for classes with annotations: {}", annotation.getName());
        LOGGER.trace("Found {} classes with annotations: {}", classes.size(), annotation.getName());

        return classes;
    }

    protected List<Method> getMethods(Class<? extends Annotation> annotation) {
        LOGGER.debug("Searching for methods with annotations: {}", annotation.getName());

        Reflections reflections = configureReflection(new MethodAnnotationsScanner());
        List<Method> annotatedMethods = new ArrayList<>(reflections.getMethodsAnnotatedWith(annotation));

        LOGGER.debug("Searched for methods with annotations: {}", annotation.getName());
        LOGGER.trace("Found {} methods with annotations: {}", annotatedMethods.size(), annotation.getName());

        return annotatedMethods;
    }
}
