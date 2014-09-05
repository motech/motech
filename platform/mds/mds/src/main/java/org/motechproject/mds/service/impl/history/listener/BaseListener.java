package org.motechproject.mds.service.impl.history.listener;

import org.apache.commons.io.IOUtils;
import org.motechproject.mds.service.JarGeneratorService;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseListener {

    protected Class[] entityClasses(){
        Set<Class> classes = new HashSet<>();

        ClassPathResource resource = new ClassPathResource(JarGeneratorService.ENTITY_LIST_FILE);

        if (resource.exists()) {
            try (InputStream in = resource.getInputStream()) {
                for (Object line : IOUtils.readLines(in)) {
                    String className = (String) line;
                    Class clazz = getClass().getClassLoader().loadClass(className);
                    classes.add(clazz);
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new IllegalStateException("Unable to initialize " + getName() + " the listener", e);
            }
        } else {
            throw new IllegalStateException("Unable to read entity classes list");
        }

        return classes.toArray(new Class[classes.size()]);
    }

    protected abstract String getName();
}
