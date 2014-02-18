package org.motechproject.mds.util;

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
         * @see {@link #SCHEMA_ACCESS}
         */
        public static final String HAS_SCHEMA_ACCESS = "hasRole('" + SCHEMA_ACCESS + "')";

        /**
         * Spring security el expression to check if the given user has the 'Settings Access' role.
         *
         * @see {@link #SETTINGS_ACCESS}
         */
        public static final String HAS_SETTINGS_ACCESS = "hasRole('" + SETTINGS_ACCESS + "')";

        /**
         * Spring security el expression to check if the given user has the 'Data Access' role.
         *
         * @see {@link #DATA_ACCESS}
         */
        public static final String HAS_DATA_ACCESS = "hasRole('" + DATA_ACCESS + "')";

        /**
         * Spring security el expression to check if the given user has the 'Schema Access' or
         * 'Data Access' roles.
         *
         * @see {@link #SCHEMA_ACCESS}
         * @see {@link #DATA_ACCESS}
         */
        public static final String HAS_DATA_OR_SCHEMA_ACCESS = "hasAnyRole('" + SCHEMA_ACCESS + "', '" + DATA_ACCESS + "')";

        /**
         * Spring security el expression to check if the given user has any of the MDS roles.
         *
         * @see {@link #SCHEMA_ACCESS}
         * @see {@link #SETTINGS_ACCESS}
         * @see {@link #DATA_ACCESS}
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
         * @see {@link #BASE}
         */
        public static final String ENTITY = BASE + ".entity";

        /**
         * Constant <code>REPOSITORY</code> presents a package for repository classes.
         *
         * @see {@link #BASE}
         */
        public static final String REPOSITORY = BASE + ".repository";

        /**
         * Constant <code>SERVICE</code> presents a package for service interfaces.
         *
         * @see {@link #BASE}
         */
        public static final String SERVICE = BASE + ".service";

        /**
         * Constant <code>SERVICE_IMPL</code> presents a package for implementation of interfaces
         * defined in {@link #SERVICE} package.
         *
         * @see {@link #BASE}
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
         * @see {@link #ENTITY}
         */
        public static final String REPOSITORY = ENTITY + ".repository";

        /**
         * Constant <code>SERVICE</code> presents a package for generated service interfaces.
         *
         * @see {@link #ENTITY}
         */
        public static final String SERVICE = ENTITY + ".service";

        /**
         * Constant <code>SERVICE_IMPL</code> presents a package for generated implementation of interfaces
         * defined in {@link #SERVICE} package.
         *
         * @see {@link #SERVICE}
         */
        public static final String SERVICE_IMPL = SERVICE + ".impl";

        private PackagesGenerated() {
        }
    }

    /**
     * The <code>Config</code> contains constant values related with properties inside files:
     * <ul>
     * <li>datanucleus.properties</li>
     * <li>motech-mds.properties</li>
     * </ul>
     *
     * @see org.motechproject.server.config.SettingsFacade
     */
    public static final class Config {

        /**
         * Constant <code>DATANUCLEUS_FILE</code> presents the file name with configuration for
         * datanucleus.
         */
        public static final String DATANUCLEUS_FILE = "datanucleus.properties";

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
         * @see {@link #MDS_DELETE_MODE}
         * @see {@link #MDS_TIME_VALUE}
         * @see {@link #MDS_TIME_UNIT}
         */
        public static final String MDS_EMPTY_TRASH = "mds.emptyTrash";

        /**
         * The integer property that specifies after what time (according with correct time unit)
         * trash should be cleaned.
         *
         * @see {@link #MDS_DELETE_MODE}
         * @see {@link #MDS_EMPTY_TRASH}
         * @see {@link #MDS_TIME_UNIT}
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
         * @see {@link #MDS_DELETE_MODE}
         * @see {@link #MDS_EMPTY_TRASH}
         * @see {@link #MDS_TIME_VALUE}
         */
        public static final String MDS_TIME_UNIT = "mds.emptyTrash.afterTimeUnit";

        private Config() {
        }
    }

    /**
     * The <code>Manifest</code> contains constant values related with attributes inside the
     * motech-dataservices-entities bundle manifest.
     *
     * @see org.motechproject.mds.service.JarGeneratorService
     * @see org.motechproject.mds.service.impl.internal.JarGeneratorServiceImpl
     * @see org.motechproject.mds.web.controller.JarGeneratorController
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
         * Constant <code>NAME</code> corresponding to the attribute name {@code name}
         */
        public static final String NAME = "name";

        /**
         * Constant <code>MODULE</code> corresponding to the attribute name {@code module}
         */
        public static final String MODULE = "module";

        /**
         * Constant <code>NAMESPACE</code> corresponding to the attribute name {@code namespace}
         */
        public static final String NAMESPACE = "namespace";

        /**
         * Constant <code>DISPLAY_NAME</code> corresponding to the primitive value
         * {@code displayName}
         */
        public static final String DISPLAY_NAME = "displayName";

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

        private Util() {
        }
    }

    private Constants() {
    }
}
