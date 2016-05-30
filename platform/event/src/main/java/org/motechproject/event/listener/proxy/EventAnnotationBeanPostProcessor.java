package org.motechproject.event.listener.proxy;

import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.event.listener.annotations.MotechListenerAbstractProxy;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.event.listener.annotations.MotechListenerNamedParametersProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Provides the <code>BeanPostProcessor</code> implementation for processing event annotations.
 *
 * @author yyonkov
 */
public class EventAnnotationBeanPostProcessor implements DestructionAwareBeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventAnnotationBeanPostProcessor.class);

    private EventListenerRegistryService eventListenerRegistry;

    public EventAnnotationBeanPostProcessor() {
    }

    /**
     * @param eventListenerRegistryService the service for event listeners.
     */
    public EventAnnotationBeanPostProcessor(EventListenerRegistryService eventListenerRegistryService) {
        this.eventListenerRegistry = eventListenerRegistryService;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * {@inheritDoc}. Additionally, it starts processing event annotations.
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        processAnnotations(bean, beanName);
        return bean;
    }

    /**
     * Processes the {@link MotechListener} annotation from beans in the applicationContext
     *
     * @param applicationContext the central interface to provide configuration for an application
     */
    public void processAnnotations(ApplicationContext applicationContext) {
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            Object bean = applicationContext.getBean(beanName);
            processAnnotations(bean, beanName);
        }
    }

    private void processAnnotations(final Object bean, final String beanName) {
        if (bean == null) {
            return;
        }

        //Get declared methods from class without superclass methods.
        for (Method method : bean.getClass().getDeclaredMethods()) {
            Method methodOfOriginalClassIfProxied = ReflectionUtils.findMethod(AopUtils.getTargetClass(bean), method.getName(), method.getParameterTypes());

            if (methodOfOriginalClassIfProxied != null) {
                MotechListener annotation = methodOfOriginalClassIfProxied.getAnnotation(MotechListener.class);
                if (annotation != null) {
                    final List<String> subjects = Arrays.asList(annotation.subjects());
                    MotechListenerAbstractProxy proxy = null;
                    switch (annotation.type()) {
                        case MOTECH_EVENT:
                            proxy = new MotechListenerEventProxy(getFullyQualifiedBeanName(bean.getClass(), beanName), bean, method);
                            break;
                        case NAMED_PARAMETERS:
                            proxy = new MotechListenerNamedParametersProxy(getFullyQualifiedBeanName(bean.getClass(), beanName), bean, method);
                            break;
                        default:
                    }

                    LOGGER.info(String.format("Registering listener type(%20s) bean: %s, method: %s, for subjects: "
                            + "%s", annotation.type().toString() + ":" + beanName, bean.getClass().getName(),
                            method.toGenericString(), subjects));

                    if (eventListenerRegistry != null) {
                        eventListenerRegistry.registerListener(proxy, subjects);
                    } else {
                        LOGGER.error("Null eventListenerRegistry.  Unable to register listener");
                    }
                }
            }
        }
    }

    /**
     * Registers the event listeners (hack because we are running spring embedded in an OSGi module)
     * for the beans.
     *
     * @param beans the map contains the bean and its name.
     */
    public static void registerHandlers(Map<String, Object> beans) {
        EventAnnotationBeanPostProcessor processor = new EventAnnotationBeanPostProcessor();
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            processor.postProcessAfterInitialization(entry.getValue(), entry.getKey());
        }
    }

    /**
     * {@inheritDoc}. Additionally, it removes all event listeners for the bean.
     */
    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) {
        clearListenerForBean(getFullyQualifiedBeanName(bean.getClass(), beanName));
    }

    private void clearListenerForBean(String beanName) {
        if (eventListenerRegistry != null) {
            eventListenerRegistry.clearListenersForBean(beanName);
        }
    }

    /**
     * Removes all event listeners from the applicationContext.
     *
     * @param applicationContext the central interface to provide configuration for an application
     */
    public void clearListeners(ApplicationContext applicationContext) {
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            clearListenerForBean(getFullyQualifiedBeanName(applicationContext.getType(beanName), beanName));
        }
    }

    //TODO:keeping required false for now, should be removed.
    @Autowired(required = false)
    public void setEventListenerRegistry(EventListenerRegistryService eventListenerRegistry) {
        this.eventListenerRegistry = eventListenerRegistry;
    }

    private String getFullyQualifiedBeanName(Class<?> beanClass, String beanName) {
        return beanClass.getName() + ":" + beanName;
    }
}
