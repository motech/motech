package org.motechproject.mds.util;

import com.google.common.collect.ImmutableSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Set;

/**
 * The <code>Constants</code> contains constant values used in MDS module. They are grouped by
 * their role.
 */
public final class Constants {

    /**
     * The <code>Roles</code> contains constant values related with security roles.
     */
    public static final class Roles {

        /**
         * Users with ‘Schema Access’ have the ability to view the Schema Editor tab of the UI.
         * Then can add new objects, delete existing objects and modify the fields on existing
         * objects.
         */
        public static final String SCHEMA_ACCESS = "mdsSchemaAccess";

        /**
         * Users with ‘Settings Access’ have the ability to view the Settings tab. From that tab
         * then can modify data retention policies as well as import and export schema and data.
         */
        public static final String SETTINGS_ACCESS = "mdsSettingsAccess";

        /**
         * Users with ‘Data Access’ have the ability to view the Data Browser tab. From that tab
         * then can search for objects within the system, view and modify the data stored in the
         * system.
         */
        public static final String DATA_ACCESS = "mdsDataAccess";

        /**
         * Spring security el expression to check if the given user has the 'Schema Access' role.
         *
         * @see #SCHEMA_ACCESS
         */
        public static final String HAS_SCHEMA_ACCESS = "hasRole('" + SCHEMA_ACCESS + "')";

        /**
         * Spring security el expression to check if the given user has the 'Settings Access' role.
         *
         * @see #SETTINGS_ACCESS
         */
        public static final String HAS_SETTINGS_ACCESS = "hasRole('" + SETTINGS_ACCESS + "')";

        /**
         * Spring security el expression to check if the given user has the 'Data Access' role.
         *
         * @see #DATA_ACCESS
         */
        public static final String HAS_DATA_ACCESS = "hasRole('" + DATA_ACCESS + "')";

        /**
         * Spring security el expression to check if the given user has the 'Schema Access' or
         * 'Data Access' roles.
         *
         * @see #SCHEMA_ACCESS
         * @see #DATA_ACCESS
         */
        public static final String HAS_DATA_OR_SCHEMA_ACCESS = "hasAnyRole('" + SCHEMA_ACCESS + "', '" + DATA_ACCESS + "')";

        /**
         * Spring security el expression to check if the given user has any of the MDS roles.
         *
         * @see #SCHEMA_ACCESS
         * @see #SETTINGS_ACCESS
         * @see #DATA_ACCESS
         */
        public static final String HAS_ANY_MDS_ROLE = "hasAnyRole('" + SCHEMA_ACCESS + "', '" + DATA_ACCESS + "', '" + SETTINGS_ACCESS + "')";

        private Roles() {
        }

    }

    /**
     * The <code>Packages</code> contains constant values related with packages inside MDS module.
     */
    public static final class Packages {

        /**
         * Constant <code>BASE</code> presents the base package for all pakcages inside MDS module.
         */
        public static final String BASE = "org.motechproject.mds";

        /**
         * Constant <code>ENTITY</code> presents a package for entity classes.
         *
         * @see #BASE
         */
        public static final String ENTITY = BASE + ".entity";

        /**
         * Constant <code>REPOSITORY</code> presents a package for repository classes.
         *
         * @see #BASE
         */
        public static final String REPOSITORY = BASE + ".repository";

        /**
         * Constant <code>SERVICE</code> presents a package for service interfaces.
         *
         * @see #BASE
         */
        public static final String SERVICE = BASE + ".service";

        /**
         * Constant <code>SERVICE_IMPL</code> presents a package for implementation of interfaces
         * defined in {@link #SERVICE} package.
         *
         * @see #BASE
         * @see #SERVICE
         */
        public static final String SERVICE_IMPL = SERVICE + ".impl";

        private Packages() {
        }
    }

    public static final class PackagesGenerated {

        /**
         * Constant <code>ENTITY</code> presents a package for generated entity classes.
         */
        public static final String ENTITY = Packages.BASE + ".entity";

        /**
         * Constant <code>REPOSITORY</code> presents a package for generated repository classes.
         *
         * @see #ENTITY
         */
        public static final String REPOSITORY = ENTITY + ".repository";

        /**
         * Constant <code>SERVICE</code> presents a package for generated service interfaces.
         *
         * @see #ENTITY
         */
        public static final String SERVICE = ENTITY + ".service";

        /**
         * Constant <code>SERVICE_IMPL</code> presents a package for generated implementation of interfaces
         * defined in {@link #SERVICE} package.
         *
         * @see #SERVICE
         */
        public static final String SERVICE_IMPL = SERVICE + ".impl";

        private PackagesGenerated() {
        }
    }

    /**
     * The <code>Config</code> contains constant values related with properties inside files:
     * <ul>
     * <li>datanucleus_data.properties</li>
     * <li>datanucleus_schema.properties</li>
     * <li>motech-mds.properties</li>
     * </ul>
     */
    public static final class Config {
        /**
         * Constant <code>MODULE_FILE</code> presents the file name with configuration for MDS
         * module.
         */
        public static final String MODULE_FILE = "motech-mds.properties";

        /**
         * Constant <code>MDS_DELETE_MODE</code> presents what should happen with objects when
         * there are deleted. They can be deleted permanently or moved to the trash.The following
         * values are valid for this property:
         * <ul>
         * <li>delete</li>
         * <li>trash</li>
         * </ul>
         */
        public static final String MDS_DELETE_MODE = "mds.deleteMode";

        /**
         * The boolean property that specifies if the trash should be empty after some time.
         *
         * @see #MDS_DELETE_MODE
         * @see #MDS_TIME_VALUE
         * @see #MDS_TIME_UNIT
         */
        public static final String MDS_EMPTY_TRASH = "mds.emptyTrash";

        /**
         * The integer property that specifies after what time (according with correct time unit)
         * trash should be cleaned.
         *
         * @see #MDS_DELETE_MODE
         * @see #MDS_EMPTY_TRASH
         * @see #MDS_TIME_UNIT
         */
        public static final String MDS_TIME_VALUE = "mds.emptyTrash.afterTimeValue";

        /**
         * The property that specifies what time unit should be used to specify time when trash
         * should be cleaned. The following values are valid for this property:
         * <ul>
         * <li>Hours</li>
         * <li>Days</li>
         * <li>Weeks</li>
         * <li>Months</li>
         * <li>Years</li>
         * </ul>
         *
         * @see #MDS_DELETE_MODE
         * @see #MDS_EMPTY_TRASH
         * @see #MDS_TIME_VALUE
         */
        public static final String MDS_TIME_UNIT = "mds.emptyTrash.afterTimeUnit";

        /**
         * The property that specifies default number of records in each data browser grid.
         */
        public static final String MDS_DEFAULT_GRID_SIZE = "mds.default.gridSize";

        /**
         * The property that specifies that bundle should be restarted by the MDS after timeout.
         */
        public static final String MDS_RESTART_BUNDLE_AFTER_TIMEOUT = "mds.restartBundle.afterTimeout";

        /**
         * Constant <code>EMPTY_TRASH_JOB</code> presents a name of job scheduled by scheduler
         * module.
         */
        public static final String EMPTY_TRASH_JOB = "org.motechproject.mds.emptyTrash-emptyTrash-repeat";

        /**
         * Constant <code>MYSQL_DRIVER_CLASSNAME</code> represents the name of MySql driver class. It is
         * used in various places, to verify what driver class has been chosen by the user.
         */
        public static final String MYSQL_DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";

        /**
         * Constant <code>POSTGRES_DRIVER_CLASSNAME</code> represents the name of Postgres driver class. It is used in
         * various places, to verify what driver vlass has been chosen by the user.
         */
        public static final String POSTGRES_DRIVER_CLASSNAME = "org.postgresql.Driver";

        private Config() {
        }
    }

    /**
     * The <code>Manifest</code> contains constant values related with attributes inside the
     * motech-platform-dataservices-entities bundle manifest.
     *
     * @see org.motechproject.mds.service.JarGeneratorService
     * @see org.motechproject.mds.service.impl.JarGeneratorServiceImpl
     */
    public static final class Manifest {

        /**
         * Constant <code>MANIFEST_VERSION</code> presents a version of jar manifest.
         */
        public static final String MANIFEST_VERSION = "1.0";

        /**
         * Constant <code>BUNDLE_MANIFESTVERSION</code> presents a version of bundle manifest.
         */
        public static final String BUNDLE_MANIFESTVERSION = "2";

        /**
         * Constant <code>SYMBOLIC_NAME_SUFFIX</code> presents suffix of the bundle symbolic name of
         * bundle that will be created by implementation of
         * {@link org.motechproject.mds.service.JarGeneratorService} interface.
         */
        public static final String SYMBOLIC_NAME_SUFFIX = "-entities";

        /**
         * Constant <code>BUNDLE_NAME_SUFFIX</code> presents suffix of the name of bundle that will
         * be created by implementation of
         * {@link org.motechproject.mds.service.JarGeneratorService} interface.
         */
        public static final String BUNDLE_NAME_SUFFIX = " Entitites";

        private Manifest() {
        }
    }

    /**
     * The <code>AnnotationFields</code> contains constant values related with attributes names
     * in mds annotations.
     *
     * @see org.motechproject.mds.annotations.Entity
     * @see org.motechproject.mds.annotations.Field
     * @see org.motechproject.mds.annotations.Ignore
     * @see org.motechproject.mds.annotations.Lookup
     * @see org.motechproject.mds.annotations.LookupField
     */
    public static final class AnnotationFields {

        /**
         * Constant <code>NAME</code> corresponding to the @Entity attribute named {@code name}
         */
        public static final String NAME = "name";

        /**
         * Constant <code>MODULE</code> corresponding to the @Entity attribute named {@code module}
         */
        public static final String MODULE = "module";

        /**
         * Constant <code>NAMESPACE</code> corresponding to the @Entity attribute named {@code namespace}
         */
        public static final String NAMESPACE = "namespace";

        /**
         * Constant <code>TABLE_NAME</code> corresponding to the @Entity attribute named {@code tableName}
         */
        public static final String TABLE_NAME = "tableName";

        /**
         * Constant <code>HISTORY</code> corresponding to the @Entity attribute named {@code recordHistory}
         */
        public static final String HISTORY = "recordHistory";

        /**
         * Constant <code>NON_EDITABLE</code> corresponding to the @Entity attribute named {@code nonEditable}
         */
        public static final String NON_EDITABLE = "nonEditable";

        /**
         * Constant <code>TABLE_NAME</code> corresponding to the @Entity attribute named {@code maxFetchDepth}
         */
        public static final String MAX_FETCH_DEPTH = "maxFetchDepth";

        /**
         * Constant <code>CRUD_EVENTS</code> corresponding to the @Entity attribute named {@code crudEvents}
         */
        public static final String CRUD_EVENTS = "crudEvents";

        /**
         * Constant <code>DISPLAY_NAME</code> corresponding to the primitive value
         * {@code displayName}
         */
        public static final String DISPLAY_NAME = "displayName";

        public static final String TYPE = "type";

        /**
         * Constant <code>VALUE</code> corresponding to the primitive value {@code value}
         */
        public static final String VALUE = "value";

        /**
         * Constant <code>REGEXP</code> corresponding to the primitive value {@code regexp}
         */
        public static final String REGEXP = "regexp";

        /**
         * Constant <code>MIN</code> corresponding to the primitive value {@code min}
         */
        public static final String MIN = "min";

        /**
         * Constant <code>MAX</code> corresponding to the primitive value {@code max}
         */
        public static final String MAX = "max";

        /**
         * Constant <code>INTEGER</code> corresponding to the primitive value {@code integer}
         */
        public static final String INTEGER = "integer";

        /**
         * Constant <code>FRACTION</code> corresponding to the primitive value {@code fraction}
         */
        public static final String FRACTION = "fraction";

        /**
         * Constant <code>PERSIST</code> corresponding to the attribute name {@code persist}
         */
        public static final String PERSIST = "persist";

        /**
         * Constant <code>UPDATE</code> corresponding to the attribute name {@code update}
         */
        public static final String UPDATE = "update";

        /**
         * Constant <code>DELETE</code> corresponding to the attribute name {@code delete}
         */
        public static final String DELETE = "delete";

        /**
         * Constant <code>EXPANDBYDEFAULT</code> corresponding to the attribute name {@code expandByDefault}
         */
        public static final String EXPANDBYDEFAULT = "expandByDefault";

        /**
         * Constant <code>SHOWCOUNT</code> corresponding to the attribute name {@code showCount}
         */
        public static final String SHOWCOUNT = "showCount";

        /**
         * Constant <code>ALLOWADDINGNEW</code> corresponding to the attribute name {@code allowAddingNew}
         */
        public static final String ALLOWADDINGNEW = "allowAddingNew";

        /**
         * Constant <code>ALLOWADDINGEXISTING</code> corresponding to the attribute name {@code allowAddingExisting}
         */
        public static final String ALLOWADDINGEXISTING = "allowAddingExisting";

        private AnnotationFields() {
        }
    }

    /**
     * The <code>Util</code> contains constant values to help avoid string literal repetition.
     *
     * @see <a href="http://pmd.sourceforge.net/rules/strings.html#AvoidDuplicateLiterals">pmd</a>
     */
    public static final class Util {

        /**
         * Default {@link java.text.DateFormat} to be used to parse and format {@link java.util.Date}.
         */
        public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

        /**
         * Constant <code>TRUE</code> corresponding to the primitive value {@code true}
         */
        public static final String TRUE = "true";

        /**
         * Constant <code>FALSE</code> corresponding to the primitive value {@code false}
         */
        public static final String FALSE = "false";

        /**
         * Constant <code>ENTITY</code> corresponding to the field name of the class that want to
         * create a bidirectional connection with instane of
         * {@link org.motechproject.mds.domain.Entity}
         */
        public static final String ENTITY = "entity";

        public static final String ID_FIELD_NAME = "id";
        public static final String ID_FIELD_DISPLAY_NAME = "ID";
        public static final String CREATOR_FIELD_NAME = "creator";
        public static final String CREATION_DATE_FIELD_NAME = "creationDate";
        public static final String OWNER_FIELD_NAME = "owner";
        public static final String MODIFIED_BY_FIELD_NAME = "modifiedBy";
        public static final String MODIFICATION_DATE_FIELD_NAME = "modificationDate";
        public static final String ID_DISPLAY_FIELD_NAME = "Id";
        public static final String INSTANCE_VERSION_FIELD_NAME = "instanceVersion";
        public static final String CREATOR_DISPLAY_FIELD_NAME = "Created By";
        public static final String CREATION_DATE_DISPLAY_FIELD_NAME = "Creation Date";
        public static final String OWNER_DISPLAY_FIELD_NAME = "Owner";
        public static final String MODIFIED_BY_DISPLAY_FIELD_NAME = "Modified By";
        public static final String MODIFICATION_DATE_DISPLAY_FIELD_NAME = "Modification Date";
        public static final String SCHEMA_VERSION_FIELD_NAME = "schemaVersion";
        public static final String CURRENT_VERSION = "CurrentVersion";
        public static final String DATANUCLEUS = "datanucleus";
        public static final String VALUE_GENERATOR = "object-value-generator";
        public static final String AUTO_GENERATED = "autoGenerated";
        public static final String AUTO_GENERATED_EDITABLE = "autoGeneratedEditable";
        public static final String SQL_QUERY = "javax.jdo.query.SQL";
        public static final String MDS_DATABASE_KEY = "mds.data.databaseName";
        public static final String SCHEMA_DATABASE_KEY = "mds.schema.databaseName";

        public static final String[] GENERATED_FIELD_NAMES = new String[]{
                CREATOR_FIELD_NAME, OWNER_FIELD_NAME, CREATION_DATE_FIELD_NAME,
                MODIFIED_BY_FIELD_NAME, MODIFICATION_DATE_FIELD_NAME, ID_FIELD_NAME
        };

        public static final Set<String> RECORD_FIELDS_TO_COPY = ImmutableSet.copyOf(Arrays.asList(
                Constants.Util.MODIFICATION_DATE_FIELD_NAME, Constants.Util.CREATION_DATE_FIELD_NAME,
                Constants.Util.MODIFIED_BY_FIELD_NAME, Constants.Util.CREATOR_FIELD_NAME,
                Constants.Util.OWNER_FIELD_NAME
        ));

        private Util() {
        }
    }

    /**
     * The names of the bundles.
     */
    public static final class BundleNames {
        public static final String SYMBOLIC_NAME_PREFIX = "org.motechproject.";

        public static final String MDS_BUNDLE_NAME = "motech-platform-dataservices";
        public static final String MDS_BUNDLE_SYMBOLIC_NAME = SYMBOLIC_NAME_PREFIX + MDS_BUNDLE_NAME;

        public static final String MDS_ENTITIES_NAME = "motech-platform-dataservices-entities";
        public static final String MDS_ENTITIES_SYMBOLIC_NAME = SYMBOLIC_NAME_PREFIX + MDS_ENTITIES_NAME;

        public static final String MDS_MIGRATION_NAME = "motech-platform-dataservices-migration";
        public static final String MDS_MIGRATION_SYMBOLIC_NAME = SYMBOLIC_NAME_PREFIX + MDS_MIGRATION_NAME;

        public static final String WEB_SECURITY_MODULE = "org.motechproject.motech-platform-web-security";
        public static final String SERVER_CONFIG_MODULE = "org.motechproject.motech-platform-server-config";
        public static final String SCHEDULER_MODULE = "org.motechproject.motech-platform-scheduler";
        public static final String TASKS_MODULE = "org.motechproject.motech-tasks";
        private BundleNames() {
        }
    }

    /**
     * The keys used in fields metadata
     */
    public static final class MetadataKeys {
        public static final String ENUM_CLASS_NAME = "enum.className";
        public static final String ENUM_COLLECTION_TYPE = "enum.collectionType";

        public static final String RELATED_CLASS = "related.class";
        public static final String RELATED_FIELD = "related.field";
        public static final String OWNING_SIDE = "related.owningSide";
        public static final String RELATIONSHIP_COLLECTION_TYPE = "related.collectionType";

        public static final String MAP_KEY_TYPE = "map.key.class";
        public static final String MAP_VALUE_TYPE = "map.value.class";

        public static final String DATABASE_COLUMN_NAME = "databaseColumnName";

        public static final String VERSION_FIELD = "version.field";

        private MetadataKeys() {
        }
    }

    /**
     * Operators that users can use in lookups.
     */
    public static final class Operators {

        // standard operators
        public static final String LT = "<";
        public static final String LT_EQ = "<=";
        public static final String GT = ">";
        public static final String GT_EQ = ">=";
        public static final String EQ = "==";
        public static final String NEQ = "!=";

        // string functions
        public static final String MATCHES = "matches()";
        public static final String MATCHES_CASE_INSENSITIVE = "matches((?i))";
        public static final String STARTS_WITH = "startsWith()";
        public static final String ENDS_WITH = "endsWith()";
        public static final String EQ_IGNORE_CASE = "equalsIgnoreCase()";

        private Operators() {
        }
    }

    /**
     * Keys for entity settings.
     */
    public static final class Settings {

        public static final String ALLOW_MULTIPLE_SELECTIONS = "mds.form.label.allowMultipleSelections";
        public static final String ALLOW_USER_SUPPLIED = "mds.form.label.allowUserSupplied";
        public static final String COMBOBOX_VALUES = "mds.form.label.values";
        public static final String STRING_MAX_LENGTH = "mds.form.label.maxTextLength";
        public static final String STRING_TEXT_AREA = "mds.form.label.textarea";
        public static final String TEXT_AREA_SQL_TYPE = "TEXT";

        public static final String CASCADE_PERSIST = "mds.form.label.cascadePersist";
        public static final String CASCADE_UPDATE = "mds.form.label.cascadeUpdate";
        public static final String CASCADE_DELETE = "mds.form.label.cascadeDelete";

        public static final String EXPANDBYDEFAULT = "mds.form.label.expandByDefault";
        public static final String SHOWCOUNT = "mds.form.label.showCount";
        public static final String ALLOWADDINGNEW = "mds.form.label.allowAddingNew";
        public static final String ALLOWADDINGEXISTING = "mds.form.label.allowAddingExisting";

        private Settings() {
        }
    }

    /**
     * The <code>MDSEvents</code> contains constant values related with MDS CRUD events.
     */
    public static final class MDSEvents {

        // parameters name
        public static final String ENTITY_NAME = "entity_name";
        public static final String MODULE_NAME = "module_name";
        public static final String NAMESPACE = "namespace";
        public static final String ENTITY_CLASS = "entity_class";
        public static final String OBJECT_ID = "object_id";

        private static final String CSV_IMPORT_PREFIX = "csv-import.";
        public static final String CSV_IMPORT_CREATED_IDS = CSV_IMPORT_PREFIX + "created_ids";
        public static final String CSV_IMPORT_UPDATED_IDS = CSV_IMPORT_PREFIX + "updated_ids";
        public static final String CSV_IMPORT_CREATED_COUNT = CSV_IMPORT_PREFIX + "created_count";
        public static final String CSV_IMPORT_UPDATED_COUNT = CSV_IMPORT_PREFIX + "updated_count";
        public static final String CSV_IMPORT_TOTAL_COUNT = CSV_IMPORT_PREFIX + "total_count";
        public static final String CSV_IMPORT_FAILURE_MSG = CSV_IMPORT_PREFIX + "failure_message";
        public static final String CSV_IMPORT_FAILURE_STACKTRACE = CSV_IMPORT_PREFIX + "failure_stacktrace";
        public static final String CSV_IMPORT_FILENAME = CSV_IMPORT_PREFIX + "filename";

        // subject
        public static final String BASE_SUBJECT = "mds.crud.";
        public static final String CSV_IMPORT_SUCCESS = "csv-import.success";
        public static final String CSV_IMPORT_FAILURE = "csv-import.failure";
    }

    public static final class DisplayNames {

        public static final String COMBOBOX = "mds.field.combobox";
        public static final String TEXT_AREA = "mds.field.textArea";
        public static final String BLOB = "mds.field.blob";
        public static final String MAP = "mds.field.map";
    }

    /**
     * Constants corresponding to the fetch depths when retrieving entities.
     */
    public static final class FetchDepth {

        /**
         * Signals that default MDS value should be used. No custom fetch depth will be passed to the persistence manager.
         */
        public static final int MDS_DEFAULT = 0;

        /**
         * Represents greedy fetching - the infinite fetch depth.
         */
        public static final int INFINITE = -1;
    }

    /**
     *  Constants corresponding to the entities migrations.
     */
    public static final class EntitiesMigration {

        public static final String ENTITY_MIGRATIONS_PREFIX = "M";

        public static final String MIGRATION_FILE_NAME_PATTERN = "^V[1-9]{1}[0-9]*__[a-zA-Z0-9\\-_ ]*\\.sql$";

        public static final String MIGRATION_DIRECTORY = "/migration";

        public static final String FILESYSTEM_PREFIX = "filesystem:";

        public static final String PRE_SCHEMA_CREATION_DIRECTORY = "preSchemaGeneration";
    }

    /**
     * Formats for table data exported by MDS.
     */
    public static final class ExportFormat {

        public static final String CSV = "csv";

        public static final String PDF = "pdf";

        public static boolean isValidFormat(String format) {
            return CSV.equalsIgnoreCase(format) || PDF.equalsIgnoreCase(format);
        }
    }

    /**
     * Constants related to history and trash classes.
     */
    public static final class HistoryTrash {

        public static final String HISTORY_SUFFIX = "__History";

        public static final String TRASH_SUFFIX = "__Trash";
    }

    private Constants() {
    }
}
