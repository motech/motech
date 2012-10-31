package org.motechproject.event.listener.annotations;

import org.motechproject.event.listener.EventListenerRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Responsible for registering handlers based on annotations
 *
 * @author yyonkov
 */
@Component
public class EventAnnotationBeanPostProcessor implements DestructionAwareBeanPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private EventListenerRegistryService eventListenerRegistry;

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        processAnnotations(bean, beanName);
        return bean;
    }

    private void processAnnotations(final Object bean, final String beanName) {
        ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {

            @Override
            public void doWith(Method method) throws IllegalAccessException {
                Method methodOfOriginalClassIfProxied = ReflectionUtils.findMethod(AopUtils.getTargetClass(bean), method.getName(), method.getParameterTypes());

                if (methodOfOriginalClassIfProxied != null) {
                    MotechListener annotation = methodOfOriginalClassIfProxied.getAnnotation(MotechListener.class);
                    if (annotation != null) {
                        final List<String> subjects = Arrays.asList(annotation.subjects());
                        MotechListenerAbstractProxy proxy = null;

                        switch (annotation.type()) {
                            case ORDERED_PARAMETERS:
                                proxy = new MotechListenerOrderedParametersProxy(beanName, bean, method);
                                break;
                            case MOTECH_EVENT:
                                proxy = new MotechListenerEventProxy(beanName, bean, method);
                                break;
                            case NAMED_PARAMETERS:
                                proxy = new MotechListenerNamedParametersProxy(beanName, bean, method);
                                break;
                            default:
                        }

                        logger.info(String.format("Registering listener type(%20s) bean: %s , method: %s, for subjects: %s", annotation.type().toString(), beanName, method.toGenericString(), subjects));

                        if (eventListenerRegistry != null) {
                            eventListenerRegistry.registerListener(proxy, subjects);
                        }
                    }
                }
            }
        });
    }

    /**
     * Registers event handlers (hack because we are running spring embedded in an OSGi module)
     */
    public static void registerHandlers(Map<String, Object> beans) {
        EventAnnotationBeanPostProcessor processor = new EventAnnotationBeanPostProcessor();
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            processor.postProcessAfterInitialization(entry.getValue(), entry.getKey());
        }
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) {
        if (eventListenerRegistry != null) {
            eventListenerRegistry.clearListenersForBean(beanName);
        }
    }

    //TODO:keeping required false for now, should be removed.
    @Autowired (required = false)
    public void setEventListenerRegistry(EventListenerRegistryService eventListenerRegistry) {
        this.eventListenerRegistry = eventListenerRegistry;
    }
}
