package org.motechproject.mds.constants;

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
        public static final String SCHEMA_ACCESS = "seussSchemaAccess";

        /**
         * Users with ‘Settings Access’ have the ability to view the Settings tab. From that tab
         * then can modify data retention policies as well as import and export schema and data.
         */
        public static final String SETTINGS_ACCESS = "seussSettingsAccess";

        /**
         * Users with ‘Data Access’ have the ability to view the Data Browser tab. From that tab
         * then can search for objects within the system, view and modify the data stored in the
         * system.
         */
        public static final String DATA_ACCESS = "seussDataAccess";

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
         * Spring security el expression to check if the given user has any of the seuss roles.
         *
         * @see {@link #SCHEMA_ACCESS}
         * @see {@link #SETTINGS_ACCESS}
         * @see {@link #DATA_ACCESS}
         */
        public static final String HAS_ANY_SEUSS_ROLE = "hasAnyRole('" + SCHEMA_ACCESS + "', '" + DATA_ACCESS + "', '" + SETTINGS_ACCESS + "')";

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
     * Utility constants to help avoid string literal repetition
     */
    public static final class Util {

        public static final String TRUE = "true";

        public static final String FALSE = "false";
    }

    private Constants() {
    }
}
