package org.motechproject.mds.jdo;


import org.apache.commons.io.IOUtils;
import org.datanucleus.StoreNucleusContext;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.store.schema.SchemaAwareStoreManager;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.util.ClassName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * The schema generator class is responsible for generating the table schema
 * for entities upon start. Schema for all entity classes has to be generated,
 * otherwise issues might arise in foreign key generation for example.
 * This code runs in the generated entities bundle.
 */
public class SchemaGenerator implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaGenerator.class);

    private JDOPersistenceManagerFactory persistenceManagerFactory;

    public SchemaGenerator(JDOPersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    @Override
    public void afterPropertiesSet() {
        generateSchema();
    }

    public void generateSchema() {
        try {
            Set<String> classNames = classNames();

            if (!classNames.isEmpty()) {
                SchemaAwareStoreManager storeManager = getStoreManager();
                storeManager.createSchemaForClasses(classNames, new Properties());
            }
        } catch (Exception e) {
            LOGGER.error("Error while creating initial entity schema", e);
        }
    }

    private Set<String> classNames() throws IOException {
        Set<String> classNames = new HashSet<>();
        Set<String> historyClassNames = new HashSet<>();
        ClassPathResource resourceClassNames = new ClassPathResource(JarGeneratorService.ENTITY_LIST_FILE);
        ClassPathResource resourceHistory = new ClassPathResource(JarGeneratorService.HISTORY_LIST_FILE);

        if (resourceHistory.exists()) {
            try (InputStream in = resourceHistory.getInputStream()) {
                for (Object line : IOUtils.readLines(in)) {
                    String className = (String) line;
                    historyClassNames.add(className);
                }
            }
        }

        if (resourceClassNames.exists()) {
            try (InputStream in = resourceClassNames.getInputStream()) {
                for (Object line : IOUtils.readLines(in)) {
                    String className = (String) line;

                    classNames.add(className);
                    if (historyClassNames.contains(className)) {
                        classNames.add(ClassName.getHistoryClassName(className));
                    }
                    classNames.add(ClassName.getTrashClassName(className));
                }
            }
        } else {
            LOGGER.warn("List of entity ClassNames is unavailable");
        }

        return classNames;
    }

    private SchemaAwareStoreManager getStoreManager() {
        StoreNucleusContext nucleusContext = persistenceManagerFactory.getNucleusContext();
        return (SchemaAwareStoreManager) nucleusContext.getStoreManager();
    }
}
