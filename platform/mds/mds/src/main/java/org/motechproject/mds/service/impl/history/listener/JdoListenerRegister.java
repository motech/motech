package org.motechproject.mds.service.impl.history.listener;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.service.JarGeneratorService;
import org.springframework.core.io.ClassPathResource;

import javax.jdo.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class JdoListenerRegister {

    private static final String LISTENER_KEY_PREFIX = Constants.PROPERTY_INSTANCE_LIFECYCLE_LISTENER + '.';

    public Properties addTrashHistoryListeners(Properties properties) {
        Properties resultProps = new Properties();

        resultProps.putAll(properties);

        String entityClassesStr = entityClassesStr();
        resultProps.setProperty(LISTENER_KEY_PREFIX + TrashListener.class.getName(), entityClassesStr);

        return resultProps;
    }

    protected String entityClassesStr(){
        Set<String> classes = new HashSet<>();

        ClassPathResource resource = new ClassPathResource(JarGeneratorService.ENTITY_LIST_FILE);

        if (resource.exists()) {
            try (InputStream in = resource.getInputStream()) {
                for (Object line : IOUtils.readLines(in)) {
                    String className = (String) line;
                    classes.add(className);
                }
            } catch (IOException e) {
                throw new IllegalStateException("Unable to initialize the JDO listeners", e);
            }
        } else {
            throw new IllegalStateException("Unable to read entity classes list when registering JDO listeners");
        }

        return StringUtils.join(classes, ',');
    }
}
