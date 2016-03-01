package org.motechproject.mds.annotations.internal;

import com.thoughtworks.paranamer.Paranamer;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.annotations.RestExposed;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.exception.lookup.LookupWrongParameterTypeException;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mds.dto.LookupFieldType.RANGE;
import static org.motechproject.mds.dto.LookupFieldType.SET;
import static org.motechproject.mds.dto.LookupFieldType.VALUE;
import static org.motechproject.mds.testutil.FieldTestHelper.lookupFieldDto;
import static org.motechproject.mds.testutil.FieldTestHelper.lookupFieldDtos;

public class LookupProcessorTest {

    @Mock
    private Reflections reflections;

    @Mock
    private Paranamer paranamer;

    @Mock
    private SchemaHolder schemaHolder;

    @InjectMocks
    private LookupProcessor lookupProcessor;

    private String[] argNames = {"arg0", "arg1", "arg2"};

    private static final String TEST_CLASS_NAME = TestClass.class.getName();

    @Before
    public void setUp() throws NoSuchMethodException {
        lookupProcessor = new LookupProcessor();
        lookupProcessor.setSchemaHolder(schemaHolder);
        initMocks(this);
    }

    private EntityProcessorOutput mockEntityProcessorOutput(EntityDto entity, List<FieldDto> fields) {
        EntityProcessorOutput output = new EntityProcessorOutput();
        output.setEntityProcessingResult(entity);
        output.setFieldProcessingResult(fields);
        return output;
    }

    @Test
    public void shouldProcessMethodWithLookupFields() throws NoSuchMethodException {
        FieldDto arg1Field = new FieldDto("arg1", "Arg1", TypeDto.INTEGER);
        FieldDto secondArgumentField = new FieldDto("secondArgument", "Second Argument", TypeDto.STRING);

        lookupProcessor.setEntityProcessingResult
                (Arrays.asList(mockEntityProcessorOutput(new EntityDto(TestClass.class.getName()),
                        Arrays.asList(arg1Field, secondArgumentField))));
        when(paranamer.lookupParameterNames(getTestMethod(1))).thenReturn(argNames);

        Method method = getTestMethod(1);
        lookupProcessor.process(method);

        Map<String, List<LookupDto>> elements = lookupProcessor.getProcessingResult();
        assertTrue(elements.containsKey(TEST_CLASS_NAME));

        List<LookupDto> list = elements.get(TEST_CLASS_NAME);
        LookupDto expected = new LookupDto("Test Method 1", true, false,
                asList(lookupFieldDto("arg1"), lookupFieldDto("secondArgument", "LIKE")), true, "testMethod1", asList("arg1", "secondArgument"), true);

        assertEquals(1, list.size());
        assertEquals(expected, list.get(0));
    }

    @Test (expected = LookupWrongParameterTypeException.class)
    public void shouldNotProcessMethodWithLookupFieldsWithWrongType() throws NoSuchMethodException {
        FieldDto arg1Field = new FieldDto("arg1", "Arg1", TypeDto.STRING);
        FieldDto secondArgumentField = new FieldDto("secondArgument", "Second Argument", TypeDto.STRING);

        lookupProcessor.setEntityProcessingResult
                (Arrays.asList(mockEntityProcessorOutput(new EntityDto(TestClass.class.getName()),
                        Arrays.asList(arg1Field, secondArgumentField))));
        when(paranamer.lookupParameterNames(getTestMethod(1))).thenReturn(argNames);

        Method method = getTestMethod(1);

        lookupProcessor.process(method);
    }

    @Test
    public void shouldProcessMethodWithNotAnnotatedParameters() throws NoSuchMethodException {
        FieldDto arg1Field = new FieldDto("arg1", "Arg1", TypeDto.INTEGER);
        FieldDto secondArgumentField = new FieldDto("secondArgument", "Second Argument", TypeDto.STRING);

        lookupProcessor.setEntityProcessingResult
                (Arrays.asList(mockEntityProcessorOutput(new EntityDto(TestClass.class.getName()),
                        Arrays.asList(arg1Field, secondArgumentField))));

        when(paranamer.lookupParameterNames(getTestMethod(2))).thenReturn(argNames);

        Method method = getTestMethod(2);

        lookupProcessor.process(method);

        Map<String, List<LookupDto>> elements = lookupProcessor.getElements();
        assertTrue(elements.containsKey(TEST_CLASS_NAME));

        List<LookupDto> list = elements.get(TEST_CLASS_NAME);
        LookupDto expected = new LookupDto("Test Method 2", false, false,
                lookupFieldDtos(argNames), true, "testMethod2", asList(argNames), true);

        assertEquals(1, list.size());
        assertEquals(expected, list.get(0));
    }

    @Test
    public void shouldProcessMethodWithCustomLookupName() throws NoSuchMethodException {
        lookupProcessor.setEntityProcessingResult
                (Arrays.asList(mockEntityProcessorOutput(new EntityDto(TestClass.class.getName()), Collections.EMPTY_LIST)));

        when(paranamer.lookupParameterNames(getTestMethod(3))).thenReturn(argNames);

        Method method = getTestMethod(3);
        LookupDto dto = new LookupDto("My new custom lookup", false, false,
                lookupFieldDtos(argNames), true, "testMethod3",  asList(argNames), true);

        lookupProcessor.process(method);

        Map<String, List<LookupDto>> elements = lookupProcessor.getProcessingResult();
        assertTrue(elements.containsKey(TEST_CLASS_NAME));

        List<LookupDto> list = elements.get(TEST_CLASS_NAME);
        assertEquals(1, list.size());
        assertEquals(dto, list.get(0));
    }

    @Test
    public void shouldProcessMethodWithRangeParam() throws NoSuchMethodException {
        FieldDto arg0Field = new FieldDto("arg0Field", "Arg 0 Field", TypeDto.BOOLEAN);
        FieldDto rangeField = new FieldDto("rangeField", "Range Field", TypeDto.STRING);
        FieldDto regularFieldField = new FieldDto("regularField", "Regular Field", TypeDto.BOOLEAN);
        FieldDto rangeFieldField = new FieldDto("rangeFieldDouble", "Range Field Double", TypeDto.DOUBLE);

        EntityProcessorOutput eop = mockEntityProcessorOutput(new EntityDto(TestClass.class.getName()),
                Arrays.asList(arg0Field, rangeField, regularFieldField, rangeFieldField));
        lookupProcessor.setEntityProcessingResult(Arrays.asList(eop));

        LookupFieldDto[][] expectedFields = {{lookupFieldDto("arg0"), lookupFieldDto("range", RANGE)},
                {lookupFieldDto("regularField"), lookupFieldDto("rangeField", RANGE)}};
        String [][] expectedFieldsOrder = {{"arg0", "range"}, {"regularField", "rangeField"}};
        // test two methods, one with @LookupField annotations, second without
        for (int i = 0; i < 2; i++) {
            Method method = getTestMethodWithRangeParam(i);

            when(paranamer.lookupParameterNames(method)).thenReturn(new String[]{"arg0", "range"});

            LookupDto expectedLookup = new LookupDto("Test Method With Range Param " + i, false, false,
                    asList(expectedFields[i]), true, "testMethodWithRangeParam" + i,  asList(expectedFieldsOrder[i]), true);

            lookupProcessor.process(method);

            Map<String, List<LookupDto>> elements = lookupProcessor.getProcessingResult();
            assertTrue(elements.containsKey(TEST_CLASS_NAME));

            List<LookupDto> list = elements.get(TEST_CLASS_NAME);
            assertEquals(1, list.size());
            assertEquals(expectedLookup, list.get(0));

            assertEquals(asList(VALUE, RANGE), extract(list.get(0).getLookupFields(), on(LookupFieldDto.class).getType()));

            lookupProcessor.clear();
        }
    }

    @Test
    public void shouldProcessMethodWithSetParam() throws NoSuchMethodException {
        FieldDto arg0Field = new FieldDto("arg0Field", "Arg 0 Field", TypeDto.STRING);
        FieldDto setField = new FieldDto("setField", "Range Field", TypeDto.STRING);
        FieldDto regularField = new FieldDto("regularField", "Regular Field", TypeDto.STRING);
        FieldDto setFieldDouble = new FieldDto("setFieldDouble", "Set Field", TypeDto.DOUBLE);

        EntityProcessorOutput eop = mockEntityProcessorOutput(new EntityDto(TestClass.class.getName()),
                Arrays.asList(arg0Field, setField, regularField, setFieldDouble));
        lookupProcessor.setEntityProcessingResult(Arrays.asList(eop));

        LookupFieldDto[][] expectedFields = {{lookupFieldDto("arg0"), lookupFieldDto("set", SET)},
                {lookupFieldDto("regularField"), lookupFieldDto("setField", SET)}};
        String [][] expectedFieldsOrder = {{"arg0", "range"}, {"regularField", "rangeField"}};

        // test two methods, one with @LookupField annotations, second without
        for (int i = 0; i < 2; i++) {
            Method method = getTestMethodWithSetParam(i);

            when(paranamer.lookupParameterNames(method)).thenReturn(new String[]{"arg0", "set"});

            LookupDto expectedLookup = new LookupDto("Test Method With Set Param " + i, true, false,
                    asList(expectedFields[i]), true, "testMethodWithSetParam" + i, asList(expectedFieldsOrder[i]), true);

            lookupProcessor.process(method);

            Map<String, List<LookupDto>> elements = lookupProcessor.getProcessingResult();
            assertTrue(elements.containsKey(TEST_CLASS_NAME));

            List<LookupDto> list = elements.get(TEST_CLASS_NAME);
            assertEquals(1, list.size());
            assertEquals(expectedLookup, list.get(0));

            assertEquals(asList(VALUE, SET), extract(list.get(0).getLookupFields(), on(LookupFieldDto.class).getType()));

            lookupProcessor.clear();
        }
    }

    @Test
    public void shouldBreakProcessingWhenEntityNotFound() throws NoSuchMethodException {
        when(paranamer.lookupParameterNames(getTestMethod(4))).thenReturn(argNames);

        EntityProcessorOutput eop = mockEntityProcessorOutput(new EntityDto(TestClass.class.getName()),
                Arrays.asList(new FieldDto("aaa", "bbb", TypeDto.STRING)));
        lookupProcessor.setEntityProcessingResult(Arrays.asList(eop));

        Method method = getTestMethod(4);
        lookupProcessor.process(method);
        assertTrue(lookupProcessor.getProcessingResult().isEmpty());
    }

    @Test
    public void shouldReturnCorrectAnnotation() {
        assertEquals(Lookup.class, lookupProcessor.getAnnotationType());
    }

    @Test
    public void shouldProcessMethodWithRestExposedAnnotation() throws Exception {
        when(paranamer.lookupParameterNames(getTestMethodExposedViaRest())).thenReturn(argNames);

        EntityProcessorOutput eop = mockEntityProcessorOutput(new EntityDto(TestClass.class.getName()),
                Arrays.asList(new FieldDto("aaa", "bbb", TypeDto.STRING)));
        lookupProcessor.setEntityProcessingResult(Arrays.asList(eop));

        Method method = getTestMethodExposedViaRest();
        LookupDto dto = new LookupDto("Test Method Exposed Via Rest", true, true,
                lookupFieldDtos(argNames), true, "testMethodExposedViaRest", asList(argNames), true);

        lookupProcessor.process(method);

        Map<String, List<LookupDto>> elements = lookupProcessor.getProcessingResult();
        assertTrue(elements.containsKey(TEST_CLASS_NAME));

        List<LookupDto> list = elements.get(TEST_CLASS_NAME);
        assertEquals(1, list.size());
        assertEquals(dto, list.get(0));
    }

    @Test
    public void shouldNotUpdateRestExposedValueForLookupsThatHaveThatModifiedByUser() throws Exception {
        when(paranamer.lookupParameterNames(getTestMethodExposedViaRest())).thenReturn(argNames);

        AdvancedSettingsDto advanced = mock(AdvancedSettingsDto.class);
        RestOptionsDto restOptions = mock(RestOptionsDto.class);
        when(schemaHolder.getAdvancedSettings(TEST_CLASS_NAME)).thenReturn(advanced);
        when(advanced.getRestOptions()).thenReturn(restOptions);
        when(restOptions.isModifiedByUser()).thenReturn(true);

        EntityProcessorOutput eop = mockEntityProcessorOutput(new EntityDto(TestClass.class.getName()),
                Arrays.asList(new FieldDto("aaa", "bbb", TypeDto.STRING)));
        lookupProcessor.setEntityProcessingResult(Arrays.asList(eop));

        Method method = getTestMethodExposedViaRest();
        LookupDto dto = new LookupDto("Test Method Exposed Via Rest", true, false,
                lookupFieldDtos(argNames), true, "testMethodExposedViaRest", asList(argNames), true);

        lookupProcessor.process(method);

        Map<String, List<LookupDto>> elements = lookupProcessor.getProcessingResult();
        assertTrue(elements.containsKey(TEST_CLASS_NAME));

        List<LookupDto> list = elements.get(TEST_CLASS_NAME);
        assertEquals(1, list.size());
        assertEquals(dto, list.get(0));
    }

    @Test
    public void shouldNotCreateIndexForLookup() throws Exception {
        FieldDto arg1Field = new FieldDto("arg1", "Arg1", TypeDto.INTEGER);
        FieldDto secondArgumentField = new FieldDto("secondArgument", "Second Argument", TypeDto.STRING);

        lookupProcessor.setEntityProcessingResult
                (Arrays.asList(mockEntityProcessorOutput(new EntityDto(TestClass.class.getName()),
                        Arrays.asList(arg1Field, secondArgumentField))));
        when(paranamer.lookupParameterNames(getTestMethod(5))).thenReturn(argNames);

        Method method = getTestMethod(5);
        lookupProcessor.process(method);

        Map<String, List<LookupDto>> elements = lookupProcessor.getProcessingResult();
        assertTrue(elements.containsKey(TEST_CLASS_NAME));

        List<LookupDto> list = elements.get(TEST_CLASS_NAME);
        LookupDto expected = new LookupDto("Test Method 5", true, false,
                asList(lookupFieldDto("arg1"), lookupFieldDto("secondArgument", "LIKE")), true, "testMethod5", new ArrayList<>(), false);

        assertEquals(1, list.size());
        assertEquals(expected, list.get(0));
    }

    private Method getTestMethod(int number) throws NoSuchMethodException {
        return TestClass.class.getMethod("testMethod" + number, String.class, Integer.class, String.class);
    }

    private Method getTestMethodWithRangeParam(int number) throws NoSuchMethodException {
        return TestClass.class.getMethod("testMethodWithRangeParam" + number, Boolean.class, Range.class);
    }

    private Method getTestMethodWithSetParam(int number) throws NoSuchMethodException {
        return TestClass.class.getMethod("testMethodWithSetParam" + number, String.class, Set.class);
    }

    private Method getTestMethodExposedViaRest() throws NoSuchMethodException {
        return TestClass.class.getMethod("testMethodExposedViaRest", String.class, Integer.class, String.class);
    }

    private class TestClass {

        @Lookup
        public TestClass testMethod1(String arg0, @LookupField Integer arg1,
                                  @LookupField(name = "secondArgument", customOperator = "LIKE") String arg2) {
            return null;
        }

        @Lookup
        public List<TestClass> testMethod2(String arg0, Integer arg1, String arg2) {
            return new ArrayList<>();
        }

        @Lookup(name = "My new custom lookup")
        public List<TestClass> testMethod3(String arg0, Integer arg1, String arg2) {
            return new ArrayList<>();
        }

        @Lookup
        public Integer testMethod4(String arg0, Integer arg1, String arg2) {
            return 42;
        }

        @Lookup(indexRequired = false)
        public TestClass testMethod5(String arg0, @LookupField Integer arg1,
                                     @LookupField(name = "secondArgument", customOperator = "LIKE") String arg2) {
            return null;
        }

        @Lookup
        public List<TestClass> testMethodWithRangeParam0(Boolean arg0, Range<DateTime> range) {
            return Collections.emptyList();
        }

        @Lookup
        public List<TestClass> testMethodWithRangeParam1(@LookupField(name = "regularField") Boolean arg0,
                                                         @LookupField(name = "rangeField") Range<DateTime> range) {
            return Collections.emptyList();
        }

        @Lookup
        public TestClass testMethodWithSetParam0(String arg0, Set<Time> set) {
            return null;
        }

        @Lookup
        public TestClass testMethodWithSetParam1(@LookupField(name = "regularField") String arg0,
                                                 @LookupField(name = "setField") Set<Time> range) {
            return null;
        }

        @Lookup
        @RestExposed
        public TestClass testMethodExposedViaRest(String arg0, Integer arg1, String arg2) {
            return null;
        }
    }
}
