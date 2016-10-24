package org.motechproject.tasks.contract.builder;

import org.motechproject.tasks.contract.ActionParameterRequest;

import java.util.SortedSet;

/**
 * The <code>TestActionEventRequestBuilder</code> class provides methods for constructing action event requests.
 * It sets th <code>ActionEventRequest</code> properties defaults values and contains several methods helpful
 * for testing.
 *
 * @see org.motechproject.tasks.contract.ActionEventRequest
 */
public class TestActionEventRequestBuilder extends ActionEventRequestBuilder {

    private static final String EMPTY = "";
    private static final String NAMED_PARAMETERS = "NAMED_PARAMETERS";

    public TestActionEventRequestBuilder() {
        setDisplayName("displayName");
        setSubject("subject");
        setDescription("description");
        setServiceInterface("service.interface");
        setServiceMethod("service.method");
        setServiceMethodCallManner(NAMED_PARAMETERS);
    }

    public TestActionEventRequestBuilder setEmptySubject() {
        return setSubject(EMPTY);
    }

    public TestActionEventRequestBuilder setNullSubject() {
        return setSubject(null);
    }

    public TestActionEventRequestBuilder setEmptyServiceInterface() {
        return setServiceInterface(EMPTY);
    }

    public TestActionEventRequestBuilder setNullServiceInterface() {
        return setServiceInterface(null);
    }

    public TestActionEventRequestBuilder setEmptyServiceMethod() {
        return setServiceMethod(EMPTY);
    }


    public TestActionEventRequestBuilder setNullServiceMethod() {
        return setServiceMethod(null);
    }

    @Override
    public TestActionEventRequestBuilder setDisplayName(String displayName) {
        return (TestActionEventRequestBuilder) super.setDisplayName(displayName);
    }

    @Override
    public TestActionEventRequestBuilder setSubject(String subject) {
        return (TestActionEventRequestBuilder) super.setSubject(subject);
    }

    @Override
    public TestActionEventRequestBuilder setDescription(String description) {
        return (TestActionEventRequestBuilder) super.setDescription(description);
    }

    @Override
    public TestActionEventRequestBuilder setServiceInterface(String serviceInterface) {
        return (TestActionEventRequestBuilder) super.setServiceInterface(serviceInterface);
    }

    @Override
    public TestActionEventRequestBuilder setServiceMethod(String serviceMethod) {
        return (TestActionEventRequestBuilder) super.setServiceMethod(serviceMethod);
    }

    @Override
    public TestActionEventRequestBuilder setActionParameters(SortedSet<ActionParameterRequest> actionParameters) {
        return (TestActionEventRequestBuilder) super.setActionParameters(actionParameters);
    }

    @Override
    public TestActionEventRequestBuilder setPostActionParameters(SortedSet<ActionParameterRequest> postActionParameters) {
        return (TestActionEventRequestBuilder) super.setPostActionParameters(postActionParameters);
    }

    @Override
    public TestActionEventRequestBuilder setServiceMethodCallManner(String serviceMethodCallManner) {
        return (TestActionEventRequestBuilder) super.setServiceMethodCallManner(serviceMethodCallManner);
    }
}
