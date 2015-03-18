package org.motechproject.mds;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.service.DefaultMotechDataService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.testutil.FieldTestHelper;
import org.motechproject.mds.testutil.records.Record;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.core.io.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MdsDataProviderTest {

    private static final String PARAM_VALUE = "param";
    private static final long ENTITY_ID = 4;
    private static final Long INSTANCE_ID = 1L;
    private static final String FIND_BY_ID_LOOKUP = "mds.dataprovider.byinstanceid";
    private static final String ID_LOOKUP_FIELD = "mds.dataprovider.instanceid";

    @Mock
    private EntityService entityService;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceReference serviceReference;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private EntityDto entity;

    private MDSDataProvider dataProvider;

    @Before
    public void setUp() {
        when(entityService.getEntityByClassName(Record.class.getName())).thenReturn(entity);
        when(entity.getId()).thenReturn(ENTITY_ID);

        FieldDto fieldDto = FieldTestHelper.fieldDto(1L, "field", String.class.getName(), "disp", null);
        when(entityService.getEntityFields(ENTITY_ID)).thenReturn(asList(fieldDto));

        LookupFieldDto lookupField = FieldTestHelper.lookupFieldDto(1L, "field");
        LookupDto singleLookup = new LookupDto("singleLookup", true, false, asList(lookupField), false);
        LookupDto multiLookup = new LookupDto("multiLookup", false, false, asList(lookupField), false);

        when(entityService.getLookupByName(ENTITY_ID, "singleLookup")).thenReturn(singleLookup);
        when(entityService.getLookupByName(ENTITY_ID, "multiLookup")).thenReturn(multiLookup);

        when(bundleContext.getServiceReference(LookupService.class.getName())).thenReturn(serviceReference);
        when(bundleContext.getService(serviceReference)).thenReturn(new LookupService());

        dataProvider = new MDSDataProvider(resourceLoader);
        dataProvider.setEntityService(entityService);
        dataProvider.setBundleContext(bundleContext);

        MotechClassPool.registerServiceInterface(Record.class.getName(), LookupService.class.getName());
    }

    @Test
    public void testSingleResultLookup() {
        Map<String, String> lookupMap = new HashMap<>();
        lookupMap.put("field", PARAM_VALUE);

        Object result = dataProvider.lookup(Record.class.getName(), "singleLookup", lookupMap);

        assertNotNull(result);
        assertTrue("Wrong type returned", result instanceof Record);
        Record record = (Record) result;
        assertEquals("single", record.getValue());
    }

    @Test
    public void testMultiResultLookup() {
        Map<String, String> lookupMap = new HashMap<>();
        lookupMap.put("field", PARAM_VALUE);

        Object result = dataProvider.lookup(Record.class.getName(), "multiLookup", lookupMap);

        // we expect it will return the record as result if there is only item
        assertNotNull(result);
        assertTrue("Wrong type returned", result instanceof Record);
        Record record = (Record) result;
        assertEquals("multi", record.getValue());
    }

    @Test
    public void testFindByInstanceId() {
        Map<String, String> lookupMap = new HashMap<>();
        lookupMap.put(ID_LOOKUP_FIELD, INSTANCE_ID.toString());

        Object result = dataProvider.lookup(Record.class.getName(), FIND_BY_ID_LOOKUP, lookupMap);

        assertNotNull(result);
        assertTrue("Wrong type returned", result instanceof Record);
        Record record = (Record) result;
        assertEquals("found by id", record.getValue());
    }

    public static class LookupService extends DefaultMotechDataService<Record> {

        public Record singleLookup(String field) {
            assertEquals(PARAM_VALUE, field);
            Record record = new Record();
            record.setValue("single");
            return record;
        }

        public List<Record> multiLookup(String field) {
            assertEquals(PARAM_VALUE, field);
            Record record = new Record();
            record.setValue("multi");
            return asList(record);
        }


        @Override
        public Record findById(Long id) {
            assertEquals(INSTANCE_ID, id);
            Record record = new Record();
            record.setValue("found by id");
            return record;
        }

        @Override
        public Class<Record> getClassType() {
            return Record.class;
        }
    }
}
