package org.motechproject.mds.service.impl.internal;

import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.InstanceService;
import org.motechproject.mds.service.impl.DefaultMotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.web.ExampleData;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.PreviousRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the {@link org.motechproject.mds.service.InstanceService} interface.
 */
@Service
public class InstanceServiceImpl extends BaseMdsService implements InstanceService {
    private AllEntities allEntities;

    // TODO remove this once everything is in db
    private ExampleData exampleData = new ExampleData();
    private MDSClassLoader mdsClassLoader = MDSClassLoader.getInstance();

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceServiceImpl.class);

    @Override
    @Transactional
    public Object createInstance(EntityDto entityDto, List<FieldRecord> fieldRecords) {
        try {
            Class<?> entityClass = mdsClassLoader.loadClass(ClassName.getEntityName(entityDto.getClassName()));
            Class<?> repositoryClass = mdsClassLoader.loadClass(ClassName.getRepositoryName(entityDto.getClassName()));
            Class<?> serviceClass = mdsClassLoader.loadClass(ClassName.getServiceName(entityDto.getClassName()));

            MotechDataRepository repository = (MotechDataRepository) repositoryClass.newInstance();
            DefaultMotechDataService service = (DefaultMotechDataService) serviceClass.newInstance();

            service.setRepository(repository);
            return service.create(entityClass.newInstance());

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException  e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    @Override
    @Transactional
    public List<?> getEntityRecordsPaged(Long entityId, Integer page, Integer rows) {
        List<?> list = new ArrayList<>();
        String className = ClassName.getSimpleName(allEntities.retrieveById(entityId).getClassName());
        try {
            Class<?> repositoryClass = mdsClassLoader.loadClass(ClassName.getRepositoryName(className));
            Class<?> serviceClass = mdsClassLoader.loadClass(ClassName.getServiceName(className));

            MotechDataRepository repository = (MotechDataRepository) repositoryClass.newInstance();

            DefaultMotechDataService service = (DefaultMotechDataService) serviceClass.newInstance();
            service.setRepository(repository);

            list = service.retrieveAll(page, rows);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return list;
    }

    @Override
    @Transactional
    public List<EntityRecord> getEntityRecords(Long entityId) {
        return exampleData.getEntityRecordsById(entityId);
    }

    @Override
    @Transactional
    public List<FieldInstanceDto> getInstanceFields(Long instanceId) {
        return exampleData.getInstanceFields(instanceId);
    }

    @Override
    @Transactional
    public List<HistoryRecord> getInstanceHistory(Long instanceId) {
        return exampleData.getInstanceHistoryRecordsById(instanceId);
    }

    @Override
    @Transactional
    public List<PreviousRecord> getPreviousRecords(Long instanceId) {
        return exampleData.getPreviousRecordsById(instanceId);
    }

    @Autowired
    public void setAllEntityMappings(AllEntities allEntities) {
        this.allEntities = allEntities;
    }
}
