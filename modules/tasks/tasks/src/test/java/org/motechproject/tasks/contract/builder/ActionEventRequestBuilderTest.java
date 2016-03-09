package org.motechproject.tasks.contract.builder;

import org.junit.Test;
import org.motechproject.tasks.contract.ActionEventRequest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ActionEventRequestBuilderTest {

    private static final String SERVICE_METHOD = "service.method";
    private static final String SERVICE_INTERFACE = "service.interface";
    private static final String SUBJECT = "some-subject";

    @Test
    public void shouldTestSubjectAvailability() {
        TestActionEventRequestBuilder requestBuilder = new TestActionEventRequestBuilder();
        ActionEventRequest requestWithSubject = requestBuilder.setSubject(SUBJECT).createActionEventRequest();
        assertThat(requestWithSubject.hasSubject(), is(true));
        ActionEventRequest requestWithoutSubject = requestBuilder.setNullSubject().createActionEventRequest();
        assertThat(requestWithoutSubject.hasSubject(), is(false));
        ActionEventRequest requestWithEmptySubject = requestBuilder.setEmptySubject().createActionEventRequest();
        assertThat(requestWithEmptySubject.hasSubject(), is(false));
    }

    @Test
    public void shouldTestServiceAvailability() {
        TestActionEventRequestBuilder requestBuilder = new TestActionEventRequestBuilder();
        ActionEventRequest fullyFormedRequest = requestBuilder.setServiceInterface(SERVICE_INTERFACE).setServiceMethod(SERVICE_METHOD).createActionEventRequest();
        assertThat(fullyFormedRequest.hasService(), is(true));

        ActionEventRequest requestWithoutServiceInterface = requestBuilder.setNullServiceInterface().setServiceMethod(SERVICE_METHOD).createActionEventRequest();
        assertThat(requestWithoutServiceInterface.hasService(), is(false));
        ActionEventRequest requestWithEmptyServiceInterface = requestBuilder.setEmptyServiceInterface().setServiceMethod(SERVICE_METHOD).createActionEventRequest();
        assertThat(requestWithEmptyServiceInterface.hasService(), is(false));

        ActionEventRequest requestWithoutServiceMethod = requestBuilder.setServiceInterface(SERVICE_INTERFACE).setNullServiceMethod().createActionEventRequest();
        assertThat(requestWithoutServiceMethod.hasService(), is(false));
        ActionEventRequest requestWithEmptyServiceMethod = requestBuilder.setServiceInterface(SERVICE_INTERFACE).setEmptyServiceMethod().createActionEventRequest();
        assertThat(requestWithEmptyServiceMethod.hasService(), is(false));
    }


    @Test
    public void shouldConsiderRequestValidIfEitherSubjectOrServicePresent() {
        TestActionEventRequestBuilder requestBuilder = new TestActionEventRequestBuilder();

        ActionEventRequest fullyFormedRequest = requestBuilder.setSubject(SUBJECT).setServiceInterface(SERVICE_INTERFACE).setServiceMethod(SERVICE_METHOD).createActionEventRequest();
        assertThat(fullyFormedRequest.isValid(), is(true));

        ActionEventRequest requestWithSubjectButNotService = requestBuilder.setSubject(SUBJECT).setNullServiceInterface().setNullServiceMethod().createActionEventRequest();
        assertThat(requestWithSubjectButNotService.isValid(), is(true));

        ActionEventRequest requestWithoutSubjectButWithService = requestBuilder.setNullSubject().setServiceInterface(SERVICE_INTERFACE).setServiceMethod(SERVICE_METHOD).createActionEventRequest();
        assertThat(requestWithoutSubjectButWithService.isValid(), is(true));

        ActionEventRequest requestWithoutSubjectAndWithoutService = requestBuilder.setNullSubject().setNullServiceInterface().setNullServiceMethod().createActionEventRequest();
        assertThat(requestWithoutSubjectAndWithoutService.isValid(), is(false));

        ActionEventRequest requestWithoutSubjectAndWithServiceMethodOnly = requestBuilder.setNullSubject().setNullServiceInterface().setServiceMethod(SERVICE_METHOD).createActionEventRequest();
        assertThat(requestWithoutSubjectAndWithServiceMethodOnly.isValid(), is(false));

        ActionEventRequest requestWithoutSubjectAndWithServiceInterfaceOnly = requestBuilder.setNullSubject().setServiceInterface(SERVICE_INTERFACE).setNullServiceMethod().createActionEventRequest();
        assertThat(requestWithoutSubjectAndWithServiceInterfaceOnly.isValid(), is(false));
    }

}
