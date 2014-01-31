package org.motechproject.mds.annotations.internal;

import com.thoughtworks.paranamer.Paranamer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.service.EntityService;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LookupProcessorTest {

    @InjectMocks
    LookupProcessor lookupProcessor;

    @Mock
    EntityService entityService;

    @Mock
    Reflections reflections;

    @Mock
    Paranamer paranamer;

    private String[] argNames = {"arg0", "arg1", "arg2"};

    @Before
    public void setUp() throws NoSuchMethodException {
        lookupProcessor = new LookupProcessor();
        initMocks(this);

        when(entityService.getEntityByClassName(String.class.getName())).thenReturn(getTestEntity());
        when(entityService.getEntityByClassName(TestClass.class.getName())).thenReturn(getTestEntity());
        when(entityService.getEntityByClassName(Integer.class.getName())).thenReturn(null);
        when(entityService.getAdvancedSettings(getTestEntity().getId(), true)).thenReturn(getAdvancedSettings());
    }

    @Test
    public void shouldProcessMethodWithLookupFields() throws NoSuchMethodException {
        when(paranamer.lookupParameterNames(getTestMethod(1))).thenReturn(argNames);

        Method method =  getTestMethod(1);
        lookupProcessor.process(method);

        verify(entityService).getEntityByClassName(String.class.getName());
        verify(entityService).getAdvancedSettings(getTestEntity().getId(),true);
        verify(entityService).addLookupToEntity(getTestEntity().getId(), new LookupDto("Test Method 1", true,
                Arrays.asList("arg1", "Second argument")));
    }

    @Test
    public void shouldProcessMethodWithNotAnnotatedParameters() throws NoSuchMethodException {
        when(paranamer.lookupParameterNames(getTestMethod(2))).thenReturn(argNames);

        Method method =  getTestMethod(2);
        lookupProcessor.process(method);

        verify(entityService).getEntityByClassName(TestClass.class.getName());
        verify(entityService).getAdvancedSettings(getTestEntity().getId(),true);
        verify(entityService).addLookupToEntity(getTestEntity().getId(), new LookupDto("Test Method 2", false,
                Arrays.asList(argNames)));
    }

    @Test
    public void shouldProcessMethodWithCustomLookupName() throws NoSuchMethodException {
        when(paranamer.lookupParameterNames(getTestMethod(3))).thenReturn(argNames);

        Method method =  getTestMethod(3);
        lookupProcessor.process(method);

        verify(entityService).getEntityByClassName(TestClass.class.getName());
        verify(entityService).getAdvancedSettings(getTestEntity().getId(),true);
        verify(entityService).addLookupToEntity(getTestEntity().getId(), new LookupDto("My new custom lookup", false,
                Arrays.asList(argNames)));
    }

    @Test
    public void shouldBreakProcessingWhenEntityNotFound() throws NoSuchMethodException {
        when(paranamer.lookupParameterNames(getTestMethod(4))).thenReturn(argNames);

        Method method =  getTestMethod(4);
        lookupProcessor.process(method);

        verify(entityService).getEntityByClassName(Integer.class.getName());
        verify(entityService, never()).getAdvancedSettings(anyLong(), eq(true));
        verify(entityService, never()).addLookupToEntity(anyLong(), any(LookupDto.class));
    }

    @Test
    public void shouldReturnCorrectAnnotation() {
        assertEquals(Lookup.class, lookupProcessor.getAnnotation());
    }

    private Method getTestMethod(int number) throws NoSuchMethodException {
        return TestClass.class.getMethod("testMethod"+number, String.class, int.class, String.class);
    }

    private EntityDto getTestEntity() {
        EntityDto testEntity = new EntityDto();
        testEntity.setId(1L);
        return testEntity;
    }

    private List<LookupDto> getLookupList() {
        LookupDto lookup1 = new LookupDto();
        lookup1.setLookupName("Lookup 1");
        LookupDto lookup2 = new LookupDto();
        lookup2.setLookupName("Lookup 2");
        return Arrays.asList(lookup1, lookup2);
    }

    private AdvancedSettingsDto getAdvancedSettings() {
        AdvancedSettingsDto settings = new AdvancedSettingsDto();
        settings.setIndexes(getLookupList());
        return settings;
    }

    private class TestClass {

        @Lookup
        public String testMethod1(String arg0, @LookupField int arg1, @LookupField(name = "Second argument") String arg2) {
            return "testString";
        }

        @Lookup
        public List<TestClass> testMethod2(String arg0, int arg1, String arg2) {
            return new ArrayList<TestClass>();
        }

        @Lookup(name = "My new custom lookup")
        public List<TestClass> testMethod3(String arg0, int arg1, String arg2) {
            return new ArrayList<TestClass>();
        }

        @Lookup
        public Integer testMethod4(String arg0, int arg1, String arg2) {
            return 42;
        }
    }
}
