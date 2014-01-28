package org.motechproject.mds.service.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.repository.AllEntityMappings;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.service.MDSConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.motechproject.mds.constants.Constants.Packages;

public class MDSConstructorIT extends BaseIT {
    private static final String SIMPLE_NAME = "Constructor";
    private static final String CLASS_NAME = String.format("%s.%s", Packages.ENTITY, SIMPLE_NAME);
    private static final String REPOSITORY_NAME = String.format("%s.All%ss", Packages.REPOSITORY, SIMPLE_NAME);
    private static final String INTERFACE_NAME = String.format("%s.%sService", Packages.SERVICE, SIMPLE_NAME);
    private static final String SERVICE_NAME = String.format("%s.%sServiceImpl", Packages.SERVICE_IMPL, SIMPLE_NAME);

    @Autowired
    private MDSConstructor constructor;

    @Autowired
    private AllEntityMappings allEntityMappings;

    private EntityMapping entity;

    @Before
    public void setUp() throws Exception {
        entity = allEntityMappings.save(new EntityDto(CLASS_NAME));
    }

    @After
    public void tearDown() throws Exception {
        allEntityMappings.delete(entity.getId());
    }

    @Test
    public void testConstructEntity() throws Exception {
        EntityMapping mapping = new EntityMapping();
        mapping.setClassName(CLASS_NAME);

        constructor.constructEntity(mapping);

        MDSClassLoader classLoader = MDSClassLoader.PERSISTANCE;

        Class<?> entityClass = classLoader.loadClass(CLASS_NAME);
        Class<?> repositoryClass = classLoader.loadClass(REPOSITORY_NAME);
        Class<?> interfaceClass = classLoader.loadClass(INTERFACE_NAME);
        Class<?> serviceClass = classLoader.loadClass(SERVICE_NAME);

        assertNotNull(entityClass);
        assertNotNull(repositoryClass);
        assertNotNull(interfaceClass);
        assertNotNull(serviceClass);

        MotechDataRepository repository = (MotechDataRepository) repositoryClass.newInstance();
        DefaultMotechDataService service = (DefaultMotechDataService) serviceClass.newInstance();

        repository.setPersistenceManagerFactory(getPersistenceManagerFactory());
        service.setRepository(repository);

        Object Voucher = service.create(entityClass.newInstance());
        assertEquals(1, service.retrieveAll().size());

        service.delete(Voucher);
        assertTrue(service.retrieveAll().isEmpty());
    }
}
