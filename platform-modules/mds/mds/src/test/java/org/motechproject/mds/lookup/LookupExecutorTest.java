package org.motechproject.mds.lookup;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.dto.DtoHelper;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.DefaultMotechDataService;
import org.motechproject.mds.testutil.FieldTestHelper;
import org.motechproject.mds.util.LookupName;
import org.motechproject.mds.util.Order;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LookupExecutorTest {

    private static final String LOOKUP_NAME = "MY lookup";
    private static final String LOOKUP_METHOD_NAME_1 = "find";
    private static final String LOOKUP_METHOD_NAME_2 = "findByRelationFields";
    private static final String STR_FIELD_NAME = "strField";
    private static final String TEXTAREA_FIELD_NAME = "textAreaField";
    private static final String INT_FIELD_NAME = "intField";
    private static final String RELATED_FIELD_NAME = "relatedField";
    private static final String ID = "id";
    private static final String NAME = "name";

    private static final String STR_ARG = "expectedStrArg";
    private static final String STR_ARG_2 = "expectedStrArg_2";
    private static final String TEXTAREA_ARG = "expectedTextAreaArg";
    private static final int INT_ARG = 27;
    private static final long LONG_ARG = 6l;
    public static final long COUNT = 2;

    private static final int PAGE = 3;
    private static final int PAGE_SIZE = 15;
    private static final String SORT_FIELD = INT_FIELD_NAME;
    private static final Order.Direction DIRECTION = Order.Direction.ASC;

    private TestLookupService dataService = new TestLookupService();

    private LookupExecutor lookupExecutor1;

    private LookupExecutor lookupExecutor2;

    @Before
    public void setUp() {
        LookupDto lookupDto = new LookupDto(LOOKUP_NAME, false, false,
                asList(new LookupFieldDto(1L, STR_FIELD_NAME, LookupFieldType.VALUE),
                        new LookupFieldDto(2L, INT_FIELD_NAME, LookupFieldType.VALUE),
                        new LookupFieldDto(3L, TEXTAREA_FIELD_NAME, LookupFieldType.VALUE)),
                false, LOOKUP_METHOD_NAME_1, asList(STR_FIELD_NAME, INT_FIELD_NAME, TEXTAREA_FIELD_NAME));

        List<FieldDto> fields = Arrays.asList(
                FieldTestHelper.fieldDto(1L, STR_FIELD_NAME, String.class.getName(), "strFieldDisp", null),
                FieldTestHelper.fieldDto(2L, INT_FIELD_NAME, Integer.class.getName(), "intFieldDisp", null),
                FieldTestHelper.fieldDto(3L, TEXTAREA_FIELD_NAME, "mds.field.textArea", "textAreaFieldDisp", null)
        );

        lookupExecutor1 = new LookupExecutor(dataService, lookupDto, DtoHelper.asFieldMapByName(fields));

        lookupDto = new LookupDto(LOOKUP_NAME, false, false,
                asList(new LookupFieldDto(1l, STR_FIELD_NAME, LookupFieldType.VALUE, null, false, null),
                        new LookupFieldDto(3l, RELATED_FIELD_NAME, LookupFieldType.VALUE, null, false, "id"),
                        new LookupFieldDto(2l, INT_FIELD_NAME, LookupFieldType.VALUE, null, false, null),
                        new LookupFieldDto(4l, RELATED_FIELD_NAME, LookupFieldType.VALUE, null, false, "name")),
                false, LOOKUP_METHOD_NAME_2, asList(STR_FIELD_NAME, LookupName.buildLookupFieldName(RELATED_FIELD_NAME, ID),
                INT_FIELD_NAME, LookupName.buildLookupFieldName(RELATED_FIELD_NAME, NAME)));
        lookupExecutor2 = new LookupExecutor(dataService, lookupDto, getFieldMapping());
    }

    @Test
    public void shouldExecuteALookupWithoutQueryParams() {
        Map<String, Object> lookupMap = new HashMap<>();
        lookupMap.put(STR_FIELD_NAME, STR_ARG);
        lookupMap.put(INT_FIELD_NAME, INT_ARG);
        lookupMap.put(TEXTAREA_FIELD_NAME, TEXTAREA_ARG);

        List result = (List) lookupExecutor1.execute(lookupMap);

        assertEquals(dataService.find(STR_ARG, INT_ARG, TEXTAREA_ARG), result);
    }

    @Test
    public void shouldExecuteALookupWithQueryParams() {
        Map<String, Object> lookupMap = new HashMap<>();
        lookupMap.put(STR_FIELD_NAME, STR_ARG);
        lookupMap.put(INT_FIELD_NAME, INT_ARG);
        lookupMap.put(TEXTAREA_FIELD_NAME, TEXTAREA_ARG);
        QueryParams queryParams = new QueryParams(PAGE, PAGE_SIZE, new Order(SORT_FIELD, DIRECTION));

        List result = (List) lookupExecutor1.execute(lookupMap, queryParams);

        assertEquals(dataService.find(STR_ARG, INT_ARG, TEXTAREA_ARG, queryParams), result);
    }

    @Test
    public void shouldExecuteCountLookup() {
        Map<String, Object> lookupMap = new HashMap<>();
        lookupMap.put(STR_FIELD_NAME, STR_ARG);
        lookupMap.put(INT_FIELD_NAME, INT_ARG);
        lookupMap.put(TEXTAREA_FIELD_NAME, TEXTAREA_ARG);

        long result = lookupExecutor1.executeCount(lookupMap);

        assertEquals(COUNT, result);
    }

    @Test
    public void shouldExecuteLookupWithRelatedFields() {
        Map<String, Object> lookupMap = new HashMap<>();
        lookupMap.put(STR_FIELD_NAME, STR_ARG);
        lookupMap.put(LookupName.buildLookupFieldName(RELATED_FIELD_NAME, ID), LONG_ARG);
        lookupMap.put(INT_FIELD_NAME, INT_ARG);
        lookupMap.put(LookupName.buildLookupFieldName(RELATED_FIELD_NAME, NAME), STR_ARG_2);

        List result = (List) lookupExecutor2.execute(lookupMap);

        assertEquals(dataService.findByRelationFields(STR_ARG, LONG_ARG, INT_ARG, STR_ARG_2), result);
    }

    private Map<String, FieldDto> getFieldMapping() {
        Map<String, FieldDto> mapping = new HashMap<>();
        mapping.put(STR_FIELD_NAME, FieldTestHelper.fieldDto(1L, STR_FIELD_NAME, String.class.getName(), "strFieldDisp", null));
        mapping.put(LookupName.buildLookupFieldName(RELATED_FIELD_NAME, ID), FieldTestHelper.fieldDto(3L, ID, Long.class.getName(), "Id", null));
        mapping.put(INT_FIELD_NAME, FieldTestHelper.fieldDto(2L, INT_FIELD_NAME, Integer.class.getName(), "strFieldDisp", null));
        mapping.put(LookupName.buildLookupFieldName(RELATED_FIELD_NAME, NAME), FieldTestHelper.fieldDto(4L, NAME, String.class.getName(), "Name", null));
        return mapping;
    }

    private class TestClass {
        public int intField;
        public String strField;
        public String textAreaField;

        private TestClass(int intField, String strField, String textAreaField) {
            this.intField = intField;
            this.strField = strField;
            this.textAreaField = textAreaField;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (!(o instanceof TestClass)) {
                return false;
            }

            TestClass other = (TestClass) o;

            return Objects.equals(intField, other.intField) && Objects.equals(strField, other.strField);

        }

        @Override
        public int hashCode() {
            return Objects.hash(intField, strField);
        }
    }

    public class TestLookupService extends DefaultMotechDataService<TestClass> {

        public List<TestClass> find(String strField, Integer intField, String textAreaField) {
            assertParams(strField, intField, textAreaField);
            return asList(new TestClass(1, "firstRecord", "textArea"), new TestClass(2, "secondRecord", "textArea"));
        }

        public List<TestClass> find(String strField, Integer intField, String textAreaField, QueryParams queryParams) {
            assertParams(strField, intField, textAreaField);
            assertQueryParams(queryParams);
            return asList(new TestClass(1, "firstRecord", "textArea"));
        }

        public long countFind(String strField, Integer intField, String textAreaField) {
            assertParams(strField, intField, textAreaField);
            return COUNT;
        }

        private void assertParams(String strParam, int intParam, String textAreaParam) {
            assertEquals(STR_ARG, strParam);
            assertEquals(INT_ARG, intParam);
            assertEquals(TEXTAREA_ARG, textAreaParam);
        }

        private void assertQueryParams(QueryParams queryParams) {
            assertNotNull(queryParams);
            assertEquals(Integer.valueOf(PAGE), queryParams.getPage());
            assertEquals(Integer.valueOf(PAGE_SIZE), queryParams.getPageSize());
            assertNotNull(queryParams.getOrderList());
            assertEquals(1, queryParams.getOrderList().size());
            assertEquals(SORT_FIELD, queryParams.getOrderList().get(0).getField());
            assertEquals(DIRECTION, queryParams.getOrderList().get(0).getDirection());
        }

        public List<TestClass> findByRelationFields(String strParam, long longParam, int intParam , String strParam2) {
            assertEquals(STR_ARG, strParam);
            assertEquals(LONG_ARG, longParam);
            assertEquals(INT_ARG, intParam);
            assertEquals(STR_ARG_2, strParam2);
            return asList(new TestClass(2, "second", "textArea"));
        }

        @Override
        public Class<TestClass> getClassType() {
            return TestClass.class;
        }
    }
}
