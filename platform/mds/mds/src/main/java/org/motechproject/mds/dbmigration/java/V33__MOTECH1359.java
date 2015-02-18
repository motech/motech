package org.motechproject.mds.dbmigration.java;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.helper.ClassTableName;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.TypeHelper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Migrates values from fields being Map. Previously every map type different than Map<String, String>
 * was serialized into DB. Now MDS supports more types and this class migrates serialized values into
 * new separate tables.
 *
 * @see org.motechproject.mds.util.TypeHelper#isTypeSupportedInMap
 */
public class V33__MOTECH1359 { // NO CHECKSTYLE Bad format of member name

    private static final String POSTGRES = "PostgreSQL";

    private String keyColumnName;

    private boolean isPostgres;

    public void migrate(JdbcTemplate jdbcTemplate) throws SQLException {
        String fieldIdColumn = "field_id_OID";
        String tableNameColumn = "tableName";
        String className = "className";

        isPostgres = StringUtils.equals(jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductName(),
                POSTGRES);

        keyColumnName = isPostgres ? "\"key\"" : "`key`";

        String sqlFieldMetadata = String.format("SELECT value, %s FROM %s WHERE %s = ?", enquoteIfPostgres(fieldIdColumn),
                enquoteIfPostgres("FieldMetadata"), keyColumnName);
        List<Map<String, Object>> fieldsMetadata = jdbcTemplate.queryForList(sqlFieldMetadata, Constants.MetadataKeys.MAP_KEY_TYPE);

        String sqlFieldName = String.format("SELECT name FROM %s WHERE id = ?", enquoteIfPostgres("Field"));
        String sqlEntity = String.format("SELECT %s, module, namespace, %s FROM %s WHERE id = (SELECT %s FROM %s WHERE id = ?) ",
                enquoteIfPostgres(className), enquoteIfPostgres(tableNameColumn), enquoteIfPostgres("Entity"),
                enquoteIfPostgres("entity_id_OID"), enquoteIfPostgres("Field"));

        for (Map fieldMetadata : fieldsMetadata) {
            String keyClass = fieldMetadata.get("value").toString();

            if (TypeHelper.isTypeSupportedInMap(keyClass, true)) {
                sqlFieldMetadata = String.format("SELECT value FROM %s WHERE %s = ? AND %s = ?",
                        enquoteIfPostgres("FieldMetadata"), keyColumnName, enquoteIfPostgres(fieldIdColumn));
                Long fieldId = (Long) fieldMetadata.get(fieldIdColumn);

                String valueClass = jdbcTemplate.queryForObject(sqlFieldMetadata, String.class,
                        Constants.MetadataKeys.MAP_VALUE_TYPE, fieldId);

                // Map<String, String> was previously supported in MDS
                if (TypeHelper.isTypeSupportedInMap(valueClass, false) && !(keyClass.equals(String.class.getName()) &&
                        valueClass.equals(String.class.getName()))) {
                    String fieldName = jdbcTemplate.queryForObject(sqlFieldName, String.class, fieldId);
                    Map<String, Object> entity = jdbcTemplate.queryForMap(sqlEntity, fieldId);

                    String tableName = ClassTableName.getTableName((String) entity.get(className),
                            (String) entity.get("module"),
                            (String) entity.get("namespace"),
                            (String) entity.get(tableNameColumn),
                            EntityType.STANDARD);
                    String sqlValues = String.format("SELECT %s, id FROM %s", enquoteIfPostgres(fieldName),
                            enquoteIfPostgres(tableName));

                    List<Map<String, Object>> instances = jdbcTemplate.queryForList(sqlValues);

                    if (!instances.isEmpty()) {
                        insertValues(jdbcTemplate, tableName, fieldName, keyClass, valueClass, instances);
                    }
                }
            }
        }
    }

    private void insertValues(JdbcTemplate jdbcTemplate, String tableName, String fieldName, String keyClass,
                              String valueClass, List<Map<String, Object>> values) {
        String value = "VALUE";
        String id = "id_OID";
        String mapTableName = tableName + "_" + fieldName.toUpperCase();

        StringBuilder createTable = new StringBuilder();
        createTable.append("CREATE TABLE IF NOT EXISTS ").append(enquoteIfPostgres(mapTableName)).append("( ");
        createTable.append(enquoteIfPostgres(id)).append(" ").append((getSqlType(Long.class.getName()))).append(" NOT NULL, ");
        createTable.append(keyColumnName.toUpperCase()).append(" ").append(getSqlType(keyClass)).append(" NOT NULL, ");
        createTable.append(enquoteIfPostgres(value)).append(" ").append(getSqlType(valueClass)).append(" DEFAULT NULL, ");
        createTable.append("PRIMARY KEY (").append(enquoteIfPostgres(id)).append(", ").append(keyColumnName.toUpperCase()).append("), ");
        if (!isPostgres) {
            createTable.append("KEY ").append(mapTableName).append("_N49 (id_OID), ");
        }
        createTable.append("CONSTRAINT ").append(enquoteIfPostgres(mapTableName + "_FK1")).append(" FOREIGN KEY (");
        createTable.append(enquoteIfPostgres(id)).append(") REFERENCES ").append(enquoteIfPostgres(tableName)).append(" (id))");

        jdbcTemplate.execute(createTable.toString());

        String updateTable = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?,?,?)",
                enquoteIfPostgres(mapTableName), enquoteIfPostgres(id), keyColumnName.toUpperCase(), enquoteIfPostgres(value));

        for (Map<String, Object> map : values) {
            byte[] serializedValue = (byte[]) map.get(fieldName);
            if (serializedValue != null) {
                Map deserializedValue = (Map) SerializationUtils.deserialize(serializedValue);
                for (Object key : deserializedValue.keySet()) {
                    jdbcTemplate.update(updateTable, map.get(Constants.Util.ID_FIELD_NAME),
                            TypeHelper.parseMapValue(key, keyClass, true),
                            TypeHelper.parseMapValue(deserializedValue.get(key), valueClass, false));
                }
            }
        }
    }

    private String enquoteIfPostgres(String name) {
        return isPostgres ? String.format("\"%s\"", name) : name;
    }

    private String getSqlType(String clazz) {
        String sqlType = "";

        if (clazz.equals(String.class.getName())) {
            sqlType = isPostgres ? "varchar(255)" : "varchar(255) CHARACTER SET latin1 COLLATE latin1_bin";
        } else if (clazz.equals(Integer.class.getName())) {
            sqlType = isPostgres ? "int" : "int(11)";
        } else if (clazz.equals(Long.class.getName())) {
            sqlType = isPostgres ? "bigint" : "bigint(20)";
        }

        return sqlType;
    }
}
