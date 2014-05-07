package org.motechproject.mds.builder.impl;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.CtClass;
import org.apache.commons.io.IOUtils;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.builder.EntityInfrastructureBuilder;
import org.motechproject.mds.builder.EntityMetadataBuilder;
import org.motechproject.mds.builder.EnumBuilder;
import org.motechproject.mds.builder.MDSConstructor;
import org.motechproject.mds.config.SettingsWrapper;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.enhancer.MdsJDOEnhancer;
import org.motechproject.mds.ex.EntityCreationException;
import org.motechproject.mds.javassist.JavassistHelper;
import org.motechproject.mds.javassist.JavassistLoader;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MetadataHolder;
import org.motechproject.mds.util.ClassName;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Default implmenetation of {@link org.motechproject.mds.builder.MDSConstructor} interface.
 */
@Service
public class MDSConstructorImpl implements MDSConstructor {

    private static final Logger LOG = LoggerFactory.getLogger(MDSConstructorImpl.class);

    private SettingsWrapper settingsWrapper;
    private AllEntities allEntities;
    private EntityBuilder entityBuilder;
    private EntityInfrastructureBuilder infrastructureBuilder;
    private EntityMetadataBuilder metadataBuilder;
    private MetadataHolder metadataHolder;
    private BundleContext bundleContext;
    private EnumBuilder enumBuilder;
    private PersistenceManagerFactory persistenceManagerFactory;

    @Override
    public synchronized void constructEntities(boolean buildDDE) {
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

        for (Entity entity : entities) {
            ClassData classData = buildClass(entity);
            ClassData historyClassData = entityBuilder.buildHistory(entity);
            ClassData trashClassData = entityBuilder.buildTrash(entity);

            metadataBuilder.addEntityMetadata(jdoMetadata, entity);
            metadataBuilder.addHelperClassMetadata(jdoMetadata, historyClassData, entity);
            metadataBuilder.addHelperClassMetadata(jdoMetadata, trashClassData, entity);

            // next we create the java classes and add them to both
            // the temporary classloader and enhancer
            addClassData(loader, enhancer, classData);
            addClassData(loader, enhancer, historyClassData);
            addClassData(loader, enhancer, trashClassData);
            LOG.debug("Generated classes for {}", entity.getClassName());
        }

        // after the classes are defined, we register their metadata
        enhancer.registerMetadata(jdoMetadata);

        // then, we commence with enhancement
        enhancer.enhance();

        // we register the enhanced class bytes
        // and build the infrastructure classes
        for (Entity entity : entities) {
            // register
            String className = entity.getClassName();
            LOG.debug("Registering {}", className);

            registerClass(enhancer, entity);
            registerHistoryClass(enhancer, className);
            registerTrashClass(enhancer, className);

            LOG.debug("Building infrastructure for {}", className);
            buildInfrastructure(entity);
        }
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
                    String enumName = holder.getEnumFullName();
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
            updateFieldName(key, fieldNameChanges.get(key), tableName + "__HISTORY");
            updateFieldName(key, fieldNameChanges.get(key), tableName + "__TRASH");
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

    private void addClassData(JavassistLoader loader, MdsJDOEnhancer enhancer, ClassData data) {
        loader.loadClass(data);
        enhancer.addClass(data);
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
            if (!entity.isActualEntity() || (!buildDDE && entity.isDDE())) {
                it.remove();
            } else if (entity.isDDE()) {
                Class<?> defitinion = loadClass(entity.getModule(), entity.getClassName());

                if (null == defitinion) {
                    it.remove();
                }
            }
        }
    }

    private void updateFieldName(String oldName, String newName, String tableName) {

        JDOConnection con = persistenceManagerFactory.getPersistenceManager().getDataStoreConnection();
        Connection nativeCon = (Connection) con.getNativeConnection();
        Statement stmt = null;
        try {
            stmt = nativeCon.createStatement();

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
                LOG.info("Table " + oldName + "does not exist yet.", e);
            } else {
                LOG.error("Can not update column " + oldName, e);
            }
        } finally {
            con.close();
        }
    }

    private MdsJDOEnhancer createEnhancer(ClassLoader enhancerClassLoader) {
        Properties config = settingsWrapper.getDataNucleusProperties();
        return new MdsJDOEnhancer(config, enhancerClassLoader);
    }

    private Class<?> loadClass(String module, String className) {
        Bundle declaringBundle = WebBundleUtil.findBundleByName(bundleContext, module);
        Class<?> definition = null;

        if (declaringBundle == null) {
            LOG.warn("Declaring bundle unavailable for entity {]", className);
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
    public void setSettingsWrapper(SettingsWrapper settingsWrapper) {
        this.settingsWrapper = settingsWrapper;
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
