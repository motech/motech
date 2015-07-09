package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.InstanceLifecycleListeners;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.service.JdoListenerRegistryService;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * The <code>InstanceLifecycleListenersProcessor</code> provides a mechanism for processing
 * {@link org.motechproject.mds.annotations.InstanceLifecycleListeners} annotation of a single
 * bundle.
 *
 * @see org.motechproject.mds.annotations.InstanceLifecycleListeners
 */
@Component
public class InstanceLifecycleListenersProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceLifecycleListenersProcessor.class);

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
        Set<Class<?>> classes = ReflectionsUtil.getClasses(InstanceLifecycleListeners.class, bundle);

        for (Class cls : classes) {
            if (ReflectionsUtil.hasAnnotationClassLoaderSafe(cls, cls, Entity.class)) {
                jdoListenerRegistryService.registerEntityWithListeners(cls.getName());
            } else {
                LOGGER.error("InstanceLifecycleListeners annotation may not be applied to non entity class");
            }
        }
    }

    @Autowired
    public void setJdoListenerRegistryService(JdoListenerRegistryService jdoListenerRegistryService) {
        this.jdoListenerRegistryService = jdoListenerRegistryService;
    }
}
