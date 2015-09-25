package org.motechproject.mds.repository;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.util.Constants;
import org.springframework.stereotype.Repository;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.List;

/**
 * Responsible for fetching possible values for a combobox from the database.
 */
@Repository
public class ComboboxValueRepository extends AbstractRepository {

    /**
     * Retrieves all values for a multi-select combobox given its table name in the database.
     * Multi-select comboboxes have values stored in a separate table, hence we need the table name.
     * @param cbTableName the name of the combobox tables
     * @return all values for the combobox currently in the database
     */
    public List<String> getComboboxValuesForCollection(String cbTableName) {
        PersistenceManager pm = getPersistenceManager();

        // we need to execute sql, since jdoql won't allow multi-value fields in the result
        // tableName is safe since it comes from the metadata, no need for params
        Query query = pm.newQuery(Constants.Util.SQL_QUERY, "SELECT DISTINCT element FROM " + cbTableName);

        return (List<String>) query.execute();
    }

    /**
     * Retrieves all values for a single-select combobox.
     * @param entity the entity to which the combobox belongs to
     * @param cbField the field representing the combobox
     * @return all values for the combobox currently in the database
     */
    public List<String> getComboboxValuesForStringField(Entity entity, Field cbField) {
        PersistenceManager pm = getPersistenceManager();

        // MDS must ensure that these are valid
        Query query = pm.newQuery(String.format("SELECT DISTINCT %s FROM %s", cbField.getName(),
                entity.getClassName()));

        return (List<String>) query.execute();
    }
}