package org.motechproject.mds.builder.impl;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.CtClass;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.motechproject.commons.sql.service.SqlDBManager;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.builder.EntityInfrastructureBuilder;
import org.motechproject.mds.builder.EntityMetadataBuilder;
import org.motechproject.mds.builder.EnumBuilder;
import org.motechproject.mds.builder.MDSConstructor;
import org.motechproject.mds.config.MdsConfig;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.enhancer.MdsJDOEnhancer;
import org.motechproject.mds.exception.entity.EntityCreationException;
import org.motechproject.mds.helper.ClassTableName;
import org.motechproject.mds.helper.EntitySorter;
import org.motechproject.mds.helper.bundle.MdsBundleHelper;
import org.motechproject.mds.javassist.JavassistLoader;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.internal.MetadataHolder;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.JavassistUtil;
import org.motechproject.mds.util.MDSClassLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.datastore.JDOConnection;
import javax.jdo.metadata.JDOMetadata;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Default implementation of {@link org.motechproject.mds.builder.MDSConstructor} interface.
 */
@Service
public class MDSConstructorImpl implements MDSConstructor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MDSConstructorImpl.class);

    private static final String DATA_TRANSACTION_MANAGER = "dataTransactionManager";

    private MdsConfig mdsConfig;
    private EntityBuilder entityBuilder;
    private EntityInfrastructureBuilder infrastructureBuilder;
    private EntityMetadataBuilder metadataBuilder;
    private MetadataHolder metadataHolder;
    private BundleContext bundleContext;
    private EnumBuilder enumBuilder;
    private PersistenceManagerFactory persistenceManagerFactory;
    private SqlDBManager sqlDBManager;

    @Override
    public synchronized boolean constructEntities(SchemaHolder schemaHolder) {
        // To be able to register updated class, we need to reload class loader
        // and therefore add all the classes again
        MotechClassPool.clearEnhancedData();
        MDSClassLoader.reloadClassLoader();

        // we need an jdo enhancer and a temporary classLoader
        // to define classes in before enhancement
        MDSClassLoader tmpClassLoader = MDSClassLoader.getStandaloneInstance();
        MdsJDOEnhancer enhancer = createEnhancer(tmpClassLoader);
        JavassistLoader loader = new JavassistLoader(tmpClassLoader);

        // process only entities that are not drafts
        List<EntityDto> entities = schemaHolder.getAllEntities();
        filterEntities(entities);
        sortEntities(entities, schemaHolder);

        // create enum for appropriate combobox fields
        for (EntityDto entity : entities) {
            buildEnum(loader, enhancer, entity, schemaHolder);
        }

        // load entities interfaces
        for (EntityDto entity : entities) {
            buildInterfaces(loader, enhancer, entity);
        }

        // generate jdo metadata from scratch for our entities
        JDOMetadata jdoMetadata = metadataHolder.reloadMetadata();

        // First we build empty history and trash classes
        // (We don't have to generate it for main class,
        // since we just fetch fields from existing definition
        for (EntityDto entity : entities) {
            if (entity.isRecordHistory()) {
                entityBuilder.prepareHistoryClass(entity);
            }
            entityBuilder.prepareTrashClass(entity);
        }

        // Build classes
        Map<String, ClassData> classDataMap = buildClasses(entities, schemaHolder);
        List<Class> classes = new ArrayList<>();

        // We add the java classes to both
        // the temporary ClassLoader and enhancer
        for (EntityDto entity : entities) {
            String className = entity.getClassName();

            Class<?> definition = addClassData(loader, enhancer, classDataMap.get(className));
            if (entity.isRecordHistory()) {
                addClassData(loader, enhancer, classDataMap.get(ClassName.getHistoryClassName(className)));
            }
            addClassData(loader, enhancer, classDataMap.get(ClassName.getTrashClassName(className)));

            classes.add(definition);

            LOGGER.debug("Generated classes for {}", entity.getClassName());
        }

        for (Class<?> definition : classes) {
            loader.loadFieldsAndMethodsOfClass(definition);
        }

        // Prepare metadata
        buildMetadata(entities, jdoMetadata, classDataMap, classes, schemaHolder);

        // after the classes are defined, we register their metadata
        enhancer.registerMetadata(jdoMetadata);

        // then, we commence with enhancement
        enhancer.enhance();

        // we register the enhanced class bytes
        // and build the infrastructure classes
        registerEnhancedClassBytes(entities, enhancer, schemaHolder);

        metadataBuilder.fixEnhancerIssuesInMetadata(jdoMetadata, schemaHolder);

        return CollectionUtils.isNotEmpty(entities);
    }

    private void registerEnhancedClassBytes(List<EntityDto> entities, MdsJDOEnhancer enhancer, SchemaHolder schemaHolder) {
        for (EntityDto entity : entities) {
            // register
            String className = entity.getClassName();
            LOGGER.debug("Registering {}", className);

            registerClass(enhancer, entity);
            if (entity.isRecordHistory()) {
                registerHistoryClass(enhancer, className);
            }
            registerTrashClass(enhancer, className);

            LOGGER.debug("Building infrastructure for {}", className);
            buildInfrastructure(entity, schemaHolder);
        }
    }

    private void sortEntities(List<EntityDto> entities, SchemaHolder schemaHolder) {
        List<EntityDto> byInheritance = EntitySorter.sortByInheritance(entities);
        List<EntityDto> byHasARelation = EntitySorter.sortByHasARelation(byInheritance, schemaHolder);

        // for safe we clear entities list
        entities.clear();
        // for now the entities list will be sorted by inheritance and by 'has-a' relation
        entities.addAll(byHasARelation);
    }

    private Map<String, ClassData> buildClasses(List<EntityDto> entities, SchemaHolder schemaHolder) {
        Map<String, ClassData> classDataMap = new LinkedHashMap<>();

        //We build classes for all entities
        for (EntityDto entity : entities) {
            List<FieldDto> fields = schemaHolder.getFields(entity);

            ClassData classData = buildClass(entity, fields);

            ClassData historyClassData = null;

            if (entity.isRecordHistory()) {
                historyClassData = entityBuilder.buildHistory(entity, fields);
            }
            ClassData trashClassData = entityBuilder.buildTrash(entity, fields);

            String className = entity.getClassName();

            classDataMap.put(className, classData);
            if (historyClassData != null) {
                classDataMap.put(ClassName.getHistoryClassName(className), historyClassData);
            }
            classDataMap.put(ClassName.getTrashClassName(className), trashClassData);
        }

        return classDataMap;
    }

    private void buildMetadata(List<EntityDto> entities, JDOMetadata jdoMetadata, Map<String, ClassData> classDataMap,
                               List<Class> classes, SchemaHolder schemaHolder) {
        for (EntityDto entity : entities) {
            String className = entity.getClassName();
            Class definition = null;

            for (Class clazz : classes) {
                if (clazz.getName().equals(className)) {
                    definition = clazz;
                    break;
                }
            }

            metadataBuilder.addEntityMetadata(jdoMetadata, entity, definition, schemaHolder);

            if (entity.isRecordHistory()) {
                metadataBuilder.addHelperClassMetadata(jdoMetadata, classDataMap.get(ClassName.getHistoryClassName(className)),
                        entity, EntityType.HISTORY, definition, schemaHolder);
            }

            metadataBuilder.addHelperClassMetadata(jdoMetadata, classDataMap.get(ClassName.getTrashClassName(className)),
                    entity, EntityType.TRASH, definition, schemaHolder);
        }
    }

    private void buildEnum(JavassistLoader loader, MdsJDOEnhancer enhancer, EntityDto entity,
                           SchemaHolder schemaHolder) {
        for (FieldDto field : schemaHolder.getFields(entity.getClassName())) {
            TypeDto type = field.getType();

            if (!type.isCombobox()) {
                continue;
            }

            ComboboxHolder holder = new ComboboxHolder(entity, field);

            if (holder.isEnum() || holder.isEnumCollection()) {
                if (field.isReadOnly()) {
                    String enumName = holder.getEnumName();
                    Class<?> definition = loadClass(entity, enumName);

                    if (null != definition) {
                        MotechClassPool.registerEnum(enumName);

                        CtClass ctClass = MotechClassPool.getDefault().getOrNull(enumName);

                        if (null != ctClass) {
                            try {
                                ctClass.defrost();
                                byte[] bytecode = ctClass.toBytecode();
                                ClassData data = new ClassData(enumName, bytecode);

                                // register with the classloader so that we avoid issues with the persistence manager
                                MDSClassLoader.getInstance().safeDefineClass(data.getClassName(), data.getBytecode());

                                addClassData(loader, enhancer, data);
                            } catch (IOException | CannotCompileException e) {
                                LOGGER.error("Could not load enum: {}", enumName);
                            }
                        }
                    }
                } else {
                    buildEnum(loader, enhancer, holder);
                }
            }
        }
    }

    private void buildEnum(JavassistLoader loader, MdsJDOEnhancer enhancer, ComboboxHolder holder) {
        ClassData data = enumBuilder.build(holder);

        ByteArrayClassPath classPath = new ByteArrayClassPath(data.getClassName(), data.getBytecode());
        MotechClassPool.getDefault().appendClassPath(classPath);

        MotechClassPool.registerEnhancedClassData(data);

        // register with the classloader so that we avoid issues with the persistence manager
        MDSClassLoader.getInstance().safeDefineClass(data.getClassName(), data.getBytecode());

        addClassData(loader, enhancer, data);
    }

    @Override
    @Transactional(DATA_TRANSACTION_MANAGER)
    public void updateFields(Entity entity, Map<String, String> fieldNameChanges) {
        for (String key : fieldNameChanges.keySet()) {
            String tableName = ClassTableName.getTableName(entity, EntityType.STANDARD);
            updateFieldName(key, fieldNameChanges.get(key), tableName);
            if (entity.isRecordHistory()) {
                updateFieldName(key, fieldNameChanges.get(key), ClassTableName.getTableName(entity, EntityType.HISTORY));
            }
            updateFieldName(key, fieldNameChanges.get(key), ClassTableName.getTableName(entity, EntityType.TRASH));
        }
    }

    @Override
    @Transactional(DATA_TRANSACTION_MANAGER)
    public void updateRequired(Entity entity, Map<String, String> fieldNameRequired) {
        String tableName = ClassTableName.getTableName(entity, EntityType.STANDARD);
        String historyTableName = entity.isRecordHistory() ? ClassTableName.getTableName(entity, EntityType.HISTORY) : null;
        String trashTableName = ClassTableName.getTableName(entity, EntityType.TRASH);

        boolean isMySqlDriver = isMysql();

        for (String field : fieldNameRequired.keySet()) {
            boolean required = Boolean.valueOf(fieldNameRequired.get(field));
            updateRequired(field, required, tableName, isMySqlDriver);

            //update history and trash tables only if column is not required
            if (!required) {
                updateRequired(field, false, trashTableName, isMySqlDriver);

                if (StringUtils.isNotEmpty(historyTableName)) {
                    updateRequired(field, false, historyTableName, isMySqlDriver);
                }
            }
        }
    }

    @Override
    @Transactional(DATA_TRANSACTION_MANAGER)
    public void removeFields(Entity entity, Set<String> fieldsToRemove) {
        String tableName = ClassTableName.getTableName(entity, EntityType.STANDARD);
        String historyTableName = entity.isRecordHistory() ? ClassTableName.getTableName(entity, EntityType.HISTORY) : null;
        String trashTableName = ClassTableName.getTableName(entity, EntityType.TRASH);

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        boolean isMySql = isMysql();

        for (String field : fieldsToRemove) {
            removeField(pm, isMySql, tableName, field);
            if (StringUtils.isNotEmpty(historyTableName)) {
                removeField(pm, isMySql, historyTableName, field);
            }
            removeField(pm, isMySql, trashTableName, field);
        }
    }

    @Override
    @Transactional(DATA_TRANSACTION_MANAGER)
    public void removeUniqueIndexes(Entity entity, Collection<String> fields) {
        String tableName = ClassTableName.getTableName(entity, EntityType.STANDARD);

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        boolean isMySql = isMysql();

        for (String field : fields) {
            String constraintName = KeyNames.uniqueKeyName(entity.getName(), field);

            String sql;
            if (isMySql) {
                sql = "DROP INDEX " + constraintName + " ON " + tableName;
            } else {
                sql = "ALTER TABLE \"" + tableName + "\" DROP CONSTRAINT IF EXISTS \"" + constraintName + "\"";
            }

            Query query = pm.newQuery(Constants.Util.SQL_QUERY, sql);

            query.execute();
        }
    }

    private void registerHistoryClass(MdsJDOEnhancer enhancer, String className) {
        String historyClassName = ClassName.getHistoryClassName(className);

        byte[] enhancedBytes = enhancer.getEnhancedBytes(historyClassName);
        ClassData classData = new ClassData(historyClassName, enhancedBytes);

        // register with the classloader so that we avoid issues with the persistence manager
        MDSClassLoader.getInstance().safeDefineClass(classData.getClassName(), classData.getBytecode());

        MotechClassPool.registerHistoryClassData(classData);
    }

    private void registerTrashClass(MdsJDOEnhancer enhancer, String className) {
        String trashClassName = ClassName.getTrashClassName(className);

        byte[] enhancedBytes = enhancer.getEnhancedBytes(trashClassName);
        ClassData classData = new ClassData(trashClassName, enhancedBytes);

        // register with the classloader so that we avoid issues with the persistence manager
        MDSClassLoader.getInstance().safeDefineClass(classData.getClassName(), classData.getBytecode());

        MotechClassPool.registerTrashClassData(classData);
    }

    private void registerClass(MdsJDOEnhancer enhancer, EntityDto entity) {
        byte[] enhancedBytes = enhancer.getEnhancedBytes(entity.getClassName());
        ClassData classData = new ClassData(entity, enhancedBytes);

        // register with the classloader so that we avoid issues with the persistence manager
        MDSClassLoader.getInstance().safeDefineClass(classData.getClassName(), classData.getBytecode());

        MotechClassPool.registerEnhancedClassData(classData);
    }

    private Class<?> addClassData(JavassistLoader loader, MdsJDOEnhancer enhancer, ClassData data) {
        Class<?> definition = loader.loadClass(data);
        enhancer.addClass(data);
        return definition;
    }

    private ClassData buildClass(EntityDto entity, List<FieldDto> fields) {
        ClassData classData;

        if (entity.isDDE()) {
            // for DDE we load the class coming from the bundle
            Bundle declaringBundle = MdsBundleHelper.searchForBundle(bundleContext, entity);

            if (declaringBundle == null) {
                throw new EntityCreationException("Declaring bundle unavailable for entity " + entity.getClassName());
            }

            classData = entityBuilder.buildDDE(entity, fields, declaringBundle);
        } else {
            classData = entityBuilder.build(entity, fields);
        }

        return classData;
    }

    private void buildInterfaces(JavassistLoader loader, MdsJDOEnhancer enhancer, EntityDto entity) {
        List<ClassData> interfaces = new LinkedList<>();

        if (entity.isDDE()) {
            Bundle declaringBundle = MdsBundleHelper.searchForBundle(bundleContext, entity);
            try {
                Class<?> definition = declaringBundle.loadClass(entity.getClassName());

                for (Class interfaceClass : definition.getInterfaces()) {
                    String classpath = JavassistUtil.toClassPath(interfaceClass.getName());
                    URL classResource = declaringBundle.getResource(classpath);

                    if (classResource != null) {
                        try (InputStream in = classResource.openStream()) {
                            interfaces.add(new ClassData(interfaceClass.getName(), IOUtils.toByteArray(in), true));
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                LOGGER.error("Class {} not found in {} bundle", entity.getClassName(), declaringBundle.getSymbolicName());
            } catch (IOException ioExc) {
                LOGGER.error("Could not load interface for {} class", entity.getClassName());
            }
        }

        for (ClassData data : interfaces) {
            try {
                MDSClassLoader.getInstance().loadClass(data.getClassName());
            } catch (ClassNotFoundException e) {
                // interfaces should be defined in the MDS class loader only if it does not exist
                MDSClassLoader.getInstance().safeDefineClass(data.getClassName(), data.getBytecode());
                ByteArrayClassPath classPath = new ByteArrayClassPath(data.getClassName(), data.getBytecode());
                MotechClassPool.getDefault().appendClassPath(classPath);

                MotechClassPool.registerEnhancedClassData(data);
                addClassData(loader, enhancer, data);
            }

        }
    }

    private void buildInfrastructure(EntityDto entity, SchemaHolder schemaHolder) {
        String className = entity.getClassName();

        List<ClassData> infrastructure = infrastructureBuilder.buildInfrastructure(entity, schemaHolder);

        for (ClassData classData : infrastructure) {
            // if we have a DDE service registered, we register the enhanced bytecode
            // so that the weaving hook can weave the interface class and add lookups
            // coming from the UI
            if (classData.isInterfaceClass() && MotechClassPool.isServiceInterfaceRegistered(className)) {
                MotechClassPool.registerEnhancedClassData(classData);
            }
        }
    }

    private void filterEntities(List<EntityDto> entities) {
        Iterator<EntityDto> it = entities.iterator();
        while (it.hasNext()) {
            EntityDto entity = it.next();

            if (isSkippedDDE(entity)) {
                it.remove();
            } else if (entity.isDDE()) {
                Class<?> definition = loadClass(entity, entity.getClassName());

                if (null == definition) {
                    it.remove();
                }
            }
        }
    }

    private boolean isSkippedDDE(EntityDto entity) {
        return entity.isDDE() && !MotechClassPool.isDDEReady(entity.getClassName());
    }

    private void updateFieldName(String oldName, String newName, String tableName) {
        LOGGER.info("Renaming column in {}: {} to {}", tableName, oldName, newName);

        JDOConnection con = persistenceManagerFactory.getPersistenceManager().getDataStoreConnection();
        Connection nativeCon = (Connection) con.getNativeConnection();

        boolean isMySqlDriver = isMysql();

        try {
            Statement stmt = nativeCon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            StringBuilder fieldTypeQuery = new StringBuilder("SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '");
            fieldTypeQuery.append(tableName);
            fieldTypeQuery.append("' AND COLUMN_NAME = '");
            fieldTypeQuery.append(oldName);
            fieldTypeQuery.append("';");
            ResultSet resultSet = stmt.executeQuery(fieldTypeQuery.toString());
            resultSet.first();
            String fieldType = resultSet.getString("DATA_TYPE");
            con.close();

            con = persistenceManagerFactory.getPersistenceManager().getDataStoreConnection();
            nativeCon = (Connection) con.getNativeConnection();
            stmt = nativeCon.createStatement();

            StringBuilder updateQuery = new StringBuilder("ALTER TABLE ");
            updateQuery.append(enquoteIfPostgres(tableName, isMySqlDriver));
            updateQuery.append(isMySqlDriver ? " CHANGE " : " RENAME COLUMN ");
            updateQuery.append(enquoteIfPostgres(oldName, isMySqlDriver));
            updateQuery.append(isMySqlDriver ? " " : " TO ");
            updateQuery.append(enquoteIfPostgres(newName, isMySqlDriver));

            if (isMySqlDriver) {
                updateQuery.append(" ");
                updateQuery.append("varchar".equals(fieldType) ? "varchar(255)" : fieldType);
            }
            updateQuery.append(";");

            stmt.executeUpdate(updateQuery.toString());
        } catch (SQLException e) {
            if ("S1000".equals(e.getSQLState())) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(String.format("Column %s does not exist in %s", oldName, tableName), e);
                }
            } else {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error(String.format("Unable to rename column in %s: %s to %s", tableName, oldName, newName), e);
                }
            }
        } finally {
            con.close();
        }
    }

    private void removeField(PersistenceManager pm, boolean isMySql, String tableName, String field) {
        String sql = String.format("ALTER TABLE %s DROP COLUMN %s", enquoteIfPostgres(tableName, isMySql),
                enquoteIfPostgres(field, isMySql));

        Query query = pm.newQuery(Constants.Util.SQL_QUERY, sql);

        query.execute();
    }

    private void updateRequired(String field, boolean required, String tableName, boolean isMySqlDriver) {
        JDOConnection con = persistenceManagerFactory.getPersistenceManager().getDataStoreConnection();
        Connection nativeCon = (Connection) con.getNativeConnection();

        try {
            Statement stmt = nativeCon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            StringBuilder fieldTypeQuery = new StringBuilder("SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '");
            fieldTypeQuery.append(tableName);
            fieldTypeQuery.append("' AND COLUMN_NAME = '");
            fieldTypeQuery.append(field);
            fieldTypeQuery.append("';");
            ResultSet resultSet = stmt.executeQuery(fieldTypeQuery.toString());
            resultSet.first();
            String fieldType = resultSet.getString("DATA_TYPE");
            con.close();

            con = persistenceManagerFactory.getPersistenceManager().getDataStoreConnection();
            nativeCon = (Connection) con.getNativeConnection();
            stmt = nativeCon.createStatement();

            StringBuilder updateQuery = new StringBuilder("ALTER TABLE ");
            updateQuery.append(enquoteIfPostgres(tableName, isMySqlDriver));
            updateQuery.append(isMySqlDriver ? " MODIFY " : " ALTER COLUMN ");
            updateQuery.append(enquoteIfPostgres(field, isMySqlDriver));

            if (isMySqlDriver) {
                updateQuery.append(" ");
                updateQuery.append("varchar".equals(fieldType) ? "varchar(255)" : fieldType);

                updateQuery.append(required ? " NOT NULL" : " DEFAULT NULL");
            } else {
                updateQuery.append(required ? " SET NOT NULL" : "DROP NOT NULL");
            }

            updateQuery.append(";");

            stmt.executeUpdate(updateQuery.toString());
        } catch (SQLException e) {
            LOGGER.error(String.format("Error while updating required constraints for %s", tableName), e);
        } finally {
            con.close();
        }
    }

    private String enquoteIfPostgres(String name, boolean isMySqlDriver) {
        return isMySqlDriver ? name : "\"".concat(name).concat("\"");
    }

    private boolean isMysql() {
        return sqlDBManager.getChosenSQLDriver().equals(Constants.Config.MYSQL_DRIVER_CLASSNAME);
    }

    private MdsJDOEnhancer createEnhancer(ClassLoader enhancerClassLoader) {
        Properties config = mdsConfig.getDataNucleusProperties();
        return new MdsJDOEnhancer(config, enhancerClassLoader);
    }

    private Class<?> loadClass(EntityDto entity, String className) {
        Bundle declaringBundle = MdsBundleHelper.searchForBundle(bundleContext, entity);
        Class<?> definition = null;

        if (declaringBundle == null) {
            LOGGER.warn("Declaring bundle unavailable for entity {}", className);
        } else {
            try {
                definition = declaringBundle.loadClass(className);
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Class declaration for {} not present in bundle {}",
                        className, declaringBundle.getSymbolicName());
            }
        }

        return definition;
    }

    @Autowired
    public void setSqlDBManager(SqlDBManager sqlDBManager) {
        this.sqlDBManager = sqlDBManager;
    }

    @Autowired
    public void setEntityBuilder(EntityBuilder entityBuilder) {
        this.entityBuilder = entityBuilder;
    }

    @Autowired
    public void setInfrastructureBuilder(EntityInfrastructureBuilder infrastructureBuilder) {
        this.infrastructureBuilder = infrastructureBuilder;
    }

    @Autowired
    public void setMetadataBuilder(EntityMetadataBuilder metadataBuilder) {
        this.metadataBuilder = metadataBuilder;
    }

    @Autowired
    public void setMdsConfig(MdsConfig mdsConfig) {
        this.mdsConfig = mdsConfig;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Autowired
    public void setMetadataHolder(MetadataHolder metadataHolder) {
        this.metadataHolder = metadataHolder;
    }

    @Autowired
    public void setEnumBuilder(EnumBuilder enumBuilder) {
        this.enumBuilder = enumBuilder;
    }

    @Autowired
    @Qualifier("dataPersistenceManagerFactory")
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }
}
