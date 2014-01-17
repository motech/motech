package org.motechproject.mds.service.impl.internal;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mds.builder.EntityInfrastructureBuilder;
import org.motechproject.mds.builder.EntityMetadataBuilder;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.enhancer.MdsJDOEnhancer;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.MDSConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.metadata.JDOMetadata;
import java.io.IOException;
import java.util.List;

import static org.motechproject.mds.builder.EntityInfrastructureBuilder.ClassMapping;

/**
 * Default implmenetation of {@link org.motechproject.mds.service.MDSConstructor} interface.
 */
@Service
public class MDSConstructorImpl extends BaseMdsService implements MDSConstructor {
    private MdsJDOEnhancer enhancer;

    @Override
    @Transactional
    public void constructEntity(EntityMapping mapping) throws IOException {
        MDSClassLoader classLoader = MDSClassLoader.PERSISTANCE;
        byte[] enhancedBytes = enhancer.enhance(mapping.getClassName());

        Class<?> clazz = classLoader.defineClass(mapping.getClassName(), enhancedBytes);
        JDOMetadata metadata = EntityMetadataBuilder.createBaseEntity(
                getPersistenceManagerFactory().newMetadata(), mapping.getClassName()
        );

        getPersistenceManagerFactory().registerMetadata(metadata);

        List<ClassMapping> classes = EntityInfrastructureBuilder.create(classLoader, clazz);

        if (CollectionUtils.isNotEmpty(classes)) {
            for (ClassMapping c : classes) {
                classLoader.defineClass(c.getClassName(), c.getBytecode());
            }
        }
    }

    @Autowired
    public void setEnhancer(MdsJDOEnhancer enhancer) {
        this.enhancer = enhancer;
    }

}
