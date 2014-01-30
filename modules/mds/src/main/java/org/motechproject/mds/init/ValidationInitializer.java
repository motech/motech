package org.motechproject.mds.init;

import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.TypeValidationMapping;
import org.motechproject.mds.domain.ValidationCriterionMapping;
import org.motechproject.mds.repository.AllFieldTypes;
import org.motechproject.mds.repository.AllTypeValidationMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is responsible for inserting initial validation data into the database
 */
@Component
public class ValidationInitializer extends TransactionCallbackWithoutResult {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationInitializer.class);

    @Autowired
    private AllTypeValidationMappings allTypeValidationMappings;

    @Autowired
    private AllFieldTypes allFieldTypes;

    @Autowired
    private JdoTransactionManager transactionManager;

    @PostConstruct
    public void init() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(this);
    }

    @Override
    protected void doInTransactionWithoutResult(TransactionStatus status) {
        initValidations();
    }

    public void initValidations() {
        initValidation(Integer.class.getName(),
                criterion("mds.field.validation.minValue", Integer.class),
                criterion("mds.field.validation.maxValue", Integer.class),
                criterion("mds.field.validation.mustBeInSet", String.class),
                criterion("mds.field.validation.cannotBeInSet", String.class));
        initValidation(Double.class.getName(),
                criterion("mds.field.validation.minValue", Integer.class),
                criterion("mds.field.validation.maxValue", Integer.class),
                criterion("mds.field.validation.mustBeInSet", String.class),
                criterion("mds.field.validation.cannotBeInSet", String.class));
        initValidation(String.class.getName(),
                criterion("mds.field.validation.regex", String.class),
                criterion("mds.field.validation.minLength", Integer.class),
                criterion("mds.field.validation.maxLength", Integer.class));
    }

    private void initValidation(String typeClass, ValidationCriterionMapping... criteria) {
        AvailableFieldTypeMapping type = allFieldTypes.getByClassName(typeClass);
        if (type != null && criteria != null) {

            TypeValidationMapping existingValidation = allTypeValidationMappings.getValidationForType(type);

            if (existingValidation == null) {
                LOG.debug("Creating validations for " + typeClass);

                TypeValidationMapping validation = new TypeValidationMapping(type, new ArrayList<>(Arrays.asList(criteria)));

                allTypeValidationMappings.save(validation);
            }
        }
    }

    private ValidationCriterionMapping criterion(String displayName, Class<?> clazz) {
        AvailableFieldTypeMapping type = allFieldTypes.getByClassName(clazz.getName());
        return new ValidationCriterionMapping(displayName, "", false, null, type);
    }
}
