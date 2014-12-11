package org.motechproject.mds.event;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.motechproject.mds.util.Constants.MDSEvents.BASE_SUBJECT;
import static org.motechproject.mds.event.CrudEventType.CREATE;

public class CrudEventBuilderTest {

    private static final String MODULE = "test_module";
    private static final String NAMESPACE = "test_namespace";
    private static final String ENTITY = "test_entity";

    @Test
    public void shouldReturnValidSubject() {
        String subject = CrudEventBuilder.createSubject(MODULE, ENTITY, NAMESPACE, CREATE);
        String expected = BASE_SUBJECT + MODULE + "." + NAMESPACE + "." + ENTITY + "." + CREATE;

        assertThat(subject, equalTo(expected));
    }

    @Test
    public void shouldReturnValidSubjectWithoutModuleAndNamespace() {
        String subject = CrudEventBuilder.createSubject(null, ENTITY, null, CREATE);
        String expected = BASE_SUBJECT + ENTITY + "." + CREATE;

        assertThat(subject, equalTo(expected));
    }

    @Test
    public void shouldReturnValidSubjectWithoutNamespace() {
        String subject = CrudEventBuilder.createSubject(MODULE, ENTITY, null, CREATE);
        String expected = BASE_SUBJECT + MODULE + "." + ENTITY + "." + CREATE;

        assertThat(subject, equalTo(expected));
    }
}
