package org.motechproject.tasks.domain;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class TaskConfigTest {

    @Test
    public void shouldRemoveAllSteps() throws Exception {
        TaskConfig config = new TaskConfig()
                .add(new FilterSet(), new FilterSet(), new FilterSet())
                .add(new DataSource(), new DataSource(), new DataSource());

        assertNotNull(config.getSteps());
        assertFalse(config.getSteps().isEmpty());

        config.removeAll();

        assertNotNull(config.getSteps());
        assertTrue(config.getSteps().isEmpty());
    }

    @Test
    public void shouldRemoveOnlyFilterSets() throws Exception {
        TaskConfig config = new TaskConfig()
                .add(new FilterSet(), new FilterSet(), new FilterSet())
                .add(new DataSource(), new DataSource(), new DataSource());

        assertNotNull(config.getSteps());
        assertFalse(config.getSteps().isEmpty());

        config.removeFilterSets();

        assertNotNull(config.getSteps());
        assertFalse(config.getSteps().isEmpty());

        assertTrue(config.getFilters().isEmpty());
        assertFalse(config.getDataSources().isEmpty());
    }

    @Test
    public void shouldRemoveOnlyDataSources() throws Exception {
        TaskConfig config = new TaskConfig()
                .add(new FilterSet(), new FilterSet(), new FilterSet())
                .add(new DataSource(), new DataSource(), new DataSource());

        assertNotNull(config.getSteps());
        assertFalse(config.getSteps().isEmpty());

        config.removeDataSources();

        assertNotNull(config.getSteps());
        assertFalse(config.getSteps().isEmpty());

        assertFalse(config.getFilters().isEmpty());
        assertTrue(config.getDataSources().isEmpty());
    }

    @Test
    public void shouldAddStepAndSetNewOrderNumber() throws Exception {
        List<TaskConfigStep> steps = new ArrayList<>();
        steps.add(new FilterSet());
        steps.add(new DataSource());
        steps.add(new DataSource());
        steps.add(new FilterSet());

        Random random = new Random();
        for (TaskConfigStep step : steps) {
            step.setOrder(random.nextInt());
        }

        TaskConfig config = new TaskConfig();
        for (TaskConfigStep step : steps) {
            config.add(step);
        }

        List<Integer> orders = new ArrayList<>(steps.size());
        for (TaskConfigStep step : config.getSteps()) {
            orders.add(step.getOrder());
        }

        assertThat(orders, hasItems(0, 1, 2, 3));
    }

    @Test
    public void shouldAddAllStepsAndSetNewOrderNumber() throws Exception {
        List<TaskConfigStep> steps = new ArrayList<>();
        steps.add(new FilterSet());
        steps.add(new DataSource());
        steps.add(new DataSource());
        steps.add(new FilterSet());

        Random random = new Random();

        for (TaskConfigStep step : steps) {
            step.setOrder(random.nextInt());
        }

        TaskConfig config = new TaskConfig()
                .addAll(null)
                .addAll(new TreeSet<TaskConfigStep>())
                .addAll(new TreeSet<>(steps));

        List<Integer> orders = new ArrayList<>(steps.size());

        for (TaskConfigStep step : config.getSteps()) {
            orders.add(step.getOrder());
        }

        assertThat(orders, hasItems(0, 1, 2, 3));
    }
}
