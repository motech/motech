package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.entityinfo.EntityInfo;
import org.motechproject.mds.entityinfo.EntityInfoReader;
import org.motechproject.mds.exception.entity.EntityNotFoundException;
import org.motechproject.mds.exception.field.FieldNotFoundException;
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
    private EntityInfoReader entityInfoReader;

    @Override
    @Transactional
    public List<String> getAllValuesForCombobox(String entityClassName, String fieldName) {
        EntityInfo entityInfo = entityInfoReader.getEntityInfo(entityClassName);
        if (entityInfo == null) {
            throw new EntityNotFoundException(entityClassName);
        }

        FieldDto field = entityInfo.getField(fieldName).getField();
        if (field == null) {
            throw new FieldNotFoundException(entityClassName, fieldName);
        } else if (!field.getType().isCombobox()) {
            throw new IllegalArgumentException("Field " + fieldName + "in entity " + entityClassName +
                    " is not a combobx field");
        }

        return getAllValuesForCombobox(entityInfo.getEntity(), field);
    }

    @Override
    @Transactional
    public List<String> getAllValuesForCombobox(EntityDto entityDto, FieldDto fieldDto) {
        if (entityDto == null || fieldDto == null || !fieldDto.getType().isCombobox()) {
            throw new IllegalArgumentException("An existing entity and a combobox field are required");
        }

        ComboboxHolder cbHolder = new ComboboxHolder(entityDto, fieldDto);

        Set<String> options = new LinkedHashSet<>();

        String[] values = cbHolder.getValues();
        options.addAll(Arrays.asList(values));

        // if this combobox allows user supplied values, then add all existing values from the database
        // as options
        if (cbHolder.isAllowUserSupplied()) {
            try {
                List<String> optionsFromDb;

                if (cbHolder.isAllowMultipleSelections()) {
                    String cbTableName = metadataService.getComboboxTableName(entityDto.getClassName(), fieldDto.getBasic().getName());
                    optionsFromDb = cbValueRepository.getComboboxValuesForCollection(cbTableName);
                } else {
                    optionsFromDb = cbValueRepository.getComboboxValuesForStringField(entityDto, fieldDto);
                }

                options.addAll(optionsFromDb);
            } catch (RuntimeException e) {
                // we don't want to break in this case, so we just return the predefined values
                // after logging the exception
                LOGGER.error("Unable to retrieve combobox values from the database for field {} in entity {}",
                        fieldDto.getBasic().getName(), entityDto.getClassName(), e);
            }
        }

        return new ArrayList<>(options);
    }
}
