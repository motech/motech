package org.motechproject.mds.helper;

import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.repository.ComboboxValueRepository;
import org.motechproject.mds.repository.MetadataHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class ComboboxValueHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComboboxValueHelper.class);

    @Autowired
    private ComboboxValueRepository cbValueRepository;

    @Autowired
    private MetadataHelper metadataRepository;

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
                    String cbTableName = metadataRepository.getComboboxTableName(entity, field);
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
