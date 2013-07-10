package org.motechproject.tasks.domain;

import org.junit.Test;

import static junit.framework.Assert.*;

public class TaskBuilderTest {
    private static final String TASK_NAME = "name";
    private static final String TASK_DESCRIPTION = "myDescription";
    private static final boolean isEnabled = true;

    @Test
    public void shouldReturnBuiltTaskObject() throws Exception {
        TaskBuilder builder = new TaskBuilder();

        Task task = builder.withName(TASK_NAME).withDescription(TASK_DESCRIPTION).isEnabled(isEnabled)
                .withTrigger(new TaskEventInformation()).addAction(new TaskActionInformation())
                .withTaskConfig(new TaskConfig()).addFilterSet(new FilterSet()).addDataSource(new DataSource())
                .build();

        assertNotNull(task);
        assertEquals(TASK_NAME, task.getName());
        assertEquals(TASK_DESCRIPTION, task.getDescription());
        assertEquals(isEnabled, task.isEnabled());
        assertNotNull(task.getTrigger());
        assertNotNull(task.getActions());
        assertFalse(task.getActions().isEmpty());
        assertNotNull(task.getTaskConfig());
        assertFalse(task.getTaskConfig().getDataSources().isEmpty());
        assertFalse(task.getTaskConfig().getFilters().isEmpty());
    }

    @Test
    public void shouldReturnEmptyTaskObject() throws Exception {
        TaskBuilder builder = new TaskBuilder();

        Task task = builder.withName(TASK_NAME).withDescription(TASK_DESCRIPTION).isEnabled(isEnabled)
                .withTrigger(new TaskEventInformation()).addAction(new TaskActionInformation())
                .withTaskConfig(new TaskConfig()).addFilterSet(new FilterSet()).addDataSource(new DataSource())
                .clear().build();

        assertNotNull(task);
        assertTrue(task.getName().isEmpty());
        assertTrue(task.getDescription().isEmpty());
        assertEquals(false, task.isEnabled());
        assertNull(task.getTrigger());
        assertNotNull(task.getActions());
        assertTrue(task.getActions().isEmpty());
        assertNotNull(task.getTaskConfig());
        assertTrue(task.getTaskConfig().getSteps().isEmpty());
    }
}
