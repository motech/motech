package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.exception.EntityAlreadyExistException;
import org.motechproject.mds.repository.EntityRepository;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.mapper.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntityServiceImpl implements EntityService {

    private EntityRepository entityRepository;

    @Autowired
    public EntityServiceImpl(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    @Transactional
    @Override
    public void create(EntityDto entityDto) {
        entityDto.validate();

        Entity entityToCreate = EntityMapper.map(entityDto);
        Entity existingEntity = entityRepository.findByName(entityToCreate.getName());
        if(existingEntity != null) {
            throw new EntityAlreadyExistException("key:mds.validation.error.entityAlreadyExist");
        }
        entityRepository.create(entityToCreate);
    }
}
