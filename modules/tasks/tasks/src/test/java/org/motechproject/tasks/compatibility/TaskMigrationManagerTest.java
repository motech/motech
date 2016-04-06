package org.motechproject.tasks.compatibility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.tasks.domain.mds.task.Task;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TaskMigrationManagerTest {

    private TaskMigrationManager taskMigrationManager = new TaskMigrationManager();

    @Mock
    private Task task;

    @Mock
    private TaskMigrator migrator1;

    @Mock
    private TaskMigrator migrator2;

    @Before
    public void setUp() {
        Set<TaskMigrator> migrators = new HashSet<>(asList(migrator1, migrator2));
        ReflectionTestUtils.setField(taskMigrationManager, "migrators", migrators);
    }

    @Test
    public void shouldMigrateTasks() {
        taskMigrationManager.migrateTask(task);

        verify(migrator1).migrate(task);
        verify(migrator2).migrate(task);
    }
}
