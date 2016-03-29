package org.motechproject.scheduler.tasks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.tasks.service.DynamicChannelProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DynamicChannelProviderRegistererTest {

    private static final String TASKS_BUNDLE_SYMBOLIC_NAME = "org.motechproject.motech-tasks";

    @InjectMocks
    private DynamicChannelProviderRegisterer registerer;

    @Mock
    private Properties sqlProperties;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private Bundle bundle;

    @Before
    public void setUp() {
        registerer = new DynamicChannelProviderRegisterer();
        initMocks(this);
    }

    @Test
    public void shouldRegisterServiceIfTasksModuleIsAlreadyAvailable() {
        when(bundleContext.getBundle(TASKS_BUNDLE_SYMBOLIC_NAME)).thenReturn(bundle);

        registerer.init();

        verify(bundleContext).getBundle(TASKS_BUNDLE_SYMBOLIC_NAME);
        verify(bundleContext).registerService(
                eq(DynamicChannelProvider.class.getName()),
                eq(new SchedulerChannelProvider(sqlProperties)),
                eq(new Hashtable<>())
        );
    }

    @Test
    public void shouldNotRegisterServiceIfTasksModuleIsNotAvailable() {
        when(bundleContext.getBundle(TASKS_BUNDLE_SYMBOLIC_NAME)).thenReturn(null);

        registerer.init();

        verify(bundleContext).getBundle(TASKS_BUNDLE_SYMBOLIC_NAME);
        verify(bundleContext, never()).registerService(
                anyString(),
                any(SchedulerChannelProvider.class),
                any(Dictionary.class)
        );
    }

    @Test
    public void shouldRegisterServiceIfTasksModuleBecomesAvailable() {
        when(bundle.getSymbolicName()).thenReturn(TASKS_BUNDLE_SYMBOLIC_NAME);

        registerer.addingBundle(bundle, null);

        verify(bundle).getSymbolicName();
        verify(bundleContext).registerService(
                eq(DynamicChannelProvider.class.getName()),
                eq(new SchedulerChannelProvider(sqlProperties)),
                eq(new Hashtable<>())
        );
    }

    @Test
    public void shouldNotRegisterIfNonTasksModuleBecomesAvailable() {
        when(bundle.getSymbolicName()).thenReturn("some.other.module");

        registerer.addingBundle(bundle, null);


        verify(bundle).getSymbolicName();
        verify(bundleContext, never()).registerService(
                anyString(),
                any(SchedulerChannelProvider.class),
                any(Dictionary.class)
        );
    }

}