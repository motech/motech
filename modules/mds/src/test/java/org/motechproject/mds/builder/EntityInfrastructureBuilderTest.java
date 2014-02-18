package org.motechproject.mds.builder;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.mds.builder.impl.EntityInfrastructureBuilderImpl;
import org.motechproject.mds.util.Constants.PackagesGenerated;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

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

    @Mock
    private MDSClassLoader classLoader;

    private EntityInfrastructureBuilder entityInfrastructureBuilder = new EntityInfrastructureBuilderImpl();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(MDSClassLoader.class);
        when(MDSClassLoader.getInstance()).thenReturn(classLoader);
    }

    @Test
    public void shouldCreateCodeIfClassNotExistsInClassPath() throws Exception {
        doThrow(new ClassNotFoundException()).when(classLoader).loadClass(SAMPLE_SERVICE);

        List<ClassData> data = entityInfrastructureBuilder.buildInfrastructure(Sample.class.getName());

        assertNotNull(data);
        assertFalse(data.isEmpty());
        assertThat(data, hasItem(Matchers.<ClassData>hasProperty("className", equalTo(SAMPLE_REPOSITORY))));
        assertThat(data, hasItem(Matchers.<ClassData>hasProperty("className", equalTo(SAMPLE_INTERFACE))));
        assertThat(data, hasItem(Matchers.<ClassData>hasProperty("className", equalTo(SAMPLE_SERVICE))));
    }

    @Test
    public void shouldNotCreateCodeIfClassExistsInClassPath() throws Exception {
        doReturn(Sample.class).when(classLoader).loadClass(anyString());

        assertTrue(entityInfrastructureBuilder.buildInfrastructure(Sample.class.getName()).isEmpty());
    }
}
