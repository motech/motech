package org.motechproject.tasks.service;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.motechproject.event.listener.annotations.MotechListenerAbstractProxy;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.event.listener.annotations.MotechListenerNamedParametersProxy;
import org.motechproject.tasks.domain.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.find;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HandlerPredicatesTest {

    @Test
    public void shouldFoundCorrectObject() {
        MotechListenerEventProxy expected = new MotechListenerEventProxy("def", null, null);

        List<MotechListenerAbstractProxy> list = new ArrayList<>();
        list.add(new MotechListenerNamedParametersProxy("abc", null, null));
        list.add(expected);
        list.add(new MotechListenerEventProxy("ghi", null, null));

        MotechListenerAbstractProxy actual = (MotechListenerAbstractProxy) find(list, HandlerPredicates.withServiceName("def"));

        assertTrue(actual instanceof MotechListenerEventProxy);
        assertEquals(expected.getIdentifier(), actual.getIdentifier());
    }

    @Test
    public void shouldNotFoundCorrectObject() {
        List<MotechListenerAbstractProxy> list = new ArrayList<>();
        list.add(new MotechListenerNamedParametersProxy("abc", null, null));
        list.add(new MotechListenerEventProxy("def", null, null));
        list.add(new MotechListenerEventProxy("ghi", null, null));

        MotechListenerAbstractProxy actual = (MotechListenerAbstractProxy) find(list, HandlerPredicates.withServiceName("abc"));

        assertNull(actual);
    }

    @Test
    public void shouldRemoveDisabledTasks() {
        Task enabledTask1 = new Task("enabledTask1", null, null, null, true);
        Task enabledTask2 = new Task("enabledTask2", null, null, null, true);

        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("disabledTask1", null, null, null, false));
        tasks.add(new Task("disabledTask2", null, null, null, false));
        tasks.add(enabledTask1);
        tasks.add(enabledTask2);

        CollectionUtils.filter(tasks, HandlerPredicates.activeTasks());

        assertEquals(Arrays.asList(enabledTask1, enabledTask2), tasks);
    }
}
