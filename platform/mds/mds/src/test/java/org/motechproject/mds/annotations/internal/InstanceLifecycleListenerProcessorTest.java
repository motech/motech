package org.motechproject.mds.annotations.internal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.InstanceLifecycleListenerType;
import org.motechproject.mds.listener.MotechLifecycleListener;
import org.motechproject.mds.service.JdoListenerRegistryService;
import org.motechproject.mds.service.impl.JdoListenerRegistryServiceImpl;
import org.motechproject.mds.testutil.MockBundle;
import org.osgi.framework.Bundle;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class InstanceLifecycleListenerProcessorTest extends MockBundle {

    @Spy
    private Bundle bundle = new org.eclipse.gemini.blueprint.mock.MockBundle();

    private JdoListenerRegistryService jdoListenerRegistryService = new JdoListenerRegistryServiceImpl();
    private InstanceLifecycleListenerProcessor processor;
    private static final String ENTITY_NAMES = Sample.class.getName();

    @Before
    public void setUp() throws Exception {
        processor = new InstanceLifecycleListenerProcessor();
        processor.setJdoListenerRegistryService(jdoListenerRegistryService);

        File file = computeTestDataRoot(getClass());
        String location = file.toURI().toURL().toString();

        doReturn(location).when(bundle).getLocation();
        doReturn(Sample.class).when(bundle).loadClass(Sample.class.getName());

        setUpMockBundle();
    }

    @Test
    public void shouldReturnCorrectListeners() {
        processor.processAnnotations(bundle);
        jdoListenerRegistryService.removeInactiveListeners(ENTITY_NAMES);

        List<MotechLifecycleListener> listeners = jdoListenerRegistryService.getListeners();

        Set<String> postCreateMethods = new HashSet<>();
        postCreateMethods.add("correctListener");

        Set<String> postDeleteMethods = new HashSet<>();
        postDeleteMethods.add("correctListener");
        postDeleteMethods.add("correctPostDeleteListener");

        assertEquals(1, listeners.size());
        MotechLifecycleListener listener = listeners.get(0);

        assertEquals(Sample.class.getName(), listener.getEntity());
        assertEquals(Sample.class, listener.getService());
        assertEquals(postCreateMethods, listener.getMethodsByType().get(InstanceLifecycleListenerType.POST_CREATE));
        assertEquals(postDeleteMethods, listener.getMethodsByType().get(InstanceLifecycleListenerType.POST_DELETE));
    }

    @Override
    protected Map<String, Class> getMappingsForLoader() {
        Map<String, Class> mappings = new LinkedHashMap<>();
        mappings.put(Sample.class.getName(), Sample.class);

        return mappings;
    }

    @Override
    protected Class getTestClass() {
        return getClass();
    }

    @Override
    protected Bundle getMockBundle() {
        return bundle;
    }

}
