package org.motechproject.mds.builder.impl;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.CtClass;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.IOUtils;
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
import org.motechproject.mds.domain.RelationshipHolder;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.enhancer.MdsJDOEnhancer;
import org.motechproject.mds.ex.EntityCreationException;
import org.motechproject.mds.javassist.JavassistHelper;
import org.motechproject.mds.javassist.JavassistLoader;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MetadataHolder;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.osgi.web.util.WebBundleUtil;
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

    private static final Logger LOG = LoggerFactory.getLogger(MDSConstructorImpl.class);

    private MdsConfig mdsConfig;
    private AllEntities allEntities;
    private EntityBuilder entityBuilder;
    private EntityInfrastructureBuilder infrastructureBuilder;
    private EntityMetadataBuilder metadataBuilder;
    private MetadataHolder metadataHolder;
    private BundleContext bundleContext;
    private EnumBuilder enumBuilder;
    private PersistenceManagerFactory persistenceManagerFactory;

    @Override
    public synchronized boolean constructEntities(boolean buildDDE) {
        // To be able to register updated class, we need to reload class loader
        // and therefore add all the classes again
        MotechClassPool.clearEnhancedData();
        MDSClassLoader.reloadClassLoader();

        if (buildDDE) {
            LOG.info("Building all entities");
        } else {
            LOG.info("Building all EUDE entities");
        }
        // we need an jdo enhancer and a temporary classLoader
        // to define classes in before enhancement
        MDSClassLoader tmpClassLoader = MDSClassLoader.getStandaloneInstance();
        MdsJDOEnhancer enhancer = createEnhancer(tmpClassLoader);
        JavassistLoader loader = new JavassistLoader(tmpClassLoader);

        // process only entities that are not drafts
        List<Entity> entities = allEntities.retrieveAll();
        filterEntities(entities, buildDDE);
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

        // Build classes and prepare metadata
        Map<String, ClassData> classDataMap = buildClassesAndMetadata(entities, jdoMetadata);
        List<Class> classes = new ArrayList<>();

        // Finally we add the java classes to both
        // the temporary ClassLoader and enhancer
        for (Entity entity : entities) {
            String className = entity.getClassName();

            Class<?> definition = addClassData(loader, enhancer, classDataMap.get(className));
            if (entity.isRecordHistory()) {
                addClassData(loader, enhancer, classDataMap.get(ClassName.getHistoryClassName(className)));
            }
            addClassData(loader, enhancer, classDataMap.get(ClassName.getTrashClassName(className)));

            classes.add(definition);

            LOG.debug("Generated classes for {}", entity.getClassName());
        }

        for (Class<?> definition : classes) {
            loader.loadFieldsAndMethodsOfClass(definition);
        }

        // after the classes are defined, we register their metadata
        enhancer.registerMetadata(jdoMetadata);

        // then, we commence with enhancement
        enhancer.enhance();

        // we register the enhanced class bytes
        // and build the infrastructure classes
        registerEnhancedClassBytes(entities, enhancer);

        metadataBuilder.fixEnhancerIssuesInMetadata(jdoMetadata);

        return CollectionUtils.isNotEmpty(entities);
    }

    private void registerEnhancedClassBytes(List<Entity> entities, MdsJDOEnhancer enhancer) {
        for (Entity entity : entities) {
            // register
            String className = entity.getClassName();
            LOG.debug("Registering {}", className);

            registerClass(enhancer, entity);
            if (entity.isRecordHistory()) {
                registerHistoryClass(enhancer, className);
            }
            registerTrashClass(enhancer, className);

            LOG.debug("Building infrastructure for {}", className);
            buildInfrastructure(entity);
        }
    }

    private void sortEntities(List<Entity> entities) {
        List<Entity> byInheritance = sortByInheritance(entities);
        List<Entity> byHasARelation = sortByHasARelation(byInheritance);

        // for safe we clear entities list
        entities.clear();
        // for now the entities list will be sorted by inheritance and by 'has-a' relation
        entities.addAll(byHasARelation);
    }

    private List<Entity> sortByHasARelation(List<Entity> list) {
        List<Entity> sorted = new ArrayList<>(list);

        // we need to check if classes have 'has-a' relation
        // these classes should be later in list
        // we do that after all entities will be added to sorted list
        for (int i = 0; i < sorted.size(); ++i) {
            Entity entity = sorted.get(i);
            List<Field> fields = (List<Field>) CollectionUtils.select(entity.getFields(), new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return object instanceof Field && ((Field) object).getType().isRelationship();
                }
            });

            if (CollectionUtils.isNotEmpty(fields)) {
                int max = i;

                for (Field field : fields) {
                    final RelationshipHolder holder = new RelationshipHolder(field);
                    Entity relation = (Entity) CollectionUtils.find(sorted, new Predicate() {
                        @Override
                        public boolean evaluate(Object object) {
                            return object instanceof Entity
                                    && ((Entity) object).getClassName().equalsIgnoreCase(holder.getRelatedClass());
                        }
                    });

                    // In case the relation is bidirectional, we shouldn't move the class,
                    // in order to avoid infinite loop
                    boolean biDirectional = field.getMetadata(Constants.MetadataKeys.RELATED_FIELD) != null;
                    max = Math.max(max, biDirectional ? -1 : sorted.indexOf(relation));
                }

                if (max != i) {
                    sorted.remove(i);
                    --i;

                    if (max < sorted.size()) {
                        sorted.add(max, entity);
                    } else {
                        sorted.add(entity);
                    }

                }
            }
        }

        return sorted;
    }

    private List<Entity> sortByInheritance(List<Entity> list) {
        List<Entity> sorted = new ArrayList<>(list.size());

        // firstly we add entities with base class equal to Object class
        for (Iterator<Entity> iterator = list.iterator(); iterator.hasNext(); ) {
            Entity entity = iterator.next();

            if (entity.isBaseEntity()) {
                sorted.add(entity);
                iterator.remove();
            }
        }

        // then we add entities which base classes are in sorted list
        // we do that after all entities will be added to sorted list
        while (!list.isEmpty()) {
            for (Iterator<Entity> iterator = list.iterator(); iterator.hasNext(); ) {
                final Entity entity = iterator.next();
                Entity superClass = (Entity) CollectionUtils.find(sorted, new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        return object instanceof Entity
                                && ((Entity) object).getClassName().equals(entity.getSuperClass());
                    }
                });

                if (null != superClass) {
                    sorted.add(entity);
                    iterator.remove();
                }
            }
        }

        return sorted;
    }

    private Map<String, ClassData> buildClassesAndMetadata(List<Entity> entities, JDOMetadata jdoMetadata) {

        Map<String, ClassData> classDataMap = new LinkedHashMap<>();

        //We build classes and metadata for all entities
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

            metadataBuilder.addEntityMetadata(jdoMetadata, entity);
            if (historyClassData != null) {
                metadataBuilder.addHelperClassMetadata(jdoMetadata, historyClassData, entity, EntityType.HISTORY);
            }
            metadataBuilder.addHelperClassMetadata(jdoMetadata, trashClassData, entity, EntityType.TRASH);
        }

        return classDataMap;
    }

    private void buildEnum(JavassistLoader loader, MdsJDOEnhancer enhancer, Entity entity) {
        for (Field field : entity.getFields()) {
            Type type = field.getType();

            if (!type.isCombobox()) {
                continue;
            }

            ComboboxHolder holder = new ComboboxHolder(entity, field);

            if (holder.isEnum() || holder.isEnumList()) {
                if (field.isReadOnly()) {
                    String enumName = holder.getEnumName();
                    Class<?> definition = loadClass(entity.getModule(), enumName);

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
                                LOG.error("Could not load enum: {}", enumName);
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
            String tableName = EntityMetadataBuilderImpl.getTableName(entity.getClassName(), entity.getModule(), entity.getNamespace());
            updateFieldName(key, fieldNameChanges.get(key), tableName);
            if (entity.isRecordHistory()) {
                updateFieldName(key, fieldNameChanges.get(key), EntityMetadataBuilderImpl.getTableName(entity, EntityType.HISTORY));
            }
            updateFieldName(key, fieldNameChanges.get(key), EntityMetadataBuilderImpl.getTableName(entity, EntityType.TRASH));
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
            Bundle declaringBundle = WebBundleUtil.findBundleByName(bundleContext, entity.getModule());

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
            Bundle declaringBundle = WebBundleUtil.findBundleByName(bundleContext, entity.getModule());
            try {
                Class<?> definition = declaringBundle.loadClass(entity.getClassName());

                for (Class interfaceClass : definition.getInterfaces()) {
                    String classpath = JavassistHelper.toClassPath(interfaceClass.getName());
                    URL classResource = declaringBundle.getResource(classpath);

                    if (classResource != null) {
                        try (InputStream in = classResource.openStream()) {
                            interfaces.add(new ClassData(interfaceClass.getName(), IOUtils.toByteArray(in), true));
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                LOG.error("Class {} not found in {} bundle", entity.getClassName(), declaringBundle.getSymbolicName());
            } catch (IOException ioExc) {
                LOG.error("Could not load interface for {} class", entity.getClassName());
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

    private void filterEntities(List<Entity> entities, boolean buildDDE) {
        Iterator<Entity> it = entities.iterator();
        while (it.hasNext()) {
            Entity entity = it.next();

            // DDEs are generated when their declaring bundles context is loaded
            if (!entity.isActualEntity() || (!buildDDE && entity.isDDE()) || isSkipedDDE(entity)) {
                it.remove();
            } else if (entity.isDDE()) {
                Class<?> defitinion = loadClass(entity.getModule(), entity.getClassName());

                if (null == defitinion) {
                    it.remove();
                }
            }
        }
    }

    private boolean isSkipedDDE(Entity entity) {
        return entity.isDDE() && !MotechClassPool.isDDEReady(entity.getClassName());
    }

    private void updateFieldName(String oldName, String newName, String tableName) {
        LOG.info("Renaming column in {}: {} to {}", new String[]{tableName, oldName, newName});

        JDOConnection con = persistenceManagerFactory.getPersistenceManager().getDataStoreConnection();
        Connection nativeCon = (Connection) con.getNativeConnection();

        try {
            Statement stmt = nativeCon.createStatement();

            StringBuilder fieldTypeQuery = new StringBuilder("SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '");
            fieldTypeQuery.append(tableName);
            fieldTypeQuery.append("' AND COLUMN_NAME = '");
            fieldTypeQuery.append(oldName);
            fieldTypeQuery.append("';");
            ResultSet resultSet = stmt.executeQuery(fieldTypeQuery.toString());
            resultSet.absolute(1);
            String fieldType = resultSet.getString("DATA_TYPE");
            con.close();

            con = persistenceManagerFactory.getPersistenceManager().getDataStoreConnection();
            nativeCon = (Connection) con.getNativeConnection();
            stmt = nativeCon.createStatement();

            StringBuilder updateQuery = new StringBuilder("ALTER TABLE ");
            updateQuery.append(tableName);
            updateQuery.append(" CHANGE ");
            updateQuery.append(oldName);
            updateQuery.append(" ");
            updateQuery.append(newName);
            updateQuery.append(" ");
            updateQuery.append("varchar".equals(fieldType) ? "varchar(255)" : fieldType);
            updateQuery.append(";");

            stmt.executeUpdate(updateQuery.toString());

        } catch (SQLException e) {
            if ("S1000".equals(e.getSQLState())) {
                if (LOG.isInfoEnabled()) {
                    LOG.info(String.format("Column %s does not exist in %s", oldName, tableName), e);
                }
            } else {
                if (LOG.isErrorEnabled()) {
                    LOG.error(String.format("Unable to rename column in %s: %s to %s", tableName, oldName, newName), e);
                }
            }
        } finally {
            con.close();
        }
    }

    private MdsJDOEnhancer createEnhancer(ClassLoader enhancerClassLoader) {
        Properties config = mdsConfig.getDataNucleusProperties();
        return new MdsJDOEnhancer(config, enhancerClassLoader);
    }

    private Class<?> loadClass(String module, String className) {
        Bundle declaringBundle = WebBundleUtil.findBundleByName(bundleContext, module);
        Class<?> definition = null;

        if (declaringBundle == null) {
            LOG.warn("Declaring bundle unavailable for entity {}", className);
        } else {
            try {
                definition = declaringBundle.loadClass(className);
            } catch (ClassNotFoundException e) {
                LOG.warn("Class declaration for {} not present in bundle {}",
                        className, declaringBundle.getSymbolicName());
            }
        }

        return definition;
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
