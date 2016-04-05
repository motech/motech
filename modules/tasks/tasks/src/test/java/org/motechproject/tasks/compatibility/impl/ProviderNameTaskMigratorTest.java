package org.motechproject.tasks.compatibility.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.tasks.domain.mds.task.DataSource;
import org.motechproject.tasks.domain.mds.task.Filter;
import org.motechproject.tasks.domain.mds.task.FilterSet;
import org.motechproject.tasks.domain.mds.task.Lookup;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.TaskConfig;
import org.motechproject.tasks.ex.ProviderNotFoundException;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProviderNameTaskMigratorTest {

    private ProviderNameTaskMigrator migrator = new ProviderNameTaskMigrator();

    @Mock
    private Task task;

    @Mock
    private TaskConfig taskConfig;

    @Mock
    private TaskActionInformation action1;

    @Mock
    private TaskActionInformation action2;

    @Mock
    private DataSource dataSource1;

    @Mock
    private DataSource dataSource2;

    @Mock
    private FilterSet filterSet;

    @Mock
    private Filter filter;

    @Mock
    private Lookup lookup;

    private Map<String, String> action1Values;

    private Map<String, String> action2Values;

    @Test
    public void shouldMigrateTasks() {
        prepareTask();

        migrator.migrate(task);

        // make sure the maps for actions were change accordingly
        assertEquals("{{ad.data-services.MotechPermission#0.name}}", action1Values.get("key-to-change"));
        assertEquals("{{ad.data-services.MotechPermission#1.name}}", action1Values.get("key-to-leave"));

        assertEquals("{{ad.cms-lite.StringResource#0.value}}", action2Values.get("key-to-change"));
        assertEquals("test", action2Values.get("key-to-leave"));

        //verify changes to task config
        verify(filter).setKey("ad.data-services.MotechPermission#0.name");
        verify(lookup).setValue("{{ad.data-services.MotechPermission#0.name}}");
    }

    @Test(expected = ProviderNotFoundException.class)
    public void shouldThrowExceptionOnNonExistentProvider() {
        prepareTask();
        action1Values.put("invalid", "{{ad.3.something#1.val}}");

        migrator.migrate(task);
    }

    private void prepareTask() {
        when(task.getTaskConfig()).thenReturn(taskConfig);
        when(task.getActions()).thenReturn(asList(action1, action2));
        when(taskConfig.getDataSources()).thenReturn(asList(dataSource1, dataSource2));
        when(taskConfig.getFilters()).thenReturn(singletonList(filterSet));
        when(filterSet.getFilters()).thenReturn(singletonList(filter));

        action1Values = new HashMap<>();
        action1Values.put("key-to-change", "{{ad.1.MotechPermission#0.name}}");
        action1Values.put("key-to-leave", "{{ad.data-services.MotechPermission#1.name}}");
        when(action1.getValues()).thenReturn(action1Values);

        action2Values = new HashMap<>();
        action2Values.put("key-to-change", "{{ad.2.StringResource#0.value}}");
        action2Values.put("key-to-leave", "test");
        when(action2.getValues()).thenReturn(action2Values);

        when(filter.getKey()).thenReturn("ad.1.MotechPermission#0.name");

        when(dataSource1.getProviderId()).thenReturn(1L);
        when(dataSource1.getProviderName()).thenReturn("data-services");

        when(dataSource2.getProviderId()).thenReturn(2L);
        when(dataSource2.getProviderName()).thenReturn("cms-lite");
        when(dataSource2.getLookup()).thenReturn(singletonList(lookup));
        when(lookup.getValue()).thenReturn("{{ad.1.MotechPermission#0.name}}");
    }
}