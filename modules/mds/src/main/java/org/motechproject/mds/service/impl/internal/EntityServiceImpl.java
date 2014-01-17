package org.motechproject.mds.service.impl.internal;

import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.repository.AllEntityMappings;
import org.motechproject.mds.repository.AllLookupMappings;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.MDSConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.motechproject.mds.builder.EntityBuilder.PACKAGE;

/**
 * Default implmenetation of {@link org.motechproject.mds.service.EntityService} interface.
 */
@Service
public class EntityServiceImpl extends BaseMdsService implements EntityService {
    private AllEntityMappings allEntityMappings;
    private MDSConstructor constructor;
    private AllLookupMappings allLookupMappings;

    @Override
    @Transactional
    public EntityDto createEntity(EntityDto entity) throws IOException {
        if (entity.isReadOnly()) {
            throw new EntityReadOnlyException();
        }

        if (allEntityMappings.containsEntity(entity.getName())) {
            throw new EntityAlreadyExistException();
        }

        String className = String.format("%s.%s", PACKAGE, entity.getName());
        EntityMapping entityMapping = allEntityMappings.save(className);
        constructor.constructEntity(entityMapping);

        return entityMapping.toDto();
    }

    @Override
    @Transactional
    public void deleteEntity(Long id) {
        allEntityMappings.delete(id);
    }

    @Override
    @Transactional
    public List<LookupDto> saveEntityLookups(Long entityId, List<LookupDto> lookups) {
        EntityMapping entity = allEntityMappings.getEntityById(entityId);

        if (entity == null) {
            throw new EntityNotFoundException();
        }

        if (entity.isReadOnly()) {
            throw new EntityReadOnlyException();
        }

        // updated lookups contain generated ids from db
        List<LookupDto> updatedLookups = new ArrayList<>();

        for (LookupDto lookup : lookups) {
            if (lookup.getId() == null || allLookupMappings.getLookupById(lookup.getId()) == null) {
                updatedLookups.add(allLookupMappings.save(lookup, entity).toDto());
            } else {
                updatedLookups.add(allLookupMappings.update(lookup).toDto());
            }
        }

        return updatedLookups;
    }

    @Autowired
    public void setAllEntityMappings(AllEntityMappings allEntityMappings) {
        this.allEntityMappings = allEntityMappings;
    }

    @Autowired
    public void setConstructor(MDSConstructor constructor) {
        this.constructor = constructor;
    }

    @Autowired
    public void setAllLookupMappings(AllLookupMappings allLookupMappings) {
        this.allLookupMappings = allLookupMappings;
    }
}
