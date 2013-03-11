package org.motechproject.tasks.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ActionEventTest {

    @Test
    public void shouldAcceptTaskActionInformationWithSameService() {
        String serviceInterface = "interface";
        String serviceMethod = "method";

        TaskActionInformation information = new TaskActionInformation(null, null, null, serviceInterface, serviceMethod);
        ActionEvent actionEvent = new ActionEvent(null, null, serviceInterface, serviceMethod, null);

        assertTrue(actionEvent.accept(information));
    }

    @Test
    public void shouldNotAcceptTaskActionInformationWithDifferentServiceInterface() {
        String serviceInterface = "interface";
        String serviceMethod = "method";

        TaskActionInformation information = new TaskActionInformation();
        information.setServiceInterface(serviceInterface);
        information.setServiceMethod(serviceMethod);

        ActionEvent actionEvent = new ActionEvent();
        actionEvent.setServiceInterface("abc");
        actionEvent.setServiceMethod(serviceMethod);

        assertFalse(actionEvent.accept(information));
    }

    @Test
    public void shouldNotAcceptTaskActionInformationWithDifferentServiceMethod() {
        String serviceInterface = "interface";
        String serviceMethod = "method";

        TaskActionInformation information = new TaskActionInformation();
        information.setServiceInterface(serviceInterface);
        information.setServiceMethod(serviceMethod);

        ActionEvent actionEvent = new ActionEvent();
        actionEvent.setServiceInterface(serviceInterface);
        actionEvent.setServiceMethod("abc");

        assertFalse(actionEvent.accept(information));
    }

    @Test
    public void shouldNotAcceptWhenActionEventNotContainService() {
        String serviceInterface = "interface";
        String serviceMethod = "method";

        TaskActionInformation information = new TaskActionInformation();
        information.setServiceInterface(serviceInterface);
        information.setServiceMethod(serviceMethod);

        ActionEvent actionEvent = new ActionEvent();
        actionEvent.setSubject("subject");

        assertFalse(actionEvent.accept(information));
    }

    @Test
    public void shouldAcceptTaskActionInformationWithSameSubject() {
        String subject = "subject";

        TaskActionInformation information = new TaskActionInformation(null, null, null, subject);
        ActionEvent actionEvent = new ActionEvent(null, subject, null, null);

        assertTrue(actionEvent.accept(information));
    }

    @Test
    public void shouldNotAcceptTaskActionInformationWithDifferentSubject() {
        String subject = "subject";

        TaskActionInformation information = new TaskActionInformation();
        information.setSubject(subject);

        ActionEvent actionEvent = new ActionEvent();
        actionEvent.setSubject("abc");

        assertFalse(actionEvent.accept(information));
    }

    @Test
    public void shouldNotAcceptWhenActionEventNotContainSubject() {
        String serviceInterface = "interface";
        String serviceMethod = "method";
        String subject = "subject";

        TaskActionInformation information = new TaskActionInformation();
        information.setSubject(subject);

        ActionEvent actionEvent = new ActionEvent();
        actionEvent.setServiceInterface(serviceInterface);
        actionEvent.setServiceMethod(serviceMethod);

        assertFalse(actionEvent.accept(information));
    }
}
