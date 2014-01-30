package org.motechproject.mds.init;

import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.SettingOptionsMapping;
import org.motechproject.mds.domain.TypeSettingsMapping;
import org.motechproject.mds.domain.TypeValidationMapping;
import org.motechproject.mds.domain.ValidationCriterionMapping;
import org.motechproject.mds.repository.AllFieldTypes;
import org.motechproject.mds.repository.AllTypeSettingsMappings;
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
import java.util.List;

/**
 * This class is responsible for inserting initial validation data into the database
 */
@Component
public class MdsInitialDataLoader extends TransactionCallbackWithoutResult {

    private static final Logger LOG = LoggerFactory.getLogger(MdsInitialDataLoader.class);

    @Autowired
    private AllTypeValidationMappings allTypeValidationMappings;

    @Autowired
    private AllFieldTypes allFieldTypes;

    @Autowired
    private AllTypeSettingsMappings allTypeSettingsMappings;

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
        initTypeSettings();
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

    private void initTypeSettings() {
        initTypeSettings(List.class.getName(), List.class.getName(), "mds.form.label.values", "", "REQUIRE");
        initTypeSettings(List.class.getName(), "java.lang.Boolean", "mds.form.label.allowUserSupplied", "false");
        initTypeSettings(List.class.getName(), "java.lang.Boolean", "mds.form.label.allowMultipleSelections", "false");
        initTypeSettings("java.lang.Double", "java.lang.Integer", "mds.form.label.precision", "9",
                "REQUIRE", "POSITIVE");
        initTypeSettings("java.lang.Double", "java.lang.Integer", "mds.form.label.scale", "2",
                "REQUIRE", "POSITIVE");
    }

    private void initTypeSettings(String typeClass, String valueTypeClass, String name,
                                  String value, String... settingsOptionsNames) {
        AvailableFieldTypeMapping type = allFieldTypes.getByClassName(typeClass);
        AvailableFieldTypeMapping valueType = allFieldTypes.getByClassName(valueTypeClass);

        List<SettingOptionsMapping> settingsOptions = settingsOptions(settingsOptionsNames);

        TypeSettingsMapping typeSettingsFromDb = getExistingSettingForType(type, name);

        if (typeSettingsFromDb == null) {
            TypeSettingsMapping typeSettings = new TypeSettingsMapping(name, value, valueType, type,
                   settingsOptions.toArray(new SettingOptionsMapping[settingsOptions.size()]));

            allTypeSettingsMappings.save(typeSettings);
        }
    }

    private List<SettingOptionsMapping> settingsOptions(String... names) {
        List<SettingOptionsMapping> settingsOptions = new ArrayList<>();
        if (names != null) {
            for (String name : names) {
                settingsOptions.add(new SettingOptionsMapping(name));
            }
        }
        return settingsOptions;
    }

    private ValidationCriterionMapping criterion(String displayName, Class<?> clazz) {
        AvailableFieldTypeMapping type = allFieldTypes.getByClassName(clazz.getName());
        return new ValidationCriterionMapping(displayName, "", false, null, type);
    }

    private TypeSettingsMapping getExistingSettingForType(AvailableFieldTypeMapping type, String name) {
        List<TypeSettingsMapping> typeSettingsList = allTypeSettingsMappings.getSettingsForType(type);
        for (TypeSettingsMapping typeSettings : typeSettingsList) {
            if (name.equals(typeSettings.getName())) {
                return typeSettings;
            }
        }
        return null;
    }
}
