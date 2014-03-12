package org.motechproject.mds.builder.impl;

import org.motechproject.mds.builder.ClassData;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.builder.EntityInfrastructureBuilder;
import org.motechproject.mds.builder.EntityMetadataBuilder;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.builder.MDSConstructor;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.enhancer.MdsJDOEnhancer;
import org.motechproject.mds.ex.EntityCreationException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MetadataHolder;
import org.motechproject.mds.util.Constants;
import org.motechproject.osgi.web.util.WebBundleUtil;
import org.motechproject.server.config.SettingsFacade;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.annotations.Transactional;
import javax.jdo.metadata.JDOMetadata;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Default implmenetation of {@link org.motechproject.mds.builder.MDSConstructor} interface.
 */
@Service
public class MDSConstructorImpl implements MDSConstructor {

    private static final Logger LOG = LoggerFactory.getLogger(MDSConstructorImpl.class);

    private SettingsFacade settingsFacade;
    private AllEntities allEntities;
    private EntityBuilder entityBuilder;
    private EntityInfrastructureBuilder infrastructureBuilder;
    private EntityMetadataBuilder metadataBuilder;
    private MetadataHolder metadataHolder;
    private PersistenceManagerFactory persistenceManagerFactory;
    private BundleContext bundleContext;

    private final Object generationLock = new Object();

    @Override
    @Transactional
    public void constructEntity(Entity entity) {
        synchronized (generationLock) {
            LOG.info("Constructing {}", entity.getClassName());
            // we need an jdo enhancer and a temporary classloader
            // to define classes in before enhancement
            MDSClassLoader tmpClassLoader = new MDSClassLoader();
            MdsJDOEnhancer enhancer = createEnhancer(tmpClassLoader);

            // modify the existing metadata
            JDOMetadata jdoMetadata = metadataHolder.getJdoMetadata();
            JDOMetadata tmpMetadata = persistenceManagerFactory.newMetadata();

            metadataBuilder.addEntityMetadata(jdoMetadata, entity);
            metadataBuilder.addEntityMetadata(tmpMetadata, entity);

            ClassData classData = buildClass(entity);

            tmpClassLoader.defineClass(classData);
            enhancer.addClass(classData);

            enhancer.registerMetadata(tmpMetadata);

            // then, we commence with enhancement
            enhancer.enhance();

            // register
            String className = entity.getClassName();
            byte[] enhancedBytes = enhancer.getEnhancedBytes(className);

            ClassData enhancedClassData = new ClassData(className, enhancedBytes);

            // register as enhanced class data
            MotechClassPool.registerEnhancedClassData(enhancedClassData);
            // define in the MDS classloader, so that the persistence manager sees this class
            MDSClassLoader.getInstance().defineClass(enhancedClassData);

            // create appropriate class that will hold the history of changes on entity
            buildHistory(tmpClassLoader, enhancer, entity);

            // build infrastructure classes such as services and repositories
            buildInfrastructure(entity);
        }
    }

    @Override
    public void constructAllEntities(boolean buildDDE) {
        synchronized (generationLock) {
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
            MDSClassLoader tmpClassLoader = new MDSClassLoader();
            MdsJDOEnhancer enhancer = createEnhancer(tmpClassLoader);

            // process only entities that are not drafts
            List<Entity> entities = allEntities.retrieveAll();
            filterEntities(entities, buildDDE);

            // generate jdo metadata from scratch for our entities
            JDOMetadata jdoMetadata = metadataHolder.reloadMetadata();
            for (Entity entity : entities) {
                metadataBuilder.addEntityMetadata(jdoMetadata, entity);
            }

            // next we create the java classes and add them to both
            // the temporary classloader and enhancer
            for (Entity entity : entities) {
                LOG.debug("Generating a class for {}", entity.getClassName());

                ClassData classData = buildClass(entity);

                tmpClassLoader.defineClass(classData);
                enhancer.addClass(classData);
            }

            // after the classes are defined, we register their metadata
            enhancer.registerMetadata(jdoMetadata);

            // then, we commence with enhancement
            enhancer.enhance();

            // we register the enhanced class bytes
            // and build the infrastructure classes
            for (Entity entity : entities) {
                String className = entity.getClassName();

                LOG.debug("Registering {}", className);

                byte[] enhancedBytes = enhancer.getEnhancedBytes(className);

                // register
                ClassData enhancedData = new ClassData(className, enhancedBytes);
                MotechClassPool.registerEnhancedClassData(enhancedData);

                // register with the classloader so that we avoid issues with the persistence manager
                MDSClassLoader.getInstance().defineClass(enhancedData);

                LOG.debug("Building history for {}", className);
                buildHistory(tmpClassLoader, enhancer, entity);

                LOG.debug("Building infrastructure for {}", className);
                buildInfrastructure(entity);
            }
        }
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

    private void buildHistory(MDSClassLoader tmpClassLoader, MdsJDOEnhancer enhancer,
                              Entity entity) {
        LOG.info("Constructing history class for: {}", entity.getClassName());
        ClassData classData = entityBuilder.buildHistory(entity);

        // modify the existing metadata
        JDOMetadata jdoMetadata = metadataHolder.getJdoMetadata();
        JDOMetadata tmpMetadata = persistenceManagerFactory.newMetadata();

        metadataBuilder.addBaseMetadata(jdoMetadata, classData);
        metadataBuilder.addBaseMetadata(tmpMetadata, classData);

        tmpClassLoader.defineClass(classData);
        enhancer.addClass(classData);

        enhancer.registerMetadata(tmpMetadata);

        // then, we commence with enhancement
        enhancer.enhance();

        // register
        String className = classData.getClassName();
        byte[] enhancedBytes = enhancer.getEnhancedBytes(className);

        ClassData enhancedClassData = new ClassData(className, enhancedBytes);

        // register as enhanced class data
        MotechClassPool.registerEnhancedClassData(enhancedClassData);
        // define in the MDS classloader, so that the persistence manager sees this class
        MDSClassLoader.getInstance().defineClass(enhancedClassData);

        // build infrastructure classes
        buildInfrastructure(className);
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

    private void buildInfrastructure(String className) {
        List<ClassData> infrastructure = infrastructureBuilder.buildHistoryInfrastructure(className);
        for (ClassData classData : infrastructure) {
            MDSClassLoader.getInstance().defineClass(classData);
        }
    }

    private void filterEntities(List<Entity> entities, boolean buildDDE) {
        Iterator<Entity> it = entities.iterator();
        while (it.hasNext()) {
            Entity entity = it.next();

            // DDEs are generated when their declaring bundles context is loaded
            if (entity.isDraft() || (!buildDDE && entity.isDDE())) {
                it.remove();
            } else if (entity.isDDE()) {
                Bundle declaringBundle = WebBundleUtil.findBundleByName(bundleContext, entity.getModule());

                if (declaringBundle == null) {
                    LOG.warn("Declaring bundle unavailable for entity {]", entity.getClassName());
                    it.remove();
                } else {
                    try {
                        declaringBundle.loadClass(entity.getClassName());
                    } catch (ClassNotFoundException e) {
                        LOG.warn("Class declaration for {} not present in bundle {}",
                                entity.getClassName(), declaringBundle.getSymbolicName());
                        it.remove();
                    }
                }
            }
        }
    }

    private MdsJDOEnhancer createEnhancer(ClassLoader enhancerClassLoader) {
        Properties config = settingsFacade.getProperties(Constants.Config.DATANUCLEUS_FILE);
        return new MdsJDOEnhancer(config, enhancerClassLoader);
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
    public void setSettingsFacade(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
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
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }
}
