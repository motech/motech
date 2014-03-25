package org.motechproject.mds;

import javassist.CtClass;
import org.apache.commons.beanutils.PropertyUtils;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.builder.MDSConstructor;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MetadataHolder;
import org.motechproject.mds.service.DefaultMotechDataService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.ClassName;
import org.springframework.beans.factory.annotation.Autowired;

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
    private HistoryService historyServiceMock;

    private MotechDataService service;

    /**
     * This method should return the name of the entity used in tests.
     * @return the entity name
     */
    protected abstract String getEntityName();

    /**
     * This method should return the list of fields which will be added to the generated entity.
     * @return A list of field dtos, which will be used during entity creation.
     */
    protected abstract List<FieldDto> getEntityFields();

    protected String getEntityClass() {
        return ClassName.getEntityName(getEntityName());
    }

    protected String getRepositoryClass() {
        return ClassName.getRepositoryName(getEntityName());
    }

    protected String getInterfaceClass() {
        return ClassName.getInterfaceName(getEntityName());
    }

    protected String getServiceClass() {
        return ClassName.getServiceName(getEntityName());
    }

    /**
     * Use this to get a handle for the generated instance service.
     * @return The generated service.
     */
    protected MotechDataService getService() {
        return service;
    }

    /**
     * Override this to inject an actual implementation of the history service into the instance service.
     * @return the history service that will be used, a mock by default
     */
    protected HistoryService getHistoryService() {
        return historyServiceMock;
    }

    /**
     * This should be called in the set up method for the test.
     * @throws Exception
     */
    public void setUpForInstanceTesting() throws Exception {
        MockitoAnnotations.initMocks(this);
        clearDB();
        MDSClassLoader.reloadClassLoader();
        metadataHolder.reloadMetadata();

        setUpEntity();

        service = createService();
    }


    private MotechDataService createService() throws Exception {
        Object repository = MDSClassLoader.getInstance().loadClass(getRepositoryClass()).newInstance();
        Object service = MDSClassLoader.getInstance().loadClass(getServiceClass()).newInstance();

        PropertyUtils.setProperty(repository, "persistenceManagerFactory", getPersistenceManagerFactory());
        PropertyUtils.setProperty(service, "repository", repository);
        PropertyUtils.setProperty(service, "allEntities", allEntities);
        PropertyUtils.setProperty(service, "historyService", getHistoryService());

        MotechDataService mds = (MotechDataService) service;
        ((DefaultMotechDataService) mds).initializeSecurityState();

        return mds;
    }

    private void setUpEntity() throws Exception {
        EntityDto entity = new EntityDto(getEntityClass());
        entity = entityService.createEntity(entity);

        entityService.addFields(entity, getEntityFields());

        mdsConstructor.constructEntities(true);
        getPersistenceManagerFactory().registerMetadata(metadataHolder.getJdoMetadata());

        CtClass ctClass = MotechClassPool.getDefault().get(getRepositoryClass());
        MDSClassLoader.getInstance().defineClass(getRepositoryClass(), ctClass.toBytecode());
        ctClass = MotechClassPool.getDefault().get(getInterfaceClass());
        MDSClassLoader.getInstance().defineClass(getInterfaceClass(), ctClass.toBytecode());
        ctClass = MotechClassPool.getDefault().get(getServiceClass());
        MDSClassLoader.getInstance().defineClass(getServiceClass(), ctClass.toBytecode());
    }
}
