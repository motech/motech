package org.motechproject.mds.builder.impl;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.CtClass;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.motechproject.commons.sql.service.SqlDBManager;
import org.motechproject.mds.annotations.internal.AnnotationProcessingContext;
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
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.enhancer.MdsJDOEnhancer;
import org.motechproject.mds.ex.entity.EntityCreationException;
import org.motechproject.mds.helper.ClassTableName;
import org.motechproject.mds.helper.EntitySorter;
import org.motechproject.mds.helper.MdsBundleHelper;
import org.motechproject.mds.javassist.JavassistLoader;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MetadataHolder;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.JavassistUtil;
import org.motechproject.mds.util.MDSClassLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.PersistenceManagerFactory;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Default implementation of {@link org.motechproject.mds.builder.MDSConstructor} interface.
 */
@Service
public class MDSConstructorImpl implements MDSConstructor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MDSConstructorImpl.class);

    private AllEntities allEntities;
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
    public synchronized boolean constructEntities(AnnotationProcessingContext context) {
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
        List<Entity> entities = new ArrayList<>(context.getAllEntities());
        filterEntities(entities);
        sortEntities(entities);

        // create enum for appropriate combobox fields
        for (Entity entity : entities) {
            buildEnum(loader, enhancer, entity);
        }

        // load entities interfaces
        for (Entity entity : entities) {
            buildInterfaces(loader, enhancer, entity);
        }

        // generate jdo metadata from scratch for our entities
        JDOMetadata jdoMetadata = metadataHolder.reloadMetadata();

        // First we build empty history and trash classes
        // (We don't have to generate it for main class,
        // since we just fetch fields from existing definition
        for (Entity entity : entities) {
            if (entity.isRecordHistory()) {
                entityBuilder.prepareHistoryClass(entity);
            }
            entityBuilder.prepareTrashClass(entity);
        }

        // Build classes
        Map<String, ClassData> classDataMap = buildClasses(entities);
        List<Class> classes = new ArrayList<>();

        // We add the java classes to both
        // the temporary ClassLoader and enhancer
        for (Entity entity : entities) {
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
        LOGGER.debug("Preparing metadata");
        buildMetadata(entities, jdoMetadata, classDataMap, classes, context);

        // after the classes are defined, we register their metadata
        LOGGER.debug("Registering metadata");
        enhancer.registerMetadata(jdoMetadata);

        // then, we commence with enhancement
        LOGGER.debug("Enhancing entity classes");
        enhancer.enhance();

        // we register the enhanced class bytes
        // and build the infrastructure classes
        LOGGER.debug("Registering enhanced class bytecode");
        registerEnhancedClassBytes(entities, enhancer);

        LOGGER.debug("Fixing JDO metadata after enhancement");
        metadataBuilder.fixEnhancerIssuesInMetadata(jdoMetadata, context);

        return CollectionUtils.isNotEmpty(entities);
    }

    private void registerEnhancedClassBytes(List<Entity> entities, MdsJDOEnhancer enhancer) {
        for (Entity entity : entities) {
            // register
            String className = entity.getClassName();
            LOGGER.debug("Registering {}", className);

            registerClass(enhancer, entity);
            if (entity.isRecordHistory()) {
                registerHistoryClass(enhancer, className);
            }
            registerTrashClass(enhancer, className);

            LOGGER.debug("Building infrastructure for {}", className);
            buildInfrastructure(entity);
        }
    }

    private void sortEntities(List<Entity> entities) {
        List<Entity> byInheritance = EntitySorter.sortByInheritance(entities);
        List<Entity> byHasARelation = EntitySorter.sortByHasARelation(byInheritance);

        // for safe we clear entities list
        entities.clear();
        // for now the entities list will be sorted by inheritance and by 'has-a' relation
        entities.addAll(byHasARelation);
    }

    private Map<String, ClassData> buildClasses(List<Entity> entities) {
        Map<String, ClassData> classDataMap = new LinkedHashMap<>();

        //We build classes for all entities
        for (Entity entity : entities) {
            ClassData classData = buildClass(entity);
            ClassData historyClassData = null;
            if (entity.isRecordHistory()) {
                historyClassData = entityBuilder.buildHistory(entity);
            }
            ClassData trashClassData = entityBuilder.buildTrash(entity);

            String className = entity.getClassName();

            classDataMap.put(className, classData);
            if (historyClassData != null) {
                classDataMap.put(ClassName.getHistoryClassName(className), historyClassData);
            }
            classDataMap.put(ClassName.getTrashClassName(className), trashClassData);
        }

        return classDataMap;
    }

    private void buildMetadata(List<Entity> entities, JDOMetadata jdoMetadata, Map<String, ClassData> classDataMap,
                               List<Class> classes, AnnotationProcessingContext context) {
        for (Entity entity : entities) {
            String className = entity.getClassName();
            Class definition = null;

            for (Class clazz : classes) {
                if (clazz.getName().equals(className)) {
                    definition = clazz;
                    break;
                }
            }

            metadataBuilder.addEntityMetadata(jdoMetadata, entity, definition, context);
            if (entity.isRecordHistory()) {
                metadataBuilder.addHelperClassMetadata(jdoMetadata, classDataMap.get(ClassName.getHistoryClassName(className)),
                        entity, EntityType.HISTORY, definition, context);
            }
            metadataBuilder.addHelperClassMetadata(jdoMetadata, classDataMap.get(ClassName.getTrashClassName(className)),
                    entity, EntityType.TRASH, definition, context);
        }
    }

    private void buildEnum(JavassistLoader loader, MdsJDOEnhancer enhancer, Entity entity) {
        for (Field field : entity.getFields()) {
            Type type = field.getType();

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
    @Transactional
    public void updateFields(Long entityId, Map<String, String> fieldNameChanges) {
        Entity entity = allEntities.retrieveById(entityId);

        for (String key : fieldNameChanges.keySet()) {
            String tableName = ClassTableName.getTableName(entity.getClassName(), entity.getModule(), entity.getNamespace(), entity.getTableName(), null);
            updateFieldName(key, fieldNameChanges.get(key), tableName);
            if (entity.isRecordHistory()) {
                updateFieldName(key, fieldNameChanges.get(key), ClassTableName.getTableName(entity, EntityType.HISTORY));
            }
            updateFieldName(key, fieldNameChanges.get(key), ClassTableName.getTableName(entity, EntityType.TRASH));
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

    private void registerClass(MdsJDOEnhancer enhancer, Entity entity) {
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

    private ClassData buildClass(Entity entity) {
        ClassData classData;

        if (entity.isDDE()) {
            // for DDE we load the class coming from the bundle
            Bundle declaringBundle = MdsBundleHelper.searchForBundle(bundleContext, entity);

            if (declaringBundle == null) {
                throw new EntityCreationException("Declaring bundle unavailable for entity " + entity.getClassName());
            }

            classData = entityBuilder.buildDDE(entity, declaringBundle);
        } else {
            classData = entityBuilder.build(entity);
        }

        return classData;
    }

    private void buildInterfaces(JavassistLoader loader, MdsJDOEnhancer enhancer, Entity entity) {
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

    private void buildInfrastructure(Entity entity) {
        String className = entity.getClassName();

        List<ClassData> infrastructure = infrastructureBuilder.buildInfrastructure(entity);

        for (ClassData classData : infrastructure) {
            // if we have a DDE service registered, we register the enhanced bytecode
            // so that the weaving hook can weave the interface class and add lookups
            // coming from the UI
            if (classData.isInterfaceClass() && MotechClassPool.isServiceInterfaceRegistered(className)) {
                MotechClassPool.registerEnhancedClassData(classData);
            }
        }
    }

    private void filterEntities(List<Entity> entities) {
        Iterator<Entity> it = entities.iterator();
        while (it.hasNext()) {
            Entity entity = it.next();

            if (!entity.isActualEntity() || isSkippedDDE(entity)) {
                it.remove();
            } else if (entity.isDDE()) {
                Class<?> definition = loadClass(entity, entity.getClassName());

                if (null == definition) {
                    it.remove();
                }
            }
        }
    }

    private boolean isSkippedDDE(Entity entity) {
        return entity.isDDE() && !MotechClassPool.isDDEReady(entity.getClassName());
    }

    private void updateFieldName(String oldName, String newName, String tableName) {
        LOGGER.info("Renaming column in {}: {} to {}", tableName, oldName, newName);

        JDOConnection con = persistenceManagerFactory.getPersistenceManager().getDataStoreConnection();
        Connection nativeCon = (Connection) con.getNativeConnection();

        boolean isMySqlDriver = sqlDBManager.getChosenSQLDriver().equals(Constants.Config.MYSQL_DRIVER_CLASSNAME);

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
            updateQuery.append(getDatabaseValidName(tableName, isMySqlDriver));
            updateQuery.append(isMySqlDriver ? " CHANGE " : " RENAME COLUMN ");
            updateQuery.append(getDatabaseValidName(oldName, isMySqlDriver));
            updateQuery.append(isMySqlDriver ? " " : " TO ");
            updateQuery.append(getDatabaseValidName(newName, isMySqlDriver));

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

    private String getDatabaseValidName(String name, boolean isMySqlDriver) {
        return isMySqlDriver ? name : "\"".concat(name).concat("\"");
    }

    private MdsJDOEnhancer createEnhancer(ClassLoader enhancerClassLoader) {
        Properties config = mdsConfig.getDataNucleusProperties();
        return new MdsJDOEnhancer(config, enhancerClassLoader);
    }

    private Class<?> loadClass(Entity entity, String className) {
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
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
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
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }
}
