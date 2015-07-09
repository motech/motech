package org.motechproject.mds.service;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.annotations.internal.AnotherSample;
import org.motechproject.mds.annotations.internal.Sample;
import org.motechproject.mds.annotations.InstanceLifecycleListenerType;
import org.motechproject.mds.listener.MotechLifecycleListener;
import org.motechproject.mds.service.impl.JdoListenerRegistryServiceImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JdoListenerRegistryServiceTest {

    private JdoListenerRegistryService jdoListenerRegistryService = new JdoListenerRegistryServiceImpl();

    private MotechLifecycleListener listener;
    private MotechLifecycleListener listener2;
    private MotechLifecycleListener listener3;
    private MotechLifecycleListener listener4;
    private MotechLifecycleListener listener5;
    private MotechLifecycleListener listener6;

    @Before
    public void setUp() {
        InstanceLifecycleListenerType[] types = {InstanceLifecycleListenerType.POST_CREATE, InstanceLifecycleListenerType.POST_DELETE};
        InstanceLifecycleListenerType[] postCreateType = {InstanceLifecycleListenerType.POST_CREATE};

        listener = new MotechLifecycleListener(Sample.class, "SampleMethodName",
                Sample.class.getName(), "", types, Arrays.asList(Sample.class.getName()));

        listener2 =  new MotechLifecycleListener(Sample.class, "SampleMethodName2",
                Sample.class.getName(), "", postCreateType, Arrays.asList(Sample.class.getName()));

        listener3 = new MotechLifecycleListener(AnotherSample.class, "AnotherSampleMethodName",
                Sample.class.getName(), "", postCreateType, Arrays.asList(Sample.class.getName()));

        listener4 = new MotechLifecycleListener(AnotherSample.class, "AnotherSampleMethodName2",
                String.class.getName(), "", types, Arrays.asList(String.class.getName()));

        listener5 = new MotechLifecycleListener(AnotherSample.class, "AnotherSampleMethodName3",
                AnotherSample.class.getName(), "", types, Arrays.asList(AnotherSample.class.getName()));

        listener6 = new MotechLifecycleListener(AnotherSample.class, "AnotherSampleMethodName4",
                Object.class.getName(), "org.motechproject.mds", types, Arrays.asList(Sample.class.getName(),
                AnotherSample.class.getName()));

        jdoListenerRegistryService.registerListener(listener);
        jdoListenerRegistryService.registerListener(listener2);
        jdoListenerRegistryService.registerListener(listener3);
        jdoListenerRegistryService.registerListener(listener4);
        jdoListenerRegistryService.registerListener(listener5);
        jdoListenerRegistryService.registerListener(listener6);
    }

    @Test
    public void shouldRegisterListenersCorrectly() {
        // listener1 and listener2 should be merged into one listener
        assertEquals(Arrays.asList(listener, listener3, listener4, listener5, listener6), jdoListenerRegistryService.getListeners());
    }

    @Test
    public void shouldReturnListenersForTheGivenEntityAndType() {
        assertEquals(Arrays.asList(listener, listener3, listener6), jdoListenerRegistryService.getListeners(Sample.class.getName(),
                InstanceLifecycleListenerType.POST_CREATE));

        assertEquals(Arrays.asList(listener, listener6), jdoListenerRegistryService.getListeners(Sample.class.getName(),
                InstanceLifecycleListenerType.POST_DELETE));

        assertEquals(Arrays.asList(listener5, listener6), jdoListenerRegistryService.getListeners(AnotherSample.class.getName(),
                InstanceLifecycleListenerType.POST_CREATE));
    }

    @Test
    public void shouldReturnCorrectListOfMethods() {
        Set<String> methods = new HashSet<>();
        methods.addAll(Arrays.asList("SampleMethodName", "SampleMethodName2"));

        assertEquals(methods, jdoListenerRegistryService.getMethods(listener, InstanceLifecycleListenerType.POST_CREATE));

        methods.remove("SampleMethodName2");

        assertEquals(methods, jdoListenerRegistryService.getMethods(listener, InstanceLifecycleListenerType.POST_DELETE));
    }

    @Test
    public void shouldRemoveInactiveListeners() {
        StringBuilder entities = new StringBuilder();
        entities.append(AnotherSample.class.getName()).append('\n');
        entities.append(Sample.class.getName()).append('\n');
        // Listener for String class should be removed
        jdoListenerRegistryService.removeInactiveListeners(entities.toString());

        String actual = jdoListenerRegistryService.getEntitiesListenerStr();
        assertTrue(actual.contains(AnotherSample.class.getName()));
        assertTrue(actual.contains(Sample.class.getName()));
        assertTrue(!actual.contains(String.class.getName()));
    }
}