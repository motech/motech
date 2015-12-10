package org.motechproject.mds.it;

import javassist.CtClass;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.mds.builder.MDSConstructor;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.entityinfo.EntityInfo;
import org.motechproject.mds.entityinfo.EntityInfoReader;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.entityinfo.FieldInfo;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.dto.TrackingDto;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MetadataHolder;
import org.motechproject.mds.service.DefaultMotechDataService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.TrashService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jdo.PersistenceManagerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Base for ITs that will manipulate entity instances.
 */
public abstract class BaseInstanceIT extends BaseIT {

    @Autowired
    private EntityService entityService;

    @Autowired
    private MDSConstructor mdsConstructor;

    @Autowired
    private AllEntities allEntities;

    @Autowired
    private MetadataHolder metadataHolder;

    @Mock
    private TrashService trashServiceMock;

    @Mock
    private HistoryService historyServiceMock;

    private MotechDataService service;
    private EntityDto entity;

    /**
     * This method should return the name of the entity used in tests.
     *
     * @return the entity name
     */
    protected abstract String getEntityName();

    /**
     * This method should return the list of fields which will be added to the generated entity.
     *
     * @return A list of field dtos, which will be used during entity creation.
     */
    protected abstract List<FieldDto> getEntityFields();

    protected String getEntityClassName() {
        return ClassName.getEntityClassName(getEntityName());
    }

    protected String getHistoryClassName() {
        return ClassName.getHistoryClassName(getEntityClassName());
    }

    protected String getTrashClassName() {
        return ClassName.getTrashClassName(getEntityClassName());
    }

    protected Class<?> getEntityClass() throws ClassNotFoundException {
        return getClass(getEntityClassName());
    }

    protected Class<?> getHistoryClass() throws ClassNotFoundException {
        return getClass(getHistoryClassName());
    }

    protected Class<?> getTrashClass() throws ClassNotFoundException {
        return getClass(getTrashClassName());
    }

    protected Class<?> getClass(String name) throws ClassNotFoundException {
        return MDSClassLoader.getInstance().loadClass(name);
    }

    protected String getRepositoryClass() {
        return ClassName.getRepositoryName(getEntityName());
    }

    protected String getInterfaceClass() {
        return ClassName.getInterfaceName(getEntityName());
    }

    protected String getServiceClass() {
        return ClassName.getServiceClassName(getEntityName());
    }

    protected EntityDto getEntity() {
        return entity;
    }

    protected AllEntities getAllEntities() {
        return allEntities;
    }

    /**
     * Use this to get a handle for the generated instance service.
     *
     * @return The generated service.
     */
    protected MotechDataService getService() {
        return service;
    }

    /**
     * Override this to inject an actual implementation of the history service into the instance service.
     *
     * @return the history service that will be used, a mock by default
     */
    protected HistoryService getHistoryService() {
        return historyServiceMock;
    }

    /**
     * Override this to inject an actual implementation of the trash service into the instance service.
     *
     * @return the trash service that will be used, a mock by default
     */
    protected TrashService getTrashService() {
        return trashServiceMock;
    }

    /**
     * This should be called in the set up method for the test.
     *
     * @throws Exception
     */
    public void setUpForInstanceTesting() throws Exception {
        MockitoAnnotations.initMocks(this);
        MotechClassPool.clearEnhancedData();
        MDSClassLoader.reloadClassLoader();

        metadataHolder.reloadMetadata();

        setUpEntity();

        service = createService();
    }


    private MotechDataService createService() throws Exception {
        Object repository = MDSClassLoader.getInstance().loadClass(getRepositoryClass()).newInstance();
        Object service = MDSClassLoader.getInstance().loadClass(getServiceClass()).newInstance();

        EntityInfoReader entityInfoReader = new EntityInfoReader() {
            @Override
            public EntityInfo getEntityInfo(String entityClassName) {
                EntityInfo info = new EntityInfo();
                info.setEntity(entity);
                info.setAdvancedSettings(new AdvancedSettingsDto());

                List<FieldInfo> fieldInfos = new ArrayList<>();
                for (FieldDto fieldDto : getEntityFields()) {
                    FieldInfo fieldInfo = new FieldInfo();
                    fieldInfo.setField(fieldDto);
                    fieldInfos.add(fieldInfo);
                }

                info.setFieldsInfo(fieldInfos);

                return info;
            }
        };

        PropertyUtil.safeSetProperty(repository, "persistenceManagerFactory", getPersistenceManagerFactory());
        PropertyUtil.safeSetProperty(service, "transactionManager", getTransactionManager());
        PropertyUtil.safeSetProperty(service, "repository", repository);
        PropertyUtil.safeSetProperty(service, "allEntities", allEntities);
        PropertyUtil.safeSetProperty(service, "entityInfoReader", entityInfoReader);
        PropertyUtil.safeSetProperty(service, "historyService", getHistoryService());
        PropertyUtil.safeSetProperty(service, "trashService", getTrashService());

        MotechDataService mds = (MotechDataService) service;
        ((DefaultMotechDataService) mds).init();

        return mds;
    }

    private void setUpEntity() throws Exception {
        String entityClass = getEntityClassName();
        entity = new EntityDto(entityClass);
        entity.setRecordHistory(true);
        entity = entityService.createEntity(entity);

        entityService.addFields(entity, getEntityFields());

        TrackingDto tracking = entityService.getAdvancedSettings(entity.getId(), true).getTracking();
        tracking.setAllowCreateEvent(false);
        tracking.setAllowUpdateEvent(false);
        tracking.setAllowDeleteEvent(false);
        entityService.updateTracking(entity.getId(), tracking);

        SchemaHolder schemaHolder = entityService.getSchema();
        mdsConstructor.constructEntities(schemaHolder);

        PersistenceManagerFactory factory = getPersistenceManagerFactory();

        if (null == factory.getMetadata(entityClass)) {
            factory.registerMetadata(metadataHolder.getJdoMetadata());
        }

        CtClass ctClass = MotechClassPool.getDefault().get(getRepositoryClass());
        MDSClassLoader.getInstance().safeDefineClass(getRepositoryClass(), ctClass.toBytecode());
        ctClass = MotechClassPool.getDefault().get(getInterfaceClass());
        MDSClassLoader.getInstance().safeDefineClass(getInterfaceClass(), ctClass.toBytecode());
        ctClass = MotechClassPool.getDefault().get(getServiceClass());
        MDSClassLoader.getInstance().safeDefineClass(getServiceClass(), ctClass.toBytecode());
    }
}
