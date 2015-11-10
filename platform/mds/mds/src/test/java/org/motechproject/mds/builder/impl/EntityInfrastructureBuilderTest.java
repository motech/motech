package org.motechproject.mds.builder.impl;

import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.builder.EntityInfrastructureBuilder;
import org.motechproject.mds.builder.Sample;
import org.motechproject.mds.builder.SampleWithLookups;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.MDSClassLoader;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.motechproject.mds.testutil.FieldTestHelper.fieldDto;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MDSClassLoader.class)
public class EntityInfrastructureBuilderTest {
    private static final String SAMPLE_REPOSITORY = "org.motechproject.mds.builder.mdsrepositoryimpl.AllSamples";
    private static final String SAMPLE_INTERFACE = "org.motechproject.mds.builder.mdsservice.SampleService";
    private static final String SAMPLE_SERVICE = "org.motechproject.mds.builder.mdsserviceimpl.SampleServiceImpl";

    private static final String SAMPLE_WITH_LOOKUPS_SERVICE = "org.motechproject.mds.builder.mdsserviceimpl.SampleWithLookupsServiceImpl";
    private static final String SAMPLE_WITH_LOOKUPS_INTERFACE = "org.motechproject.mds.builder.mdsservice.SampleWithLookupsService";
    private static final String SAMPLE_WITH_LOOKUPS_REPOSITORY = ClassName.getRepositoryName(SampleWithLookups.class.getName());

    @Mock
    private MDSClassLoader classLoader;

    @Mock
    private SchemaHolder schemaHolder;

    private EntityInfrastructureBuilder entityInfrastructureBuilder = new EntityInfrastructureBuilderImpl();

    @Before
    public void setUp() throws Exception {
        PowerMockito.spy(MDSClassLoader.class);
        when(MDSClassLoader.getInstance()).thenReturn(classLoader);
    }

    @Test
    public void shouldCreateCodeIfClassNotExistsInClassPath() throws Exception {
        doReturn(null).when(classLoader).loadClass(SAMPLE_SERVICE);

        EntityDto entity = new EntityDto(Sample.class.getName());
        List<ClassData> data = entityInfrastructureBuilder.buildInfrastructure(entity, schemaHolder);

        assertNotNull(data);
        assertFalse(data.isEmpty());
        assertThat(data, hasItem(Matchers.<ClassData>hasProperty("className", equalTo(SAMPLE_REPOSITORY))));
        assertThat(data, hasItem(Matchers.<ClassData>hasProperty("className", equalTo(SAMPLE_INTERFACE))));
        assertThat(data, hasItem(Matchers.<ClassData>hasProperty("className", equalTo(SAMPLE_SERVICE))));
        assertThat(data, hasItem(Matchers.<ClassData>hasProperty("interfaceClass", equalTo(true))));
    }

    @Test
    public void shouldCreateCodeForClassWithLookups() throws Exception {
        MDSClassLoader mdsClassLoaderImpl = MDSClassLoader.getStandaloneInstance(getClass().getClassLoader());

        EntityDto entity = new EntityDto(SampleWithLookups.class.getName());
        entity.setMaxFetchDepth(-1);

        LookupDto lookup = new LookupDto();
        lookup.setLookupName("testLookup");
        lookup.setMethodName("testLookupMethod");

        FieldDto testField = fieldDto("TestField", "testDispName", String.class);
        FieldDto testField2 = fieldDto("TestField2", "DisplayName with space", String.class);
        FieldDto dateField = fieldDto("dateField", "Display names should not affect methods", DateTime.class);
        FieldDto timeField = fieldDto("timeField", Time.class);

        List<FieldDto> fields = new ArrayList<>();
        fields.add(testField);
        fields.add(testField2);
        fields.add(dateField);
        fields.add(timeField);
        lookup.setFieldsOrder(asList("TestField", "TestField2", "dateField", "timeField"));
        lookup.setSingleObjectReturn(true);
        when(schemaHolder.getLookups(entity)).thenReturn(singletonList(lookup));

        List<LookupFieldDto> lookupFields = new ArrayList<>();
        lookupFields.add(new LookupFieldDto("TestField", LookupFieldType.VALUE));
        lookupFields.add(new LookupFieldDto("TestField2", LookupFieldType.VALUE));
        lookupFields.add(new LookupFieldDto("dateField", LookupFieldType.RANGE));
        lookupFields.add(new LookupFieldDto("timeField", LookupFieldType.SET));
        lookup.setLookupFields(lookupFields);

        List<ClassData> data = entityInfrastructureBuilder.buildInfrastructure(entity, schemaHolder);

        for (ClassData classData : data) {
            mdsClassLoaderImpl.safeDefineClass(classData.getClassName(), classData.getBytecode());
        }

        verifySingleLookup(mdsClassLoaderImpl.loadClass(SAMPLE_WITH_LOOKUPS_SERVICE));
        verifySingleLookup(mdsClassLoaderImpl.loadClass(SAMPLE_WITH_LOOKUPS_INTERFACE));
        verifyCountLookup(mdsClassLoaderImpl.loadClass(SAMPLE_WITH_LOOKUPS_SERVICE));
        verifyCountLookup(mdsClassLoaderImpl.loadClass(SAMPLE_WITH_LOOKUPS_INTERFACE));

        // lookup with multiple return
        lookup.setSingleObjectReturn(false);
        mdsClassLoaderImpl = MDSClassLoader.getStandaloneInstance(getClass().getClassLoader());

        data = entityInfrastructureBuilder.buildInfrastructure(entity, schemaHolder);

        for (ClassData classData : data) {
            mdsClassLoaderImpl.safeDefineClass(classData.getClassName(), classData.getBytecode());
        }

        verifyMultiReturnLookup(mdsClassLoaderImpl.loadClass(SAMPLE_WITH_LOOKUPS_SERVICE));
        verifyMultiReturnLookup(mdsClassLoaderImpl.loadClass(SAMPLE_WITH_LOOKUPS_INTERFACE));
        verifyCountLookup(mdsClassLoaderImpl.loadClass(SAMPLE_WITH_LOOKUPS_SERVICE));
        verifyCountLookup(mdsClassLoaderImpl.loadClass(SAMPLE_WITH_LOOKUPS_INTERFACE));

        verifyFetchDepthInRepository(mdsClassLoaderImpl.loadClass(SAMPLE_WITH_LOOKUPS_REPOSITORY), -1);
    }

    private void verifySingleLookup(Class<?> serviceClass) throws NoSuchMethodException {
        Method method = getLookupWithoutParams(serviceClass);
        assertEquals(SampleWithLookups.class, method.getReturnType());
        method = getLookupWithParams(serviceClass);
        assertEquals(SampleWithLookups.class, method.getReturnType());
    }

    private void verifyMultiReturnLookup(Class<?> serviceClass) throws NoSuchMethodException {
        Method method = getLookupWithoutParams(serviceClass);
        assertEquals(List.class, method.getReturnType());
        // test generic signature
        assertEquals("java.util.List<org.motechproject.mds.builder.SampleWithLookups>",
                method.getGenericReturnType().toString());
        method = getLookupWithoutParams(serviceClass);
        assertEquals(List.class, method.getReturnType());
        // test generic signature
        assertEquals("java.util.List<org.motechproject.mds.builder.SampleWithLookups>",
                method.getGenericReturnType().toString());
    }

    private void verifyCountLookup(Class<?> serviceClass) throws NoSuchMethodException {
        Method method = serviceClass.getMethod("countTestLookupMethod", String.class, String.class, Range.class, Set.class);

        // check the generic signature of the range/set params
        Type[] genericParamTypes = method.getGenericParameterTypes();
        verifyGenericType(genericParamTypes[2], DateTime.class);
        verifyGenericType(genericParamTypes[3], Time.class);

        assertNotNull(method);
        assertEquals(long.class, method.getReturnType());
    }

    private Method getLookupWithoutParams(Class<?> serviceClass) throws NoSuchMethodException {
        return getLookup(serviceClass, String.class, String.class, Range.class, Set.class);
    }

    private Method getLookupWithParams(Class<?> serviceClass) throws NoSuchMethodException {
        return getLookup(serviceClass, String.class, String.class, Range.class, Set.class, QueryParams.class);
    }

    private Method getLookup(Class<?> serviceClass, Class<?>... params) throws NoSuchMethodException {
        Method method = serviceClass.getMethod("testLookupMethod", params);
        assertNotNull(method);
        return method;
    }

    private void verifyGenericType(Type type, Class<?> expectedClass) {
        assertTrue(type instanceof ParameterizedType);
        assertArrayEquals(new Type[]{expectedClass}, ((ParameterizedType) type).getActualTypeArguments());
    }

    private void verifyFetchDepthInRepository(Class<?> repositoryClass, int expectedFetchDepth) throws IllegalAccessException, InstantiationException {
        MotechDataRepository repository = (MotechDataRepository) repositoryClass.newInstance();
        assertEquals(expectedFetchDepth, ReflectionTestUtils.getField(repository, "fetchDepth"));
    }
}
