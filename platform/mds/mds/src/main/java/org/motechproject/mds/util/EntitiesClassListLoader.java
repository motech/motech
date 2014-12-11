package org.motechproject.mds.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.service.JarGeneratorService;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility classes used in the MDS Entities Bundle for reading the txt files
 * containing class names.
 */
public final class EntitiesClassListLoader {

    public static Set<String> entities() {
        return loadLines(new ClassPathResource(JarGeneratorService.ENTITY_LIST_FILE));
    }

    public static Set<String> entitiesWithHistory() {
        return loadLines(new ClassPathResource(JarGeneratorService.HISTORY_LIST_FILE));
    }

    public static String entitiesStr() {
        return toStr(entities());
    }

    public static String entitiesWithHistoryStr() {
        return toStr(entitiesWithHistory());
    }

    private static Set<String> loadLines(ClassPathResource resource) {
        Set<String> classes = new HashSet<>();

        if (resource.exists()) {
            try (InputStream in = resource.getInputStream()) {
                for (Object line : IOUtils.readLines(in)) {
                    String className = (String) line;
                    classes.add(className);
                }
            } catch (IOException e) {
                throw new IllegalStateException("Unable to read " + resource.getFilename(), e);
            }
        } else {
            throw new IllegalStateException("Resource does not exist " + resource.getFilename());
        }

        return classes;
    }

    private static String toStr(Set<String> classes) {
        return StringUtils.join(classes, ',');
    }

    private EntitiesClassListLoader() {
    }
}
