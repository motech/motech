package org.motechproject.mds.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.exception.action.ActionHandlerException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.internal.AllEntities;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.testutil.FieldTestHelper;
import org.motechproject.mds.util.Constants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActionHandlerServiceImplTest {

    private static final String ENTITY_KEY = "@ENTITY";
    private static final String ENTITY_ID_KEY = Constants.Util.ID_FIELD_NAME;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private AllEntities allEntities;

    @Mock
    private ServiceReference testEntityDataServiceReference;

    @Mock
    private MotechDataService<TestEntity> testEntityDataService;

    @Mock
    private Entity testEntity;

    private ActionHandlerServiceImpl actionHandlerService;

    @Captor
    private ArgumentCaptor<List<TestEntity>> testEntityListCaptor;

    @Before
    public void setUp() throws Exception {
        when(bundleContext.getServiceReference(MotechClassPool.getInterfaceName(TestEntity.class.getName())))
                .thenReturn(testEntityDataServiceReference);
        when(bundleContext.getService(eq(testEntityDataServiceReference)))
                .thenReturn(testEntityDataService);
        when(testEntityDataService.getClassType())
                .thenReturn(TestEntity.class);
        when(allEntities.retrieveByClassName(TestEntity.class.getName()))
                .thenReturn(testEntity);
        when(testEntity.getFields()).thenReturn(asList(
                FieldTestHelper.field(TestEntity.PROPERTY_STRING, String.class),
                FieldTestHelper.field(TestEntity.PROPERTY_INTEGER, Integer.class),
                FieldTestHelper.field(TestEntity.PROPERTY_DATE, Date.class),
                FieldTestHelper.field(TestEntity.PROPERTY_LOCALE, Locale.class),
                FieldTestHelper.field(TestEntity.PROPERTY_LIST, List.class),
                FieldTestHelper.field(TestEntity.PROPERTY_MAP, Map.class)
        ));

        actionHandlerService = new ActionHandlerServiceImpl();
        actionHandlerService.setBundleContext(bundleContext);
        actionHandlerService.setAllEntities(allEntities);
    }

    @Test
    public void shouldCreateEntity() throws ActionHandlerException {
        ArgumentCaptor<TestEntity> testEntityCaptor = ArgumentCaptor.forClass(TestEntity.class);
        String string = "hello";
        Integer integer = 42;
        Date date = new Date(2014, 12, 1);
        Locale locale = Locale.US;
        List list = asList("pig", "cat");
        Map map = new HashMap(); map.put("dog", "good"); map.put("cat", "bad");
        Map<String, Object> parameters = createTestEntityParameters(string, integer, date, locale, list, map);

        actionHandlerService.create(parameters);

        verify(testEntityDataService).create(testEntityCaptor.capture());
        TestEntity expectedTestEntity = new TestEntity(string, integer, date, locale, list, map);

        assertEquals(expectedTestEntity, testEntityCaptor.getValue());
    }

    @Test
    public void shouldUpdateEntity() throws ActionHandlerException {
        Map<String, Object> parameters = updateTestParams();
        TestEntity testEntity = preUpdateTestEntity();
        testEntity.setId(1L);

        when(testEntityDataService.findById(eq(1L))).thenReturn(testEntity);

        actionHandlerService.update(parameters);
        TestEntity expectedTestEntity = expectedPostUpdateTestEntity();
        expectedTestEntity.setId(1L);

        ArgumentCaptor<TestEntity> testEntityCaptor = ArgumentCaptor.forClass(TestEntity.class);
        verify(testEntityDataService).update(testEntityCaptor.capture());
        assertEquals(expectedTestEntity, testEntityCaptor.getValue());
    }

    @Test
    public void shouldQueryAndUpdateEntity() throws ActionHandlerException {
        Map<String, Object> parameters = updateTestParams();
        Map map = new HashMap();
        map.put("dog", "good");
        map.put("cat", "bad");

        List<TestEntity> preTestEntities = prepareListTestEntities(null);
        List<TestEntity> expectedTestEntities = prepareListTestEntities(map);

        when(testEntityDataService.retrieveAll()).thenReturn(preTestEntities);

        actionHandlerService.queryAndUpdate(parameters);

        ArgumentCaptor<TestEntity> testEntitiesCaptor = ArgumentCaptor.forClass(TestEntity.class);
        verify(testEntityDataService, times(3)).update(testEntitiesCaptor.capture());

        List<TestEntity> capturedTestEntities = testEntitiesCaptor.getAllValues();

        assertEquals(expectedTestEntities.get(0), capturedTestEntities.get(0));
        assertEquals(expectedTestEntities.get(1), capturedTestEntities.get(1));
        assertEquals(expectedTestEntities.get(2), capturedTestEntities.get(2));
    }

    @Test
    public void shouldUpsertEntity() throws ActionHandlerException {
        Map<String, Object> parameters = updateTestParams();

        actionHandlerService.createOrUpdate(parameters);
        TestEntity expectedTestEntity = expectedPostUpdateTestEntity();
        expectedTestEntity.setId(1L);

        ArgumentCaptor<TestEntity> testEntityCaptor = ArgumentCaptor.forClass(TestEntity.class);
        verify(testEntityDataService).createOrUpdate(testEntityCaptor.capture());
        assertEquals(expectedTestEntity, testEntityCaptor.getValue());
    }

    private List<TestEntity> prepareListTestEntities(Map testMap) {
        List<TestEntity> result = new ArrayList<>();
        result.add(new TestEntity(1L, "hello", 42, new Date(2014, 12, 1), Locale.US, asList("pig", "cat"), testMap));
        result.add(new TestEntity(2L, "hello", 42, new Date(2014, 12, 1), Locale.US, asList("pig", "cat"), testMap));
        result.add(new TestEntity(3L, "hello", 42, new Date(2014, 12, 1), Locale.US, asList("pig", "cat"), testMap));

        return result;
    }

    private Map<String, Object> updateTestParams() {
        String string = "hello";
        Integer integer = 42;
        Date date = new Date(2014, 12, 1);
        Locale locale = Locale.US;
        List list = asList("pig", "cat");
        Map map = new HashMap();
        map.put("dog", "good");
        map.put("cat", "bad");

        Map<String, Object> parameters = createTestEntityParameters(string, integer, date, locale, list, map);
        parameters.put(ENTITY_ID_KEY, 1L);
        return parameters;
    }

    private TestEntity preUpdateTestEntity() {
        return new TestEntity("before", 42, new Date(2014, 12, 1), null, asList("pig", "cat"), null);
    }

    private TestEntity expectedPostUpdateTestEntity() {
        Map map = new HashMap();
        map.put("dog", "good");
        map.put("cat", "bad");
        return new TestEntity("hello", 42, new Date(2014, 12, 1), Locale.US, asList("pig", "cat"), map);
    }

    private static Map<String, Object> createTestEntityParameters(String string, Integer integer, Date date, Locale locale, List list, Map map) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ENTITY_KEY, TestEntity.class.getName());
        parameters.put(TestEntity.PROPERTY_STRING, string);
        parameters.put(TestEntity.PROPERTY_INTEGER, integer);
        parameters.put(TestEntity.PROPERTY_DATE, date);
        parameters.put(TestEntity.PROPERTY_LOCALE, locale.toString());
        parameters.put(TestEntity.PROPERTY_LIST, list);
        parameters.put(TestEntity.PROPERTY_MAP, map);
        return parameters;
    }

    public static class TestEntity {

        public static final String PROPERTY_STRING = "string";
        public static final String PROPERTY_INTEGER = "integer";
        public static final String PROPERTY_DATE = "date";
        public static final String PROPERTY_LOCALE = "locale";
        public static final String PROPERTY_LIST = "list";
        public static final String PROPERTY_MAP = "map";

        private String string;
        private Integer integer;
        private Date date;
        private Locale locale;
        private List list;
        private Map map;
        private Long id;

        public TestEntity() {
        }

        public TestEntity(String string, Integer integer, Date date, Locale locale, List list, Map map) {
            this(null, string, integer, date, locale, list, map);
        }

        public TestEntity(Long id, String string, Integer integer, Date date, Locale locale, List list, Map map) {
            this.id = id;
            this.string = string;
            this.integer = integer;
            this.date = date;
            this.locale = locale;
            this.list = list;
            this.map = map;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public Integer getInteger() {
            return integer;
        }

        public void setInteger(Integer integer) {
            this.integer = integer;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Locale getLocale() {
            return locale;
        }

        public void setLocale(Locale locale) {
            this.locale = locale;
        }

        public List getList() {
            return list;
        }

        public void setList(List list) {
            this.list = list;
        }

        public Map getMap() {
            return map;
        }

        public void setMap(Map map) {
            this.map = map;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestEntity that = (TestEntity) o;

            if (date != null ? !date.equals(that.date) : that.date != null) return false;
            if (integer != null ? !integer.equals(that.integer) : that.integer != null) return false;
            if (list != null ? !list.equals(that.list) : that.list != null) return false;
            if (locale != null ? !locale.equals(that.locale) : that.locale != null) return false;
            if (map != null ? !map.equals(that.map) : that.map != null) return false;
            if (string != null ? !string.equals(that.string) : that.string != null) return false;
            if (id != null ? !id.equals(that.id) : that.id!= null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = string != null ? string.hashCode() : 0;
            result = 31 * result + (integer != null ? integer.hashCode() : 0);
            result = 31 * result + (date != null ? date.hashCode() : 0);
            result = 31 * result + (locale != null ? locale.hashCode() : 0);
            result = 31 * result + (list != null ? list.hashCode() : 0);
            result = 31 * result + (map != null ? map.hashCode() : 0);
            result = 31 * result + (id != null ? id.hashCode() : 0);
            return result;
        }
    }
}