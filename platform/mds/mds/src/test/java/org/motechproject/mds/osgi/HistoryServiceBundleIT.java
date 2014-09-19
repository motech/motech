package org.motechproject.mds.osgi;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.MotechException;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.HistoryTrashClassHelper;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import javax.inject.Inject;
import javax.jdo.Query;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class HistoryServiceBundleIT extends AbstractMdsBundleIT {

    private static final String[] ORIGINAL_VALUES = {"Maecenas", "ut", "justo", "porta", "fermentum", "tellus"};

    private static final String LOREM = "Lorem";
    private static final String LOREM_CLASS = getGeneratedClassName("Lorem");
    private static final String IPSUM = "ipsum";

    private HistoryService historyService;

    @Inject
    private EntityService entityService;

    @Inject
    private JarGeneratorService jarGeneratorService;

    @Inject
    private BundleContext bundleContext;

    @Before
    public void setUp() throws IOException, ClassNotFoundException {
        if (getService() == null) {
            setUpEntity();
        }

        // these services get registered only after the bundle gets generated
        historyService = ServiceRetriever.getService(bundleContext, HistoryService.class);

        // drop all history records
        clearHistoryRecords();
    }

    @Test
    public void shouldCreateHistoricalRecord() throws Exception {
        Object instance = createInstance(ORIGINAL_VALUES[0]);

        QueryParams queryParams = new QueryParams(1, 10, null);
        List records = historyService.getHistoryForInstance(instance, queryParams);
        // The latest revision should not be present in the result
        assertRecords(records, 0);

        updateInstance(instance, "newVal");

        records = historyService.getHistoryForInstance(instance, queryParams);
        // After first edit we should have exactly 1 record
        assertRecords(records, 1);

        updateInstance(instance, "newVal2");
        updateInstance(instance, "newVal3");
        updateInstance(instance, "newVal4");

        records = historyService.getHistoryForInstance(instance, queryParams);
        // After three more edits we should have exactly 4 records
        assertRecords(records, 4);

        String currentValue = (String) PropertyUtil.safeGetProperty(instance, IPSUM);

        // Assert that the instance indeed holds the value from last update
        assertEquals("newVal4", currentValue);

        // Assert that there's indeed no last revision on the list of historical revisions
        for (Object historyRecord : records) {
            assertFalse((PropertyUtil.safeGetProperty(historyRecord, IPSUM)).equals("newVal4"));
        }
    }

    @Test
    public void shouldNotMixHistoricalRecords() throws Exception {
        // creates and updates instances one after another
        QueryParams queryParams = new QueryParams(1,10,null);
        Object instance1 = createInstance(ORIGINAL_VALUES[0]);
        instance1 = updateInstance(instance1, ORIGINAL_VALUES[2]);
        instance1 = updateInstance(instance1, ORIGINAL_VALUES[4]);

        Object instance2 = createInstance(ORIGINAL_VALUES[1]);
        instance2 = updateInstance(instance2, ORIGINAL_VALUES[3]);
        instance2 = updateInstance(instance2, ORIGINAL_VALUES[5]);

        // creates instances and then updates them alternately
        Object instance3 = createInstance(ORIGINAL_VALUES[0]);
        Object instance4 = createInstance(ORIGINAL_VALUES[1]);

        instance3 = updateInstance(instance3, ORIGINAL_VALUES[2]);
        instance4 = updateInstance(instance4, ORIGINAL_VALUES[3]);

        instance3 = updateInstance(instance3, ORIGINAL_VALUES[4]);
        instance4 = updateInstance(instance4, ORIGINAL_VALUES[5]);

        // Each instance has been edited 2 times, therefore we are expecting two records
        List records1 = historyService.getHistoryForInstance(instance1, queryParams);
        assertRecords(records1, 2);

        List records2 = historyService.getHistoryForInstance(instance2, queryParams);
        assertRecords(records2, 2);

        List records3 = historyService.getHistoryForInstance(instance3, queryParams);
        assertRecords(records3, 2);

        List records4 = historyService.getHistoryForInstance(instance4, queryParams);
        assertRecords(records4, 2);

        for (int i = 0; i < ORIGINAL_VALUES.length - 2; ++i) {
            List list1 = i % 2 == 0 ? records1 : records2;
            hasRecord(list1, ORIGINAL_VALUES[i]);

            List list2 = i % 2 == 0 ? records3 : records4;
            hasRecord(list2, ORIGINAL_VALUES[i]);
        }
    }

    @Test
    public void shouldRemoveOnlyCorrectRecords() throws Exception {
        QueryParams queryParams = new QueryParams(1,10,null);
        Object instance1 = createInstance(ORIGINAL_VALUES[0]);
        instance1 = updateInstance(instance1, ORIGINAL_VALUES[2]);
        instance1 = updateInstance(instance1, ORIGINAL_VALUES[4]);

        Object instance2 = createInstance(ORIGINAL_VALUES[1]);
        instance2 = updateInstance(instance2, ORIGINAL_VALUES[3]);
        instance2 = updateInstance(instance2, ORIGINAL_VALUES[5]);

        historyService.remove(instance1);

        List records1 = historyService.getHistoryForInstance(instance1, queryParams);
        assertRecords(records1, 0);

        List records2 = historyService.getHistoryForInstance(instance2, null);
        assertRecords(records2, 2);

        for (int i = 1; i < ORIGINAL_VALUES.length - 2; i += 2) {
            hasRecord(records2, ORIGINAL_VALUES[i]);
        }
    }

    @Test
    public void shouldConnectHistoricalRecordsWithTrashInstance() throws Exception {
        QueryParams queryParams = new QueryParams(1, 10);
        Object instance1 = createInstance(ORIGINAL_VALUES[0]);
        instance1 = updateInstance(instance1, ORIGINAL_VALUES[2]);
        instance1 = updateInstance(instance1, ORIGINAL_VALUES[4]);

        Object instance2 = createInstance(ORIGINAL_VALUES[1]);
        instance2 = updateInstance(instance2, ORIGINAL_VALUES[3]);
        instance2 = updateInstance(instance2, ORIGINAL_VALUES[5]);

        List records1 = historyService.getHistoryForInstance(instance1, null);
        assertRecords(records1, 2);

        List records2 = historyService.getHistoryForInstance(instance2, queryParams);
        assertRecords(records2, 2);

        getService().delete(instance1);

        records1 = historyService.getHistoryForInstance(instance1, queryParams);
        assertRecords(records1, 0);

        records2 = historyService.getHistoryForInstance(instance2, queryParams);
        assertRecords(records2, 2);

        final Class<?> historyClass = getEntityClass(bundleContext, ClassName.getHistoryClassName(LOREM_CLASS));
        List collection = (List) getService().executeQuery(new QueryExecution() {
            @Override
            public Object execute(Query query, InstanceSecurityRestriction restriction) {
                query.setClass(historyClass);
                return query.execute();
            }
        });

        // by default deleted instances are moved to the MDS trash and their historical data are
        // still accessible by query
        assertRecords(collection, 6);

        for (int i = 0; i < ORIGINAL_VALUES.length; ++i) {
            Object record = hasRecord(collection, ORIGINAL_VALUES[i]);
            Object property = PropertyUtil.safeGetProperty(record, HistoryTrashClassHelper.trashFlag(historyClass));

            // even records should have set trash flag
            // odd records should have unset trash flag
            assertEquals(i % 2 == 0, property);
        }
    }

    private void assertRecords(Collection records, int size) {
        boolean isEmpty = size == 0;

        assertEquals(isEmpty ? "There should be no records" : "There are no records", records.isEmpty(), isEmpty);
        assertEquals(String.format("There should be exactly %d record(s)", size), size, records.size());
    }

    private Object hasRecord(List records, String value) {
        Object record = null;

        for (Object r : records) {
            String original = (String) PropertyUtil.safeGetProperty(r, IPSUM);

            if (equalsIgnoreCase(value, original)) {
                record = r;
                break;
            }
        }

        assertNotNull("There should be a record with " + IPSUM + " property equal to " + value, record);

        return record;
    }

    private Object createInstance(String value) throws Exception {
        Object instance = getEntityClass(bundleContext, LOREM_CLASS).newInstance();

        PropertyUtil.safeSetProperty(instance, IPSUM, value);

        return getService().create(instance);
    }

    private Object updateInstance(Object instance, String value) {
        PropertyUtil.safeSetProperty(instance, IPSUM, value);
        return getService().update(instance);
    }

    private void setUpEntity() throws IOException {
        clearEntities(entityService, LOREM_CLASS);

        EntityDto entity = new EntityDto(LOREM);
        entity = entityService.createEntity(entity);

        FieldDto field = new FieldDto(null, entity.getId(), TypeDto.STRING,
                new FieldBasicDto("ipsumDisp", IPSUM), false, null);

        entityService.addFields(entity, asList(field));

        jarGeneratorService.regenerateMdsDataBundle(true);
    }

    private MotechDataService getService() {
        try {
            final String serviceName = ClassName.getInterfaceName(LOREM_CLASS);
            ServiceReference[] refs = bundleContext.getAllServiceReferences(serviceName, null);
            return (ArrayUtils.isNotEmpty(refs)) ? (MotechDataService) bundleContext.getService(refs[0]) : null;
        } catch (InvalidSyntaxException e) {
            throw new MotechException("Invalid syntax", e);
        }
    }

    private void clearHistoryRecords() throws ClassNotFoundException {
        final Class<?> historyClass = getEntityClass(bundleContext, ClassName.getHistoryClassName(LOREM_CLASS));
        getService().executeQuery(new QueryExecution() {
            @Override
            public Object execute(Query query, InstanceSecurityRestriction restriction) {
                query.setClass(historyClass);
                return query.deletePersistentAll();
            }
        });
    }
}
