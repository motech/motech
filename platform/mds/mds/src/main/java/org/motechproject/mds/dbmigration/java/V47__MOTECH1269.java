package org.motechproject.mds.dbmigration.java;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.helper.ClassTableName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_CLASS;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATIONSHIP_COLLECTION_TYPE;

/**
 * Migrates old history to new history.
 */
public class V47__MOTECH1269 { // NO CHECKSTYLE Bad format of member name

    private static final Logger LOGGER = LoggerFactory.getLogger(V47__MOTECH1269.class);

    private static final String POSTGRES = "PostgreSQL";
    private static final String MYSQL_ID_TYPE = "bigint(20)";
    private static final String PSQL_ID_TYPE = "bigint";

    private static final String FIELD_ID = "field_id_OID";
    private static final String ENTITY_ID = "entity_id_OID";
    private static final String SUFFIX_ID = "_ID";
    private static final String SUFFIX_OID = "_id_OID";
    private static final String SUFFIX_OWN = "_id_OWN";
    private static final String SUFFIX_HISTORY = "__History_ID";
    private static final String SUFFIX_TRASH = "__Trash_ID";
    private static final String SUFFIX_IDX = "_INTEGER_IDX";
    private static final String ENTITY = "Entity";
    private static final String FIELD = "Field";
    private static final String FIELD_METADATA = "FieldMetadata";
    private static final String ID = "id";
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String CLASS_NAME = "className";
    private static final String HISTORY_TABLE = "__HISTORY";
    private static final String TRASH_TABLE = "__TRASH";

    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";

    private JdbcTemplate jdbc;
    private boolean isPsql;

    public void migrate(JdbcTemplate jdbcTemplate) throws SQLException {
        jdbc = jdbcTemplate;
        isPsql = StringUtils.equals(jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductName(),
                POSTGRES);

        List<Map<String, Object>> result = getFieldMetadataWithCollectionType();

        for (Map<String, Object> row : result) {
            Long fieldId = (Long) row.get(FIELD_ID);

            String tableName = getTableNameRelatedToField(fieldId, HISTORY_TABLE);
            String relatedTableName = getTableNameEntityWithField(fieldId, HISTORY_TABLE);

            if (checkIfExists(tableName) && checkIfExists(relatedTableName)) {
                HistoryFk historyFk = getHistoryRelationship(tableName, relatedTableName);

                if (historyFk != null) {
                    boolean isList = List.class.getName().equals(row.get(VALUE));
                    createAndFillHistoryRelationshipTable(historyFk, isList);
                    removeNotNullInOldColumn(historyFk);
                }
            }

            tableName = getTableNameRelatedToField(fieldId, TRASH_TABLE);
            relatedTableName = getTableNameEntityWithField(fieldId, TRASH_TABLE);

            if (checkIfExists(tableName) && checkIfExists(relatedTableName)) {
                TrashOneToMany trashOneToMany = getRelation(tableName, relatedTableName);

                if (trashOneToMany != null) {
                    String relatedField = getRelatedClassName(fieldId);
                    boolean isList = List.class.getName().equals(row.get(VALUE));
                    createAndFillTrashRelationshipTable(trashOneToMany, relatedField, isList);
                }
            }
        }

        for (String table : getHistoryTables()) {
            // for each Foreign Key to a different history table
            List<HistoryFk> list = getHistoryFksToMigrate(table, SUFFIX_OID);
            list.addAll(getHistoryFksToMigrate(table, SUFFIX_OWN));
            for (HistoryFk fk : list) {
                migrateHistoryFk(fk);
            }
        }
    }

    private void removeNotNullInOldColumn(HistoryFk historyFk) {
        String sql = "ALTER TABLE " + addQuotes(historyFk.table);
        if (isPsql) {
            sql = sql + " ALTER COLUMN " + addQuotes(historyFk.oldColumn) + " DROP NOT NULL";
        } else {
            sql = sql + " MODIFY COLUMN " + historyFk.oldColumn + " bigint(20) DEFAULT NULL";
        }
        jdbc.execute(sql);
    }

    private TrashOneToMany getRelation(String table, String relatedTable) throws SQLException {
        Connection connection = jdbc.getDataSource().getConnection();
        DatabaseMetaData dbmd = connection.getMetaData();

        ResultSet foreignKeys = dbmd.getImportedKeys(connection.getCatalog(), null, table);

        TrashOneToMany result = null;

        while (foreignKeys.next()) {
            String pkTableName = foreignKeys.getString("PKTABLE_NAME");
            String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");

            String suffix;

            if (fkColumnName.contains(SUFFIX_OID)) {
                suffix = SUFFIX_OID;
            } else if (fkColumnName.contains(SUFFIX_OWN)) {
                suffix = SUFFIX_OWN;
            } else {
                break;
            }

            if (pkTableName.endsWith("__TRASH")) {
                String collectionName;
                String listIndex = columnEndsWith(table, SUFFIX_IDX);

                if (listIndex != null) {
                    collectionName = listIndex.replace(SUFFIX_IDX, "");
                } else {
                    collectionName = fkColumnName.replace(suffix, "");
                }

                if (pkTableName.equals(relatedTable)) {
                    result = new TrashOneToMany(table, relatedTable, fkColumnName, collectionName);
                    break;
                }
            }
        }

        return result;
    }

    private void createAndFillTrashRelationshipTable(TrashOneToMany trashOneToMany, String relatedClass, boolean isList) {
        String newTableName = trashOneToMany.relatedTable + "_" + trashOneToMany.fieldName;

        String sql;
        String query;

        if (isList) {
            sql = "CREATE TABLE IF NOT EXISTS " + addQuotes(newTableName) + " (" +
                    addQuotes(relatedClass + SUFFIX_TRASH) + " " +idType() + " NOT NULL," +
                    addQuotes(trashOneToMany.fieldName + SUFFIX_ID) + " " + idType() + " DEFAULT NULL," +
                    addQuotes("IDX") + " " + idType() + " NOT NULL," +
                    "PRIMARY KEY (" + addQuotes(relatedClass + SUFFIX_TRASH) + ", " + addQuotes("IDX") + ")," +
                    addKeyIfMySQL(addQuotes(newTableName + "_N49"), addQuotes(relatedClass + SUFFIX_TRASH)) +
                    "CONSTRAINT " + addQuotes(newTableName + "_FK1") + " FOREIGN KEY (" + addQuotes(relatedClass + SUFFIX_TRASH) + ") " +
                    "REFERENCES " + addQuotes(trashOneToMany.relatedTable) + " (" + addQuotes("id") + "));";

            query = "INSERT INTO " + addQuotes(newTableName) + " SELECT " + addQuotes(trashOneToMany.relatedColumn) + ", " +
                    addQuotes("id") + ", " + addQuotes(trashOneToMany.fieldName + "_INTEGER_IDX") +
                    FROM + addQuotes(trashOneToMany.table) + WHERE + addQuotes(trashOneToMany.relatedColumn) + " IS NOT NULL;";
        } else {
            sql = "CREATE TABLE IF NOT EXISTS " + addQuotes(newTableName) + " (" +
                    addQuotes(relatedClass + SUFFIX_TRASH) + " " +idType() + " NOT NULL," +
                    addQuotes(trashOneToMany.fieldName + SUFFIX_ID) + " " + idType() + " DEFAULT NULL," +
                    "PRIMARY KEY (" + addQuotes(relatedClass + SUFFIX_TRASH) + ", " + addQuotes(trashOneToMany.fieldName + SUFFIX_ID) + ")," +
                    addKeyIfMySQL(addQuotes(newTableName + "_N49"), addQuotes(relatedClass + SUFFIX_TRASH)) +
                    "CONSTRAINT " + addQuotes(newTableName + "_FK1") + " FOREIGN KEY (" + addQuotes(relatedClass + SUFFIX_TRASH) + ") " +
                    "REFERENCES " + addQuotes(trashOneToMany.relatedTable) + " (" + addQuotes("id") + "));";

            query = "INSERT INTO " + addQuotes(newTableName) + " SELECT " + addQuotes(trashOneToMany.relatedColumn) + ", " +
                    addQuotes("id") + FROM + addQuotes(trashOneToMany.table) +
                    WHERE + addQuotes(trashOneToMany.relatedColumn) + " IS NOT NULL;";
        }

        LOGGER.debug("Creating new table {}", newTableName);
        jdbc.execute(sql);

        LOGGER.debug("Migrating information about history to outer table {}", newTableName);
        int count = jdbc.update(query);
        LOGGER.debug("Migrated {} rows to outer table {}", count, newTableName);
    }

    private HistoryFk getHistoryRelationship(String tableName, String relatedTableName) throws SQLException {
        HistoryFk result = null;
        List<HistoryFk> list = getHistoryFksToMigrate(tableName, SUFFIX_OID);
        list.addAll(getHistoryFksToMigrate(tableName, SUFFIX_OWN));

        for (HistoryFk historyFk : list) {
            if (historyFk.relatedTable.equals(relatedTableName)) {
                result = historyFk;
            }
        }

        return result;
    }

    private String getTableNameEntityWithField(Long fieldId, String tableSuffix) {
        String sql = "SELECT * FROM " + addQuotes(ENTITY) + WHERE + addQuotes(ID) + " IN (SELECT " +
                addQuotes(ENTITY_ID) + FROM + addQuotes(FIELD) + WHERE + addQuotes(ID) + " = " + fieldId + ") LIMIT 1;";

        Map<String, Object> relatedEntity = jdbc.queryForList(sql).get(0);

        return getTableName(relatedEntity, tableSuffix);
    }

    private String getRelatedClassName(Long fieldId) {
        String sql = "SELECT * FROM " + addQuotes(ENTITY) + WHERE + addQuotes(ID) + " IN (SELECT " +
                addQuotes(ENTITY_ID) + FROM + addQuotes(FIELD) + WHERE + addQuotes(ID) + " = " + fieldId + ") LIMIT 1;";

        Map<String, Object> relatedEntity = jdbc.queryForList(sql).get(0);

        String fullClassName = (String) relatedEntity.get("className");


        return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
    }

    private String getTableNameRelatedToField(Long fieldId, String tableSuffix) {
        String sql = "SELECT * FROM " + addQuotes(ENTITY) + WHERE + addQuotes(CLASS_NAME) +
                " IN (SELECT " + addQuotes(VALUE) + FROM + addQuotes(FIELD_METADATA) + WHERE +
                addQuotes(KEY) + " = '" + RELATED_CLASS + "' AND " + addQuotes(FIELD_ID) + " = " + fieldId + ") LIMIT 1;";

        Map<String, Object> entity = jdbc.queryForList(sql).get(0);

        return getTableName(entity, tableSuffix);
    }

    private List<Map<String, Object>> getFieldMetadataWithCollectionType() {
        String sql = "SELECT " + addQuotes(FIELD_ID) + ", " + addQuotes(VALUE) + FROM + addQuotes(FIELD_METADATA) +
                WHERE + addQuotes(KEY) + " = '" + RELATIONSHIP_COLLECTION_TYPE + "';";

        return jdbc.queryForList(sql);
    }

    private void createAndFillHistoryRelationshipTable(HistoryFk historyFk, boolean isList) {
        String fieldName = historyFk.collectionName;
        String newTableName = historyFk.relatedTable + "_" + fieldName;
        String relatedFieldName = historyFk.relatedVersionColumn.replace("__HistoryCurrentVersion", "");
        relatedFieldName = Character.toUpperCase(relatedFieldName.charAt(0)) + relatedFieldName.substring(1);

        String sql;
        String query;

        if (isList) {
            sql = "CREATE TABLE IF NOT EXISTS " + addQuotes(newTableName) + " (" +
                    addQuotes(relatedFieldName + SUFFIX_HISTORY) + " " +idType() + " NOT NULL," +
                    addQuotes(fieldName + SUFFIX_ID) + " " + idType() + " DEFAULT NULL," +
                    addQuotes("IDX") + " " + idType() + " NOT NULL," +
                    "PRIMARY KEY (" + addQuotes(relatedFieldName + SUFFIX_HISTORY) + ", " + addQuotes("IDX") + ")," +
                    addKeyIfMySQL(addQuotes(newTableName + "_N49"), addQuotes(relatedFieldName + SUFFIX_HISTORY)) +
                    "CONSTRAINT " + addQuotes(newTableName + "_FK1") + " FOREIGN KEY (" + addQuotes(relatedFieldName + SUFFIX_HISTORY) + ") " +
                    "REFERENCES " + addQuotes(historyFk.relatedTable) + " (" + addQuotes("id") + "));";

            query = "INSERT INTO " + addQuotes(newTableName) + " SELECT " + addQuotes(historyFk.oldColumn) + ", " +
                    addQuotes(historyFk.versionColumn) + ", " + addQuotes(fieldName + "_INTEGER_IDX") +
                    FROM + addQuotes(historyFk.table) + WHERE + addQuotes(historyFk.oldColumn) + " IS NOT NULL;";
        } else {
            sql = "CREATE TABLE IF NOT EXISTS " + addQuotes(newTableName) + " (" +
                    addQuotes(relatedFieldName + SUFFIX_HISTORY) + " " +idType() + " NOT NULL," +
                    addQuotes(fieldName + SUFFIX_ID) + " " + idType() + " DEFAULT NULL," +
                    "PRIMARY KEY (" + addQuotes(relatedFieldName + SUFFIX_HISTORY) + ", " + addQuotes(fieldName + SUFFIX_ID) + ")," +
                    addKeyIfMySQL(addQuotes(newTableName + "_N49"), addQuotes(relatedFieldName + SUFFIX_HISTORY)) +
                    "CONSTRAINT " + addQuotes(newTableName + "_FK1") + " FOREIGN KEY (" + addQuotes(relatedFieldName + SUFFIX_HISTORY) + ") " +
                    "REFERENCES " + addQuotes(historyFk.relatedTable) + " (" + addQuotes("id") + "));";

            query = "INSERT INTO " + addQuotes(newTableName) + " SELECT " + addQuotes(historyFk.oldColumn) + ", " +
                    addQuotes(historyFk.versionColumn) + FROM + addQuotes(historyFk.table) + WHERE +
                    addQuotes(historyFk.oldColumn) + " IS NOT NULL;";
        }

        LOGGER.debug("Creating new table {}", newTableName);
        jdbc.execute(sql);

        LOGGER.debug("Migrating information about history to outer table {}", newTableName);
        int count = jdbc.update(query);
        LOGGER.debug("Migrated {} rows to outer table {}", count, newTableName);
    }

    private boolean checkIfExists(String tableName) throws SQLException {
        Connection connection = jdbc.getDataSource().getConnection();
        DatabaseMetaData dbmd = connection.getMetaData();

        ResultSet tableRs = dbmd.getTables(connection.getCatalog(), null, tableName, null);

        return tableRs.next();
    }

    private List<String> getHistoryTables() throws SQLException {
        Connection connection = jdbc.getDataSource().getConnection();
        DatabaseMetaData dbmd = connection.getMetaData();

        ResultSet tableRs = dbmd.getTables(connection.getCatalog(), null, "%__HISTORY", null);
        List<String> tables = new ArrayList<>();

        while (tableRs.next()) {
            tables.add(tableRs.getString(3));
        }

        return tables;
    }

    private List<HistoryFk> getHistoryFksToMigrate(String historyTable, String suffix) throws SQLException {
        Connection connection = jdbc.getDataSource().getConnection();
        DatabaseMetaData dbmd = connection.getMetaData();

        ResultSet foreignKeys = dbmd.getImportedKeys(connection.getCatalog(), null, historyTable);

        List<HistoryFk> keys = new ArrayList<>();
        while (foreignKeys.next()) {
            String pkTableName = foreignKeys.getString("PKTABLE_NAME");
            String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");

            if (pkTableName.endsWith("__HISTORY") && fkColumnName.endsWith(suffix)) {
                String newColumn = fkColumnName.replace(suffix, SUFFIX_ID);
                boolean newColExists = columnExists(foreignKeys.getMetaData(), newColumn);

                String relatedVersionColumn = getCurrentVersionColumn(pkTableName);

                String versionColumn = getCurrentVersionColumn(historyTable);

                String collectionName;
                String listIndex = columnEndsWith(historyTable, SUFFIX_IDX);

                if (listIndex != null && suffix.equals(SUFFIX_OID)) {
                    collectionName = listIndex.replace(SUFFIX_IDX, "");
                } else {
                    collectionName = fkColumnName.replace(suffix, "");
                }

                HistoryFk historyFk = new HistoryFk(historyTable, pkTableName, fkColumnName,
                        newColumn, newColExists, relatedVersionColumn, suffix, versionColumn, collectionName);
                keys.add(historyFk);
            }
        }

        return keys;
    }

    private boolean columnExists(ResultSetMetaData rsmd, String columnName) throws SQLException {
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            if (columnName.equals(rsmd.getColumnName(i))) {
                return true;
            }
        }
        return false;
    }

    private String columnEndsWith(String tableName, String suffix) throws SQLException {
        String result = null;

        List<Map<String, Object>> list = jdbc.queryForList("SHOW COLUMNS FROM " + tableName + ";");

        for (Map<String, Object> row : list) {
            String columnName = (String) row.get("Field");

            if (columnName.endsWith(suffix)) {
                result = columnName;
            }
        }

        return result;
    }

    private String getCurrentVersionColumn(String table)  {
        return jdbc.query("SELECT * FROM " + addQuotes(table) + " LIMIT 1", new ResultSetExtractor<String>() {
            @Override
            public String extractData(ResultSet rs) throws SQLException, DataAccessException {
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i < rsmd.getColumnCount(); i++) {
                    String colName = rsmd.getColumnName(i);
                    if (colName.toLowerCase(Locale.ENGLISH).endsWith("__historycurrentversion")) {
                        return colName;
                    }
                }
                throw new SQLException("History table is missing the current version column");
            }
        });
    }

    private void migrateHistoryFk(HistoryFk historyFk) {
        if (!historyFk.newColumnExists) {
            LOGGER.debug("Adding column {} to {}", historyFk.newColumn, historyFk.table);

            jdbc.execute(String.format("ALTER TABLE %s ADD COLUMN %s %s;",
                    addQuotes(historyFk.table), addQuotes(historyFk.newColumn), idType()));
        }

        LOGGER.debug("Migrating history field. Table: {}, old column: {}, new column: {}, related table: {}",
                historyFk.table, historyFk.oldColumn, historyFk.newColumn, historyFk.relatedTable);

        final String query = String.format("UPDATE %s SET %s = (SELECT %s from %s WHERE id = %s.%s);",
                addQuotes(historyFk.table), addQuotes(historyFk.newColumn), addQuotes(historyFk.relatedVersionColumn),
                addQuotes(historyFk.relatedTable), addQuotes(historyFk.table), addQuotes(historyFk.oldColumn));

        LOGGER.debug("Executing update query: {}", query);

        int updated = jdbc.update(query);
        LOGGER.debug("Updated {} history rows", updated);
    }

    private String getTableName(Map<String, Object> entity, String tableSuffix) {
        return ClassTableName.getTableName((String) entity.get("className"),
                (String) entity.get("module"),
                (String) entity.get("namespace"),
                (String) entity.get("tableName"),
                EntityType.STANDARD) + tableSuffix;
    }

    private String addQuotes(String value) {
        return isPsql ? String.format("\"%s\"", value) : String.format("`%s`", value);
    }

    private String addKeyIfMySQL(String key, String column) {
        return isPsql ? "" : "KEY " + key + " (" + column + "),";
    }

    private String idType() {
        return isPsql ? PSQL_ID_TYPE : MYSQL_ID_TYPE;
    }

    private class HistoryFk {
        private String table;
        private String relatedTable;
        private String oldColumn;
        private String newColumn;
        private boolean newColumnExists;
        private String relatedVersionColumn;
        private String suffix;
        private String versionColumn;
        private String collectionName;

        public HistoryFk(String table, String relatedTable, String oldColumn, String newColumn, boolean newColumnExists,
                         String relatedVersionColumn, String suffix, String versionColumn, String collectionName) {
            this.table = table;
            this.relatedTable = relatedTable;
            this.oldColumn = oldColumn;
            this.newColumn = newColumn;
            this.newColumnExists = newColumnExists;
            this.relatedVersionColumn = relatedVersionColumn;
            this.suffix = suffix;
            this.versionColumn = versionColumn;
            this.collectionName = collectionName;
        }
    }

    private class TrashOneToMany {
        private String table;
        private String relatedTable;
        private String relatedColumn;
        private String fieldName;


        private TrashOneToMany(String table, String relatedTable, String relatedColumn, String fieldName) {
            this.table = table;
            this.relatedTable = relatedTable;
            this.relatedColumn = relatedColumn;
            this.fieldName = fieldName;
        }
    }
}