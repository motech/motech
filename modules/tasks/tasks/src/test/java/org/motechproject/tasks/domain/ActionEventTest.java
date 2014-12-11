package org.motechproject.tasks.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ActionEventTest {

    @Test
    public void shouldAcceptTaskActionInformationWithSameService() {
        String serviceInterface = "interface";
        String serviceMethod = "method";

        TaskActionInformation information = new TaskActionInformation(null, null, null, null, serviceInterface, serviceMethod);
        ActionEvent actionEvent = new ActionEventBuilder().setDisplayName(null).setDescription(null)
                .setServiceInterface(serviceInterface).setServiceMethod(serviceMethod).setActionParameters(null)
                .createActionEvent();

        assertTrue(actionEvent.accept(information));
    }

    @Test
    public void shouldNotAcceptTaskActionInformationWithDifferentServiceInterface() {
        String serviceInterface = "interface";
        String serviceMethod = "method";

        TaskActionInformation information = new TaskActionInformation();
        information.setServiceInterface(serviceInterface);
        information.setServiceMethod(serviceMethod);

        ActionEvent actionEvent = new ActionEventBuilder().createActionEvent();
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

        ActionEvent actionEvent = new ActionEventBuilder().createActionEvent();
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

        ActionEvent actionEvent = new ActionEventBuilder().createActionEvent();
        actionEvent.setSubject("subject");

        assertFalse(actionEvent.accept(information));
    }

    @Test
    public void shouldAcceptTaskActionInformationWithSameSubject() {
        String subject = "subject";

        TaskActionInformation information = new TaskActionInformation(null, null, null, null, subject);
        ActionEvent actionEvent = new ActionEventBuilder().setDisplayName(null).setSubject(subject).setDescription(null)
                .setActionParameters(null).createActionEvent();

        assertTrue(actionEvent.accept(information));
    }

    @Test
    public void shouldNotAcceptTaskActionInformationWithDifferentSubject() {
        String subject = "subject";

        TaskActionInformation information = new TaskActionInformation();
        information.setSubject(subject);

        ActionEvent actionEvent = new ActionEventBuilder().createActionEvent();
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

        ActionEvent actionEvent = new ActionEventBuilder().createActionEvent();
        actionEvent.setServiceInterface(serviceInterface);
        actionEvent.setServiceMethod(serviceMethod);

        assertFalse(actionEvent.accept(information));
    }
}
