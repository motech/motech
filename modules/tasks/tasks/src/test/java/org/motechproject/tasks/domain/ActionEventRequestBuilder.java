package org.motechproject.tasks.domain;

import org.motechproject.tasks.service.ActionEventRequest;
import org.motechproject.tasks.service.ActionParameterRequest;

import java.util.SortedSet;
import java.util.TreeSet;

public class ActionEventRequestBuilder {

    private static final String EMPTY = "";
    private String displayName = "displayName";
    private String subject = "subject";
    private String description = "description";
    private String serviceInterface = "service.interface";
    private String serviceMethod = "service.method";
    private SortedSet<ActionParameterRequest> actionParameters = new TreeSet<>();


    public ActionEventRequestBuilder withSubject(String subject) {
        this.subject = subject;
        return this;
    }


    public ActionEventRequestBuilder withEmptySubject() {
        this.subject = EMPTY;
        return this;
    }

    public ActionEventRequestBuilder withoutSubject() {
        this.subject = null;
        return this;
    }

    public ActionEventRequestBuilder withServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
        return this;
    }

    public ActionEventRequestBuilder withEmptyServiceInterface() {
        this.subject = EMPTY;
        return this;
    }


    public ActionEventRequestBuilder withoutServiceInterface() {
        this.serviceInterface = null;
        return this;
    }


    public ActionEventRequestBuilder withServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
        return this;
    }

    public ActionEventRequestBuilder withEmptyServiceMethod() {
        this.serviceMethod = EMPTY;
        return this;
    }


    public ActionEventRequestBuilder withoutServiceMethod() {
        this.serviceMethod = null;
        return this;
    }


    public ActionEventRequest build() {
        return new ActionEventRequest(displayName, subject, description, serviceInterface, serviceMethod);
    }


}
