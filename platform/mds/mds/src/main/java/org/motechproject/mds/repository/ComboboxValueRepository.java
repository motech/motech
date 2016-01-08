package org.motechproject.mds.repository;

import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.util.Constants;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.List;

/**
 * Responsible for fetching possible values for a combobox from the database.
 * This repository is only defined in the MDS entities bundle.
 */
public class ComboboxValueRepository extends AbstractRepository {

    /**
     * Retrieves all values for a multi-select combobox given its table name in the database.
     * Multi-select comboboxes have values stored in a separate table, hence we need the table name.
     * @param cbTableName the name of the combobox tables
     * @return all values for the combobox currently in the database
     */
    public List<String> getComboboxValuesForCollection(String cbTableName) {
        PersistenceManager pm = getPersistenceManager();

        final String tableNameForDb = usingPsql() ? doubleQuote(cbTableName) : cbTableName;
        final String elementField = usingPsql() ? "\"ELEMENT\"" : "element";

        // we need to execute sql, since jdoql won't allow multi-value fields in the result
        // tableName is safe since it comes from the metadata, no need for params
        Query query = pm.newQuery(Constants.Util.SQL_QUERY,
                String.format("SELECT DISTINCT %1$s FROM %2$s WHERE %1$s IS NOT NULL AND %1$s <> '' ORDER BY %1$s ASC",
                        elementField, tableNameForDb));

        return (List<String>) query.execute();
    }

    /**
     * Retrieves all values for a single-select combobox.
     * @param entityDto the entity to which the combobox belongs to
     * @param cbFieldDto the field representing the combobox
     * @return all values for the combobox currently in the database
     */
    public List<String> getComboboxValuesForStringField(EntityDto entityDto, FieldDto cbFieldDto) {
        PersistenceManager pm = getPersistenceManager();

        // MDS must ensure that these are valid
        Query query = pm.newQuery(
                String.format("SELECT DISTINCT %1$s FROM %2$s WHERE %1$s != null && %1$s.length() > 0 ORDER BY %1$s ASC",
                        cbFieldDto.getBasic().getName(), entityDto.getClassName()));

        return (List<String>) query.execute();
    }
}