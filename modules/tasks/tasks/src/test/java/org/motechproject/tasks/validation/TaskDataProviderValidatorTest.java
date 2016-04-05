package org.motechproject.tasks.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.tasks.domain.mds.task.LookupFieldsParameter;
import org.motechproject.tasks.domain.mds.task.TaskDataProvider;
import org.motechproject.tasks.domain.mds.task.TaskDataProviderObject;
import org.motechproject.tasks.domain.mds.task.TaskError;

import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class TaskDataProviderValidatorTest {

    @Mock
    private TaskDataProvider provider;

    @Mock
    private TaskDataProviderObject providerObject;

    @Test
    public void shouldValidateValidNames() {
        mockValidProviderObjects();

        testValidName("test");
        testValidName("MyName");
        testValidName("data-services");
        testValidName("cms_lite_123");
    }

    @Test
    public void shouldValidateAgainstBlankName() {
        when(provider.getName()).thenReturn("");
        mockValidProviderObjects();

        Set<TaskError> errors = TaskDataProviderValidator.validate(provider);

        assertSingleError(errors, "task.validation.error.blank", "name", "taskDataProvider");
    }

    @Test
    public void shouldValidateAgainstNullName() {
        when(provider.getName()).thenReturn(null);
        mockValidProviderObjects();

        Set<TaskError> errors = TaskDataProviderValidator.validate(provider);

        assertSingleError(errors, "task.validation.error.blank", "name", "taskDataProvider");
    }

    @Test
    public void shouldValidateAgainstInvalidName() {
        mockValidProviderObjects();

        testInvalidName("A space");
        testInvalidName("Symbols$#");
        testInvalidName(";wrong");
    }

    private void testValidName(String name) {
        when(provider.getName()).thenReturn(name);

        Set<TaskError> errors = TaskDataProviderValidator.validate(provider);

        assertNotNull(errors);
        assertTrue(errors.isEmpty());
    }

    private void testInvalidName(String name) {
        when(provider.getName()).thenReturn(name);

        Set<TaskError> errors = TaskDataProviderValidator.validate(provider);

        assertSingleError(errors, "task.validation.error.provider.name", name);
    }

    private void assertSingleError(Set<TaskError> errors, String reason, String... args) {
        assertNotNull(errors);
        assertEquals(1, errors.size());

        TaskError error = errors.iterator().next();

        assertNotNull(error);
        assertEquals(reason, error.getMessage());
        assertEquals(asList(args), error.getArgs());
    }

    private void mockValidProviderObjects() {
        when(providerObject.getLookupFields()).thenReturn(singletonList(
                new LookupFieldsParameter("disp", singletonList("field"))));
        when(providerObject.getType()).thenReturn("type");
        when(providerObject.getDisplayName()).thenReturn("disp");

        when(provider.getObjects()).thenReturn(singletonList(providerObject));
    }
}
