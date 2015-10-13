package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.ex.entity.EntityNotFoundException;
import org.motechproject.mds.ex.field.FieldNotFoundException;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.ComboboxValueRepository;
import org.motechproject.mds.service.ComboboxValueService;
import org.motechproject.mds.service.MetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the combobox service. Uses {@link ComboboxValueRepository} for retrieval
 * of combobox user supplied values from the database. For comboboxes that don't allow user supplied values,
 * no database queries are performed.
 */
public class ComboboxValueServiceImpl implements ComboboxValueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComboboxValueServiceImpl.class);

    @Autowired
    private ComboboxValueRepository cbValueRepository;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private AllEntities allEntities;

    @Override
    @Transactional
    public List<String> getAllValuesForCombobox(String entityClassName, String fieldName) {
        Entity entity = allEntities.retrieveByClassName(entityClassName);
        if (entity == null) {
            throw new EntityNotFoundException(entityClassName);
        }

        Field field = entity.getField(fieldName);
        if (field == null) {
            throw new FieldNotFoundException(entityClassName, fieldName);
        } else if (!field.getType().isCombobox()) {
            throw new IllegalArgumentException("Field " + fieldName + "in entity " + entityClassName +
                    " is not a combobx field");
        }

        return getAllValuesForCombobox(entity, field);
    }

    @Override
    @Transactional
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
            try {
                List<String> optionsFromDb;

                if (cbHolder.isAllowMultipleSelections()) {
                    String cbTableName = metadataService.getComboboxTableName(entity.getClassName(), field.getName());
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
            }
        }

        return new ArrayList<>(options);
    }
}
