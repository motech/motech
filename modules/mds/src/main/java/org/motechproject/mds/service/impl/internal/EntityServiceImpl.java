package org.motechproject.mds.service.impl.internal;

import org.motechproject.mds.PersistanceClassLoader;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.builder.EntityMetadataBuilder;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.enhancer.MdsJDOEnhancer;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.repository.AllEntityMappings;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.metadata.JDOMetadata;
import java.io.IOException;

/**
 * Default implmenetation of {@link org.motechproject.mds.service.EntityService} interface.
 */
@Service
public class EntityServiceImpl extends BaseMdsService implements EntityService {
    private AllEntityMappings allEntityMappings;
    private MdsJDOEnhancer enhancer;
    private PersistanceClassLoader persistanceClassLoader;

    @Override
    @Transactional
    public EntityDto createEntity(EntityDto entity) throws IOException {
        if (entity.isReadOnly()) {
            throw new EntityReadOnlyException();
        }

        if (allEntityMappings.containsEntity(entity.getName())) {
            throw new EntityAlreadyExistException();
        }

        EntityBuilder builder = new EntityBuilder()
                .withSimpleName(entity.getName())
                .withClassLoader(persistanceClassLoader);

        String className = builder.getClassName();
        byte[] enhancedBytes = enhancer.enhance(builder);

        persistanceClassLoader.saveClass(className, enhancedBytes);
        JDOMetadata metadata = EntityMetadataBuilder.createBaseEntity(
                getPersistenceManagerFactory().newMetadata(), className
        );

        getPersistenceManagerFactory().registerMetadata(metadata);
        EntityMapping entityMapping = allEntityMappings.save(className);

        return entityMapping.toDto();
    }

    @Override
    @Transactional
    public void deleteEntity(Long id) {
        allEntityMappings.delete(id);
    }

    @Autowired
    public void setAllEntityMappings(AllEntityMappings allEntityMappings) {
        this.allEntityMappings = allEntityMappings;
    }

    @Autowired
    public void setEnhancer(MdsJDOEnhancer enhancer) {
        this.enhancer = enhancer;
    }

    @Autowired
    public void setPersistanceClassLoader(PersistanceClassLoader persistanceClassLoader) {
        this.persistanceClassLoader = persistanceClassLoader;
    }
}
