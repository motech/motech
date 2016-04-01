package org.motechproject.bundle.extender;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Created by pawel on 30.03.16.
 */
public class ValidationProviderBeanPostProcessor implements BeanPostProcessor {

    private static final String PROVIDER_CLASSNAME = "org.apache.bval.jsr303.ApacheValidationProvider";

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof LocalValidatorFactoryBean) {
            LocalValidatorFactoryBean lvfb = (LocalValidatorFactoryBean) bean;

            ClassLoader ctxCl = Thread.currentThread().getContextClassLoader();
            try {
                Class validatorClass = ctxCl.loadClass("org.apache.bval.jsr303.ApacheValidationProvider");
                lvfb.setProviderClass(validatorClass);
            } catch (ClassNotFoundException e) {
                throw new BeanCreationException(String.format("Unable to load validator provider class %s, from ClassLoader %s",
                        PROVIDER_CLASSNAME, ctxCl.toString()), e);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
