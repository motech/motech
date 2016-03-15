package org.motechproject.mds.performance.osgi;

import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.performance.service.MdsDummyDataGenerator;
import org.motechproject.mds.performance.service.impl.MdsDummyDataGeneratorImpl;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.orm.jdo.LocalPersistenceManagerFactoryBean;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.datastore.JDOConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.motechproject.mds.util.Constants.BundleNames.MDS_BUNDLE_SYMBOLIC_NAME;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MdsDiskSpaceUsageIT extends LoggingPerformanceIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(MdsDiskSpaceUsageIT.class);

    private static final int ENTITIES = 1;
    private static final int FIELDS = 5;
    private static final int INSTANCES = Integer.parseInt(System.getProperty("mds.performance.quantity"));
    private static final int LOOKUPS = 0;
    private static final String SQLQUERY = "select sum((data_length+index_length)/1024/1024) AS MB from information_schema.tables" +
            " where table_schema = \"%s\";";

    private MdsDummyDataGenerator generator;
    @Inject
    private EntityService entityService;
    @Inject
    private JarGeneratorService jarGeneratorService;
    @Inject
    private BundleContext bundleContext;

    @Before
    public void setUp() {
        setUpSecurityContext("motech_bot", "motech", "mdsSchemaAccess");

        generator = new MdsDummyDataGeneratorImpl(entityService, jarGeneratorService, bundleContext);
        generator.clearEntities();
        generator.setEntityPrefix("DiskSpaceUsageITEntity");
    }

    @After
    public void tearDown() throws Exception {
        generator.clearEntities();
    }

    @Test
    public void testEudeDiskSpaceUsage ()
            throws IOException, IllegalAccessException, ClassNotFoundException, InstantiationException, SQLException {

        LOGGER.info("Creating entity");
        generator.generateDummyEntities(ENTITIES, FIELDS, LOOKUPS, true);

        EntityDto entityDto = entityService.getEntityByClassName
                (Constants.Packages.ENTITY.concat(".").concat(generator.getEntityPrefix()).concat("0"));

        LOGGER.info("Creating {} instances for entity", INSTANCES);
        generator.generateDummyInstances(entityDto.getId(), INSTANCES);

        WebApplicationContext context = ServiceRetriever.getWebAppContext(bundleContext, MDS_BUNDLE_SYMBOLIC_NAME);

        LocalPersistenceManagerFactoryBean dataPersistenceManagerFactoryBean = (LocalPersistenceManagerFactoryBean) context.getBean(BeanFactory.FACTORY_BEAN_PREFIX + "dataPersistenceManagerFactoryBean");
        LocalPersistenceManagerFactoryBean schemaPersistenceManagerFactoryBean = (LocalPersistenceManagerFactoryBean) context.getBean(BeanFactory.FACTORY_BEAN_PREFIX + "persistenceManagerFactoryBean");

        PersistenceManagerFactory dataPersistenceManagerFactory = dataPersistenceManagerFactoryBean.getObject();
        PersistenceManagerFactory schemaPersistenceManagerFactory = schemaPersistenceManagerFactoryBean.getObject();

        JDOConnection dataCon = dataPersistenceManagerFactory.getPersistenceManager().getDataStoreConnection();
        JDOConnection schemaCon = schemaPersistenceManagerFactory.getPersistenceManager().getDataStoreConnection();

        Connection dataNativeCon = (Connection) dataCon.getNativeConnection();
        Connection schemaNativeCon = (Connection) schemaCon.getNativeConnection();

        Statement dataStmt = dataNativeCon.createStatement();
        Statement schemaStmt = schemaNativeCon.createStatement();

        ResultSet dataResultSet = dataStmt.executeQuery(String.format(SQLQUERY, "motechdata"));
        dataResultSet.absolute(1);
        double spaceUsage = dataResultSet.getDouble("MB");

        ResultSet schemaResultSet = schemaStmt.executeQuery(String.format(SQLQUERY, "motechschema"));
        schemaResultSet.absolute(1);
        spaceUsage += schemaResultSet.getDouble("MB");

        LOGGER.info("Disk space usage of Motech Data Services database after creating {} instances is {} MB", INSTANCES, spaceUsage);
        logToFile(spaceUsage);

        Bundle entitiesBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, MDS_ENTITIES_SYMBOLIC_NAME);
        MotechDataService service = generator.getService(entitiesBundle.getBundleContext(), entityDto.getClassName());
        service.deleteAll();
    }
}