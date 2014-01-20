package org.motechproject.mds.builder;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
import static org.motechproject.mds.builder.EntityInfrastructureBuilder.ClassMapping;

@RunWith(MockitoJUnitRunner.class)
public class EntityInfrastructureBuilderTest {
    private static final String SAMPLE_REPOSITORY = "org.motechproject.mds.repository.AllSamples";
    private static final String SAMPLE_INTERFACE = "org.motechproject.mds.service.SampleService";
    private static final String SAMPLE_SERVICE = "org.motechproject.mds.service.impl.SampleServiceImpl";

    @Mock
    private ClassLoader classLoader;

    @Test
    public void shouldCreateCodeIfClassNotExistsInClassPath() throws Exception {
        doThrow(new ClassNotFoundException()).when(classLoader).loadClass(SAMPLE_SERVICE);

        List<ClassMapping> mappings = EntityInfrastructureBuilder.create(classLoader, Sample.class);

        assertNotNull(mappings);
        assertFalse(mappings.isEmpty());
        assertThat(mappings, hasItem(Matchers.<ClassMapping>hasProperty("className", equalTo(SAMPLE_REPOSITORY))));
        assertThat(mappings, hasItem(Matchers.<ClassMapping>hasProperty("className", equalTo(SAMPLE_INTERFACE))));
        assertThat(mappings, hasItem(Matchers.<ClassMapping>hasProperty("className", equalTo(SAMPLE_SERVICE))));
    }

    @Test
    public void shouldNotCreateCodeIfClassExistsInClassPath() throws Exception {
        doReturn(Sample.class).when(classLoader).loadClass(anyString());

        assertTrue(EntityInfrastructureBuilder.create(classLoader, Sample.class).isEmpty());
    }
}
