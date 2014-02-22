package org.motechproject.mds.builder;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.mds.builder.impl.EntityInfrastructureBuilderImpl;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.util.Constants.PackagesGenerated;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MDSClassLoader.class)
public class EntityInfrastructureBuilderTest {
    private static final String SAMPLE_REPOSITORY = PackagesGenerated.REPOSITORY.concat(".AllSamples");
    private static final String SAMPLE_INTERFACE = PackagesGenerated.SERVICE.concat(".SampleService");
    private static final String SAMPLE_SERVICE = PackagesGenerated.SERVICE_IMPL.concat(".SampleServiceImpl");

    private static final String SAMPLE_WITH_LOOKUPS_SERVICE = PackagesGenerated.SERVICE_IMPL.concat(".SampleWithLookupsServiceImpl");

    @Mock
    private MDSClassLoader classLoader;

    private MDSClassLoader mdsClassLoader;

    private EntityInfrastructureBuilder entityInfrastructureBuilder = new EntityInfrastructureBuilderImpl();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(MDSClassLoader.class);
        when(MDSClassLoader.getInstance()).thenReturn(classLoader);
        mdsClassLoader = new MDSClassLoader(getClass().getClassLoader());
    }

    @Test
    public void shouldCreateCodeIfClassNotExistsInClassPath() throws Exception {
        doThrow(new ClassNotFoundException()).when(classLoader).loadClass(SAMPLE_SERVICE);

        Entity entity = new Entity(Sample.class.getName());
        List<ClassData> data = entityInfrastructureBuilder.buildInfrastructure(entity);

        assertNotNull(data);
        assertFalse(data.isEmpty());
        assertThat(data, hasItem(Matchers.<ClassData>hasProperty("className", equalTo(SAMPLE_REPOSITORY))));
        assertThat(data, hasItem(Matchers.<ClassData>hasProperty("className", equalTo(SAMPLE_INTERFACE))));
        assertThat(data, hasItem(Matchers.<ClassData>hasProperty("className", equalTo(SAMPLE_SERVICE))));
    }

    @Test
    public void shouldNotCreateCodeIfClassExistsInClassPath() throws Exception {
        Entity entity = new Entity(Sample.class.getName());
        doReturn(Sample.class).when(classLoader).loadClass(anyString());

        assertTrue(entityInfrastructureBuilder.buildInfrastructure(entity).isEmpty());
    }

    @Test
    public void shouldCreateCodeForClassWithLookups() throws Exception {
        Entity entity = new Entity(SampleWithLookups.class.getName());

        Lookup lookup = new Lookup();
        lookup.setLookupName("testLookup");
        Type type = new Type();
        type.setTypeClass(java.lang.String.class);
        type.setDisplayName("mds.field.string");
        type.setDescription("mds.field.description.string");

        Field testField = new Field();
        testField.setDisplayName("TestField");
        testField.setType(type);
        Field testField2 = new Field();
        testField2.setDisplayName("TestField2");
        testField2.setType(type);

        Set<Field> fields = new HashSet<>();
        fields.add(testField);
        fields.add(testField2);
        lookup.setFields(fields);
        lookup.setSingleObjectReturn(true);
        entity.addLookup(lookup);

        List<ClassData> data = entityInfrastructureBuilder.buildInfrastructure(entity);

        for (ClassData classData : data) {
            mdsClassLoader.defineClass(classData);
        }

        Class<?> clazz = mdsClassLoader.loadClass(SAMPLE_WITH_LOOKUPS_SERVICE);
        assertNotNull(clazz.getMethod("testLookup", java.lang.String.class, java.lang.String.class));
    }
}
