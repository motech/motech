package org.motechproject.mds.service.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.service.MDSConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jdo.PersistenceManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.motechproject.mds.constants.Constants.Packages;

public class MDSConstructorIT extends BaseIT {
    private static final String VOUCHER_CLASS = String.format("%s.Voucher", Packages.ENTITY);
    private static final String VOUCHER_REPOSITORY = String.format("%s.AllVouchers", Packages.REPOSITORY);
    private static final String VOUCHER_INTERFACE = String.format("%s.VoucherService", Packages.SERVICE);
    private static final String VOUCHER_SERVICE = String.format("%s.VoucherServiceImpl", Packages.SERVICE_IMPL);

    @Autowired
    private MDSConstructor constructor;

    @Before
    public void setUp() throws Exception {
        getPersistenceManager().deletePersistentAll(getEntityMappings());
        PersistenceManager persistenceManager = getPersistenceManager();
        persistenceManager.makePersistent(new EntityMapping(VOUCHER_CLASS));
    }

    @After
    public void tearDown() throws Exception {
        getPersistenceManager().newQuery(EntityMapping.class).deletePersistentAll();
    }

    @Test
    public void testConstructEntity() throws Exception {
        EntityMapping mapping = new EntityMapping();
        mapping.setClassName(VOUCHER_CLASS);

        constructor.constructEntity(mapping);

        MDSClassLoader classLoader = MDSClassLoader.PERSISTANCE;

        Class<?> entityClass = classLoader.loadClass(VOUCHER_CLASS);
        Class<?> repositoryClass = classLoader.loadClass(VOUCHER_REPOSITORY);
        Class<?> interfaceClass = classLoader.loadClass(VOUCHER_INTERFACE);
        Class<?> serviceClass = classLoader.loadClass(VOUCHER_SERVICE);

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
