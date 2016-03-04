package org.motechproject.mds.helper;

import org.motechproject.commons.sql.service.SqlDBManager;
import org.motechproject.mds.config.DeleteMode;
import org.motechproject.mds.config.MdsConfig;
import org.motechproject.mds.config.SettingsService;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.exception.entity.DataMigrationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManagerFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javax.jdo.Query.SQL;
import static org.motechproject.mds.util.Constants.Config.MYSQL_DRIVER_CLASSNAME;

/**
 * Responsible for migrating data of Combobox fields between correct tables. Transfers data from entity table to Combobox
 * table if selecting multiple values has been allowed. Also migrated data back to entity table if that option has been
 * disallowed.
 */
@Component
public class ComboboxDataMigrationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComboboxDataMigrationHelper.class);

    private static final String MIGRATION_TO_MULTI_SELECT_QUERY = "INSERT INTO %s SELECT id, %s, 0 FROM %s WHERE %s IS NOT NULL";
    private static final String CLEAR_TABLE_QUERY = "DELETE FROM %s";
    private static final String ADD_COLUMN_QUERY = "ALTER TABLE %s ADD COLUMN %s VARCHAR(255)";
    private static final String DROP_COLUMN_QUERY = "ALTER TABLE %s DROP COLUMN %s";
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS %s (%s BIGINT, %s VARCHAR(255), %s INT, PRIMARY KEY (%s, %s))";
    private static final String MYSQL_MIGRATION_TO_SINGLE_SELECT_QUERY = "UPDATE %s INNER JOIN %s on id = %s_OID SET %s = ELEMENT";
    private static final String POSTGRES_MIGRATION_TO_SINGLE_SELECT_QUERY = "UPDATE \"%s\" SET \"%s\" = \"ELEMENT\" FROM \"%s\" WHERE id = \"%s_OID\" and \"IDX\" = 0";

    private PersistenceManagerFactory persistenceManagerFactory;
    private SqlDBManager sqlDBManager;
    private SettingsService settingsService;
    private MdsConfig mdsConfig;

    /**
     * Compares given entity with it's draft and migrates data to proper table if multiple selections were allowed or
     * disallowed.
     *
     * @param parent  the parent entity
     * @param draft  the draft of the parent entity
     */
    @Transactional
    public void migrateComboboxDataIfNecessary(Entity parent, Entity draft) throws DataMigrationFailedException {
        List<Field> oldComboboxFields = parent.getComboboxFields();
        List<Field> comboboxFields = draft.getComboboxFields();
        Map<String, Boolean> changes = ComboboxHelper.comboboxesWithChangedSelectionType(oldComboboxFields, comboboxFields);

        LOGGER.info(String.format("Started data migration for entity %s.", parent.getName()));
        migrateEntityDataIfNecessary(parent, changes);
        LOGGER.info(String.format("Finished data migration for entity %s.", parent.getName()));
    }

    private void migrateEntityDataIfNecessary(Entity entity, Map<String, Boolean> changes) throws DataMigrationFailedException {
        for (Map.Entry<String, Boolean> change : changes.entrySet()) {

            LOGGER.info(String.format("Starting data migration for field %s in entity %s", change.getKey(), entity.getName()));

            String entityClassTable = ClassTableName.getTableName(entity, EntityType.STANDARD);
            String trashClassTable = ClassTableName.getTableName(entity, EntityType.TRASH);
            String historyClassTable = ClassTableName.getTableName(entity, EntityType.HISTORY);

            boolean trashMode = settingsService.getDeleteMode() == DeleteMode.TRASH;

            if (change.getValue()) {

                migrateDataToMultiSelectTable(entityClassTable, change.getKey());

                if (entity.isRecordHistory()) {
                    migrateDataToMultiSelectTable(historyClassTable, change.getKey());
                }

                if (trashMode) {
                    migrateDataToMultiSelectTable(trashClassTable, change.getKey());
                }

            } else {

                migrateDataToSingleSelectTable(entityClassTable, change.getKey());

                if (entity.isRecordHistory()) {
                    migrateDataToSingleSelectTable(historyClassTable, change.getKey());
                }

                if (trashMode) {
                    migrateDataToSingleSelectTable(trashClassTable, change.getKey());
                }
            }

            LOGGER.info(String.format("Data migration for field %s in entity %s is finished", change.getKey(), entity.getName()));
        }
    }

    private void migrateDataToSingleSelectTable(String dstTable, String field) throws DataMigrationFailedException {
        String srcTable = dstTable + "_" + field.toUpperCase();
        String mdsDataBase = mdsConfig.getDataDatabaseName();

        try {

            if (!sqlDBManager.hasColumn(mdsDataBase, dstTable, field)) {
                LOGGER.info(String.format("Adding column %s to table %s.", field, dstTable));
                executeQuery(prepareAddColumnQuery(dstTable, field));
            }
            LOGGER.info(String.format("Migrating data from table %s to table %s.", srcTable, dstTable));
            executeQuery(prepareMigrationToSingleSelectQuery(srcTable, dstTable, field));

        } catch (JDOException|SQLException e) {
            throw new DataMigrationFailedException(String.format("Error while migrating data to from %s to %s.", srcTable, dstTable), e);
        }
    }

    private String prepareAddColumnQuery(String dstTable, String field) {
        return String.format(ADD_COLUMN_QUERY, enquoteIfPostgres(dstTable), enquoteIfPostgres(field));
    }

    private void migrateDataToMultiSelectTable(String srcTable, String field) throws DataMigrationFailedException {

        String dstTable = srcTable + "_" + field.toUpperCase();

        try {

            LOGGER.info(String.format("Creating table %s if it doesn't exists.", dstTable));
            executeQuery(prepareCreateTableQuery(dstTable, field));

            LOGGER.info(String.format("Clearing table %s.", dstTable));
            executeQuery(prepareClearTableQuery(dstTable));

            LOGGER.info(String.format("Migrating data from table %s to table %s.", srcTable, dstTable));
            executeQuery(prepareMigrationToMultiSelectQuery(srcTable, dstTable, field));

            LOGGER.info(String.format("Dropping column %s in table %s.", field, srcTable));
            executeQuery(prepareDropColumnQuery(srcTable, field));

        } catch (JDOException e) {
            throw new DataMigrationFailedException(String.format("Error migrating single-select data from %s to %s.", srcTable, dstTable), e);
        }
    }

    private void executeQuery(String sql) {
        persistenceManagerFactory.getPersistenceManager().newQuery(SQL, sql).execute();
    }

    private String prepareDropColumnQuery(String srcTable, String field) {
        return String.format(DROP_COLUMN_QUERY, enquoteIfPostgres(srcTable), enquoteIfPostgres(field));
    }

    private String prepareClearTableQuery(String table) {
        return String.format(CLEAR_TABLE_QUERY, enquoteIfPostgres(table));
    }

    private String prepareMigrationToMultiSelectQuery(String srcTable, String dstTable, String field) {

        List<String> params = new ArrayList<>();

        params.add(enquoteIfPostgres(dstTable));
        params.add(enquoteIfPostgres(field));
        params.add(enquoteIfPostgres(srcTable));
        params.add(enquoteIfPostgres(field));

        return String.format(MIGRATION_TO_MULTI_SELECT_QUERY, params.toArray());
    }

    private String prepareMigrationToSingleSelectQuery(String srcTable, String dstTable, String field) {
        String query;

        if (persistenceManagerFactory.getConnectionDriverName().equals(MYSQL_DRIVER_CLASSNAME)) {
            query = String.format(MYSQL_MIGRATION_TO_SINGLE_SELECT_QUERY, dstTable, srcTable, field, field);
        } else {
            query = String.format(POSTGRES_MIGRATION_TO_SINGLE_SELECT_QUERY, dstTable, field, srcTable, field);
        }

        return query;
    }

    private String prepareCreateTableQuery(String table, String field) {

        List<String> params = new ArrayList<>();

        params.add(enquoteIfPostgres(table));
        params.add(enquoteIfPostgres(String.format("%s_OID", field)));
        params.add(enquoteIfPostgres("ELEMENT"));
        params.add(enquoteIfPostgres("IDX"));
        params.add(enquoteIfPostgres(String.format("%s_OID", field)));
        params.add(enquoteIfPostgres("IDX"));

        return String.format(CREATE_TABLE_QUERY, params.toArray());
    }

    private String enquoteIfPostgres(String string) {
        return persistenceManagerFactory.getConnectionDriverName().equals(MYSQL_DRIVER_CLASSNAME) ? string : "\"" + string + "\"";
    }

    @Autowired
    @Qualifier("dataPersistenceManagerFactory")
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    @Autowired
    public void setSqlDBManager(SqlDBManager sqlDBManager) {
        this.sqlDBManager = sqlDBManager;
    }

    @Autowired
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Autowired
    public void setMdsConfig(MdsConfig mdsConfig) {
        this.mdsConfig = mdsConfig;
    }
}
