package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.helper.MdsBundleHelper;
import org.motechproject.mds.repository.ComboboxValueRepository;
import org.motechproject.mds.service.MetadataService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is responsible for retrieving all possible values for comboboxes.
 * This is useful with comboboxes that take user supplied values, since the total number
 * of selections depends on what the users has entered. Instead of updating the data on each instance save,
 * this helper with retrieve the values from the database at read time using a distinct query.
 */
@Component
public class ComboboxValueHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComboboxValueHelper.class);

    @Autowired
    private ComboboxValueRepository cbValueRepository;

    @Autowired
    private BundleContext bundleContext;

    /**
     * Retrieves all values for a combobox. For string combobxes a DISTINCT query on the instances will
     * be performed. For comboboxes that are enums, only their settings will used.
     * @param entity the entity to which the combobox field belongs to
     * @param field the combobox field
     * @return all values for the combobox, as a list of strings
     */
    public List<String> getAllValuesForCombobox(Entity entity, Field field) {
        if (entity == null || field == null || !field.getType().isCombobox()) {
            throw new IllegalArgumentException("An existing entity and a combobox field are required");
        }

        ComboboxHolder cbHolder = new ComboboxHolder(field);

        Set<String> options = new LinkedHashSet<>();

        String[] values = cbHolder.getValues();
        options.addAll(Arrays.asList(values));

        // if this combobox allows user supplied values, then add all existing values from the database
        // as options
        if (cbHolder.isAllowUserSupplied()) {
            // we must switch the class loader here, since we need the MDS class loader for some of the queries
            ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
            try {
                ClassLoader mdsCl = MdsBundleHelper.getMdsBundleClassLoader(bundleContext);
                Thread.currentThread().setContextClassLoader(mdsCl);

                List<String> optionsFromDb;

                if (cbHolder.isAllowMultipleSelections()) {
                    String cbTableName = getComboboxTableName(entity, field);
                    optionsFromDb = cbValueRepository.getComboboxValuesForCollection(cbTableName);
                } else {
                    optionsFromDb = cbValueRepository.getComboboxValuesForStringField(entity, field);
                }

                options.addAll(optionsFromDb);
            } catch (RuntimeException e) {
                // we don't want to break in this case, so we just return the predefined values
                // after logging the exception
                LOGGER.error("Unable to retrieve combobox values from the database for field {} in entity {}",
                        field.getName(), entity.getClassName(), e);
            } finally {
                Thread.currentThread().setContextClassLoader(oldCl);
            }
        }

        return new ArrayList<>(options);
    }

    private String getComboboxTableName(Entity entity, Field field) {
        ServiceReference<MetadataService> ref = bundleContext.getServiceReference(MetadataService.class);
        if (ref == null) {
            throw new IllegalStateException("Metadata service unavailable");
        } else {
            MetadataService metadataService = bundleContext.getService(ref);
            return metadataService.getComboboxTableName(entity.getClassName(), field.getName());
        }
    }
}
