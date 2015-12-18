package org.motechproject.mds.service;

import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;

import java.util.List;

/**
 * This service is responsible for retrieving all possible values for comboboxes.
 * This is useful with comboboxes that take user supplied values, since the total number
 * of selections depends on what the users has entered. Instead of updating the data on each instance save,
 * this service will retrieve the values from the database at read time using a distinct query.
 *
 * This service lives in the entities bundle, since it needs access to entity classes.
 */
public interface ComboboxValueService {

    /**
     * Retrieves all values for a combobox. For string comboboxes a DISTINCT query on the instances will
     * be performed. For comboboxes that are enums, only their settings will used.
     * @param entityClassName the class name of the entity that contains the combobox field
     * @param fieldName the name of the combobox field
     * @return all values for the combobox, as a list of strings
     */
    List<String> getAllValuesForCombobox(String entityClassName, String fieldName);

    /**
     * Retrieves all values for a combobox. For string comboboxes a DISTINCT query on the instances will
     * be performed. For comboboxes that are enums, only their settings will used.
     * @param entityDto the entity to which the combobox field belongs to
     * @param fieldDto the combobox field
     * @return all values for the combobox, as a list of strings
     */
    List<String> getAllValuesForCombobox(EntityDto entityDto, FieldDto fieldDto);
}
