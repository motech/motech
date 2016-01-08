package org.motechproject.tasks.domain;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;

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

    @Test
    public void shouldRecognizeDifferentSetsOfActionParameters(){

        ActionEvent ae1 = new ActionEvent();
        ActionEvent ae2 = new ActionEvent();
        ActionEvent ae3 = new ActionEvent();

        SortedSet<ActionParameter> ap1 = new TreeSet<ActionParameter>();
        SortedSet<ActionParameter> ap2 = new TreeSet<ActionParameter>();

        ActionParameter ap11 = new ActionParameter();
        ap11.setOrder(1);
        ap11.setKey("A");
        ap11.setValue("A");

        ActionParameter ap12 = new ActionParameter();
        ap12.setOrder(2);
        ap12.setKey("B");
        ap12.setValue("B");

        ActionParameter ap13 = new ActionParameter();
        ap13.setOrder(3);
        ap13.setKey("C");
        ap13.setValue("C");

        ap1.add(ap11);
        ap1.add(ap12);
        ap1.add(ap13);

        ActionParameter ap21 = new ActionParameter();
        ap21.setOrder(3);
        ap21.setKey("A");
        ap21.setValue("A");

        ActionParameter ap22 = new ActionParameter();
        ap22.setOrder(1);
        ap22.setKey("B");
        ap22.setValue("B");

        ActionParameter ap23 = new ActionParameter();
        ap23.setOrder(2);
        ap23.setKey("C");
        ap23.setValue("C");

        ap2.add(ap21);
        ap2.add(ap22);
        ap2.add(ap23);

        ae1.setActionParameters(ap1);
        ae2.setActionParameters(ap2);
        ae3.setActionParameters(ap1);

        assertFalse(ae1.equals(ae2));
        assertFalse(ae2.equals(ae1));
        assertTrue(ae3.equals(ae1));
    }
}
