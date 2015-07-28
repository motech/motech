package org.motechproject.mds.builder;

import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.builder.impl.EntityInfrastructureBuilderImpl;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.testutil.FieldTestHelper;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    private EntityInfrastructureBuilder entityInfrastructureBuilder = new EntityInfrastructureBuilderImpl();

    @Before
    public void setUp() throws Exception {
        PowerMockito.spy(MDSClassLoader.class);
        when(MDSClassLoader.getInstance()).thenReturn(classLoader);
    }

    @Test
    public void shouldCreateCodeIfClassNotExistsInClassPath() throws Exception {
        doReturn(null).when(classLoader).loadClass(SAMPLE_SERVICE);

        Entity entity = new Entity(Sample.class.getName());
        List<ClassData> data = entityInfrastructureBuilder.buildInfrastructure(entity);

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

        Entity entity = new Entity(SampleWithLookups.class.getName());
        entity.setMaxFetchDepth(-1);

        Lookup lookup = new Lookup();
        lookup.setLookupName("testLookup");
        lookup.setLookupName("testLookupMethod");

        Field testField = FieldTestHelper.field("TestField", "testDispName", String.class);
        Field testField2 = FieldTestHelper.field("TestField2", "DisplayName with space", String.class);
        Field dateField = FieldTestHelper.field("dateField", "Display names should not affect methods", DateTime.class);
        Field timeField = FieldTestHelper.field("timeField", Time.class);

        List<Field> fields = new ArrayList<>();
        fields.add(testField);
        fields.add(testField2);
        fields.add(dateField);
        fields.add(timeField);
        lookup.setFields(fields);
        lookup.setSingleObjectReturn(true);
        lookup.setRangeLookupFields(Arrays.asList("dateField"));
        lookup.setSetLookupFields(Arrays.asList("timeField"));
        lookup.setFieldsOrder(Arrays.asList(testField.getName(), testField2.getName(), dateField.getName(), timeField.getName()));

        entity.addLookup(lookup);

        List<ClassData> data = entityInfrastructureBuilder.buildInfrastructure(entity);

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

        data = entityInfrastructureBuilder.buildInfrastructure(entity);

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
