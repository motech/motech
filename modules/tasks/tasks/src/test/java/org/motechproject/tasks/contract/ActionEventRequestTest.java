package org.motechproject.tasks.contract;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ActionEventRequestTest {

    private static final String SERVICE_METHOD = "service.method";
    private static final String SERVICE_INTERFACE = "service.interface";
    private static final String SUBJECT = "some-subject";

    @Test
    public void shouldTestSubjectAvailability() {
        ActionEventRequestBuilder requestBuilder = new ActionEventRequestBuilder();
        ActionEventRequest requestWithSubject = requestBuilder.withSubject(SUBJECT).build();
        assertThat(requestWithSubject.hasSubject(), is(true));
        ActionEventRequest requestWithoutSubject = requestBuilder.withoutSubject().build();
        assertThat(requestWithoutSubject.hasSubject(), is(false));
        ActionEventRequest requestWithEmptySubject = requestBuilder.withEmptySubject().build();
        assertThat(requestWithEmptySubject.hasSubject(), is(false));
    }

    @Test
    public void shouldTestServiceAvailability() {
        ActionEventRequestBuilder requestBuilder = new ActionEventRequestBuilder();
        ActionEventRequest fullyFormedRequest = requestBuilder.withServiceInterface(SERVICE_INTERFACE).withServiceMethod(SERVICE_METHOD).build();
        assertThat(fullyFormedRequest.hasService(), is(true));

        ActionEventRequest requestWithoutServiceInterface = requestBuilder.withoutServiceInterface().withServiceMethod(SERVICE_METHOD).build();
        assertThat(requestWithoutServiceInterface.hasService(), is(false));
        ActionEventRequest requestWithEmptyServiceInterface = requestBuilder.withEmptyServiceInterface().withServiceMethod(SERVICE_METHOD).build();
        assertThat(requestWithEmptyServiceInterface.hasService(), is(false));

        ActionEventRequest requestWithoutServiceMethod = requestBuilder.withServiceInterface(SERVICE_INTERFACE).withoutServiceMethod().build();
        assertThat(requestWithoutServiceMethod.hasService(), is(false));
        ActionEventRequest requestWithEmptyServiceMethod = requestBuilder.withServiceInterface(SERVICE_INTERFACE).withEmptyServiceMethod().build();
        assertThat(requestWithEmptyServiceMethod.hasService(), is(false));
    }


    @Test
    public void shouldConsiderRequestValidIfEitherSubjectOrServicePresent() {
        ActionEventRequestBuilder requestBuilder = new ActionEventRequestBuilder();

        ActionEventRequest fullyFormedRequest = requestBuilder.withSubject(SUBJECT).withServiceInterface(SERVICE_INTERFACE).withServiceMethod(SERVICE_METHOD).build();
        assertThat(fullyFormedRequest.isValid(), is(true));

        ActionEventRequest requestWithSubjectButNotService = requestBuilder.withSubject(SUBJECT).withoutServiceInterface().withoutServiceMethod().build();
        assertThat(requestWithSubjectButNotService.isValid(), is(true));

        ActionEventRequest requestWithoutSubjectButWithService = requestBuilder.withoutSubject().withServiceInterface(SERVICE_INTERFACE).withServiceMethod(SERVICE_METHOD).build();
        assertThat(requestWithoutSubjectButWithService.isValid(), is(true));

        ActionEventRequest requestWithoutSubjectAndWithoutService = requestBuilder.withoutSubject().withoutServiceInterface().withoutServiceMethod().build();
        assertThat(requestWithoutSubjectAndWithoutService.isValid(), is(false));

        ActionEventRequest requestWithoutSubjectAndWithServiceMethodOnly = requestBuilder.withoutSubject().withoutServiceInterface().withServiceMethod(SERVICE_METHOD).build();
        assertThat(requestWithoutSubjectAndWithServiceMethodOnly.isValid(), is(false));

        ActionEventRequest requestWithoutSubjectAndWithServiceInterfaceOnly = requestBuilder.withoutSubject().withServiceInterface(SERVICE_INTERFACE).withoutServiceMethod().build();
        assertThat(requestWithoutSubjectAndWithServiceInterfaceOnly.isValid(), is(false));
    }

}
