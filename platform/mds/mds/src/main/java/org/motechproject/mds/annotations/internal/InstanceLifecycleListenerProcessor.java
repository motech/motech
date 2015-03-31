package org.motechproject.mds.annotations.internal;

import org.apache.commons.lang.ArrayUtils;
import org.motechproject.mds.annotations.InstanceLifecycleListener;
import org.motechproject.mds.domain.InstanceLifecycleListenerType;
import org.motechproject.mds.listener.MotechLifecycleListener;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.service.JdoListenerRegistryService;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * The <code>InstanceLifecycleListenerProcessor</code> provides a mechanism for processing
 * {@link org.motechproject.mds.annotations.InstanceLifecycleListener} annotation of a single
 * bundle.
 *
 * @see org.motechproject.mds.annotations.InstanceLifecycleListener
 */
@Component
public class InstanceLifecycleListenerProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceLifecycleListenerProcessor.class);
    private static final int NUMBER_OF_PARAMETERS = 1;

    private JdoListenerRegistryService jdoListenerRegistryService;

    /**
     * Processes <code>InstanceLifecycleListener</code> annotations in the given bundle.
     * When the annotation is found, it is verified if following rules are fulfilled:
     * - array of <code>InstanceLifecycleListenerType</code> cannot be empty
     * - annotated methods have exactly one parameter
     *
     * @param bundle the bundle which is processed
     */
    public void processAnnotations(Bundle bundle) {
        Set<Method> methods = ReflectionsUtil.getMethods(InstanceLifecycleListener.class, bundle);

        for (Method method : methods) {
            InstanceLifecycleListener annotation = ReflectionsUtil.getAnnotationClassLoaderSafe(method, method.getDeclaringClass(), InstanceLifecycleListener.class);
            InstanceLifecycleListenerType[] types = annotation.value();

            if (ArrayUtils.isEmpty(types)) {
                LOGGER.error("InstanceLifecycleListener annotation for {} is specified but its value is missing.", method.toString());
            } else if (method.getParameterTypes().length != NUMBER_OF_PARAMETERS) {
                LOGGER.error("InstanceLifecycleListener annotation cannot be specified for method {}, because it does not have exactly " +
                        "{} parameter", method.toString(), NUMBER_OF_PARAMETERS);
            } else {
                String entity = method.getParameterTypes()[0].getName();
                MotechLifecycleListener listener = new MotechLifecycleListener(method.getDeclaringClass(), method.getName(),
                        entity, types);

                jdoListenerRegistryService.registerListener(listener);
            }
        }
    }

    @Autowired
    public void setJdoListenerRegistryService(JdoListenerRegistryService jdoListenerRegistryService) {
        this.jdoListenerRegistryService = jdoListenerRegistryService;
    }
}
