package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.factory.EntityMetadataFactory;
import org.motechproject.mds.repository.AllEntityMappings;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.EntityBuilder;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JDOClassLoader;
import org.motechproject.mds.service.MdsJDOEnhancer;
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
                .withSingleName(entity.getName())
                .withClassLoader(new JDOClassLoader(JDOClassLoader.PERSISTANCE_CLASS_LOADER));

        String className = builder.getClassName();
        byte[] enhancedBytes = new MdsJDOEnhancer(getSettingsFacade()).enhance(builder);

        JDOClassLoader.PERSISTANCE_CLASS_LOADER.defineClass(className, enhancedBytes);
        JDOMetadata metadata = EntityMetadataFactory.createBaseEntity(
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
}
