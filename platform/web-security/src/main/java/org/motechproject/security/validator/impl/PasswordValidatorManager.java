package org.motechproject.security.validator.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.security.validator.PasswordValidator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * A class responsible for retrieving validators from the bundle context.
 * It also registers the validators provided by MOTECH upon initialization.
 */
@Component
public class PasswordValidatorManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordValidatorManager.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private BundleContext bundleContext;

    private NoneValidator noneValidator;

    @PostConstruct
    public void init() {
        noneValidator = new NoneValidator(messageSource);

        registerValidator(noneValidator);

        LOGGER.info("Initial validators registered");
    }

    /**
     * Retrieves a validator by the name it was registered with. The service property with the kye org.motechproject.security.validator_name
     * is treated as the name of the validator.
     * @param name the name of the validator
     * @return the validator, or null if it is missing
     */
    public PasswordValidator getValidator(String name) {
        try {
            Collection<ServiceReference<PasswordValidator>> refs = bundleContext.getServiceReferences(PasswordValidator.class, null);

            for (ServiceReference<PasswordValidator> ref : refs) {
                PasswordValidator validator = bundleContext.getService(ref);

                if (StringUtils.equalsIgnoreCase(name, validator.getName())) {
                    return validator;
                }
            }

            LOGGER.warn("Validator with name {} not found", name);

            return null;
        } catch (InvalidSyntaxException e) {
            throw new IllegalStateException("Unable to create a service filter expression", e);
        }
    }

    /**
     * Returns a validator that will do no validation.
     * @return the validator
     */
    public NoneValidator noneValidator() {
        return noneValidator;
    }

    private void registerValidator(PasswordValidator validator) {
        bundleContext.registerService(PasswordValidator.class, validator, null);
    }
}
