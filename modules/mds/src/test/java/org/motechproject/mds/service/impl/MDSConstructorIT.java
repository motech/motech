package org.motechproject.mds.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.repository.MotechDataRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jdo.PersistenceManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.motechproject.mds.builder.EntityInfrastructureBuilder.REPOSITORY_PACKAGE;
import static org.motechproject.mds.builder.EntityInfrastructureBuilder.SERVICE_IMPL_PACKAGE;
import static org.motechproject.mds.builder.EntityInfrastructureBuilder.SERVICE_PACKAGE;

public class MDSConstructorIT extends BaseIT {
    private static final String SAMPLE_CLASS = String.format("%s.Sample", EntityBuilder.PACKAGE);
    private static final String SAMPLE_REPOSITORY = String.format("%s.AllSamples", REPOSITORY_PACKAGE);
    private static final String SAMPLE_INTERFACE = String.format("%s.SampleService", SERVICE_PACKAGE);
    private static final String SAMPLE_SERVICE = String.format("%s.SampleServiceImpl", SERVICE_IMPL_PACKAGE);

    @Autowired
    private org.motechproject.mds.service.MDSConstructor constructor;

    @Before
    public void setUp() throws Exception {
        PersistenceManager persistenceManager = getPersistenceManager();
        persistenceManager.makePersistent(new EntityMapping(SAMPLE_CLASS));
    }

    @Test
    public void testConstructEntity() throws Exception {
        EntityMapping mapping = new EntityMapping();
        mapping.setClassName(SAMPLE_CLASS);

        constructor.constructEntity(mapping);

        MDSClassLoader classLoader = MDSClassLoader.PERSISTANCE;

        Class<?> entityClass = classLoader.loadClass(SAMPLE_CLASS);
        Class<?> repositoryClass = classLoader.loadClass(SAMPLE_REPOSITORY);
        Class<?> interfaceClass = classLoader.loadClass(SAMPLE_INTERFACE);
        Class<?> serviceClass = classLoader.loadClass(SAMPLE_SERVICE);

        assertNotNull(entityClass);
        assertNotNull(repositoryClass);
        assertNotNull(interfaceClass);
        assertNotNull(serviceClass);

        MotechDataRepository repository = (MotechDataRepository) repositoryClass.newInstance();
        DefaultMotechDataService service = (DefaultMotechDataService) serviceClass.newInstance();

        repository.setPersistenceManagerFactory(getPersistenceManagerFactory());
        service.setRepository(repository);

        Object sample = service.create(entityClass.newInstance());
        assertEquals(1, service.retrieveAll().size());

        service.delete(sample);
        assertTrue(service.retrieveAll().isEmpty());
    }
}
