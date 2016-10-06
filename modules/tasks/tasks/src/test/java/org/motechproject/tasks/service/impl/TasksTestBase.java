package org.motechproject.tasks.service.impl;

import org.joda.time.LocalDate;
import org.motechproject.commons.api.TasksEventParser;
import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.domain.mds.channel.ActionEvent;
import org.motechproject.tasks.domain.mds.channel.ActionParameter;
import org.motechproject.tasks.domain.mds.channel.EventParameter;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;
import org.motechproject.tasks.domain.mds.channel.builder.ActionEventBuilder;
import org.motechproject.tasks.domain.mds.channel.builder.ActionParameterBuilder;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.TaskActivity;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.service.SampleTasksEventParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.motechproject.tasks.domain.enums.ParameterType.DATE;
import static org.motechproject.tasks.domain.enums.ParameterType.INTEGER;
import static org.motechproject.tasks.domain.enums.ParameterType.LIST;
import static org.motechproject.tasks.domain.enums.ParameterType.MAP;
import static org.motechproject.tasks.domain.enums.ParameterType.TEXTAREA;
import static org.motechproject.tasks.domain.enums.TaskActivityType.ERROR;

public abstract class TasksTestBase {

    protected static final String TRIGGER_SUBJECT = "APPOINTMENT_CREATE_EVENT_SUBJECT";
    protected static final String ACTION_SUBJECT = "SEND_SMS";
    protected static final String TASK_DATA_PROVIDER_NAME = "12345L";
    protected static final Long TASK_ACTIVITY_ID = 11L;

    protected Task task;
    protected List<Task> tasks = new ArrayList<>(1);

    protected List<TaskActivity> taskActivities;
    protected TriggerEvent triggerEvent;
    protected ActionEvent actionEvent;

    protected void initTask() throws Exception {
        Map<String, String> actionValues = new HashMap<>();
        actionValues.put("phone", "123456");
        actionValues.put("message", "Hello {{trigger.externalId}}, You have an appointment on {{trigger.startDate}}");

        TaskTriggerInformation trigger = new TaskTriggerInformation("appointments", "Appointments", "appointments-bundle", "0.15", TRIGGER_SUBJECT, TRIGGER_SUBJECT);
        TaskActionInformation action = new TaskActionInformation("sms", "SMS", "sms-bundle", "0.15", ACTION_SUBJECT, actionValues);

        task = new Task();
        task.setName("name");
        task.setTrigger(trigger);
        task.addAction(action);
        task.setId(9l);
        task.setHasRegisteredChannel(true);
        tasks.add(task);
    }

    protected void setTriggerEvent() {
        List<EventParameter> triggerEventParameters = new ArrayList<>();
        triggerEventParameters.add(new EventParameter("ExternalID", "externalId"));
        triggerEventParameters.add(new EventParameter("StartDate", "startDate", DATE));
        triggerEventParameters.add(new EventParameter("EndDate", "endDate", DATE));
        triggerEventParameters.add(new EventParameter("FacilityId", "facilityId"));
        triggerEventParameters.add(new EventParameter("EventName", "eventName"));
        triggerEventParameters.add(new EventParameter("List", "list", LIST));
        triggerEventParameters.add(new EventParameter("Map", "map", MAP));

        triggerEvent = new TriggerEvent();
        triggerEvent.setSubject(TRIGGER_SUBJECT);
        triggerEvent.setEventParameters(triggerEventParameters);
    }

    protected void setActionEvent() {
        SortedSet<ActionParameter> actionEventParameters = new TreeSet<>();

        actionEventParameters.add(new ActionParameterBuilder().setDisplayName("Phone").setKey("phone")
                .setType(INTEGER).setOrder(0).build());

        actionEventParameters.add(new ActionParameterBuilder().setDisplayName("Message").setKey("message")
                .setType(TEXTAREA).setOrder(1).build());

        actionEvent = new ActionEventBuilder().build();
        actionEvent.setSubject(ACTION_SUBJECT);
        actionEvent.setActionParameters(actionEventParameters);
    }

    protected void setTaskActivities() {
        taskActivities = new ArrayList<>(5);
        taskActivities.add(new TaskActivity("Error1", task.getId(), ERROR));
        taskActivities.add(new TaskActivity("Error2", task.getId(), ERROR));
        taskActivities.add(new TaskActivity("Error3", task.getId(), ERROR));
        taskActivities.add(new TaskActivity("Error4", task.getId(), ERROR));
    }

    protected MotechEvent createEvent() {
        return createEvent(false);
    }

    protected MotechEvent createEvent(boolean withCustomParser) {
        Map<String, Object> param = createEventParameters();

        if (withCustomParser) {
            param.put(TasksEventParser.CUSTOM_PARSER_EVENT_KEY, SampleTasksEventParser.PARSER_NAME);
        }

        return new MotechEvent(TRIGGER_SUBJECT, param);
    }

    protected Map<String, Object> createEventParameters() {
        Map<String, Object> param = new HashMap<>(4);
        param.put("externalId", 123456789);
        param.put("startDate", new LocalDate(2012, 11, 20));
        param.put("map", new HashMap<>(param));
        param.put("endDate", new LocalDate(2012, 11, 29));
        param.put("facilityId", 987654321);
        param.put("eventName", "event name");
        param.put("list", asList(1, 2, 3));
        param.put("format", "%s || %s || %s");

        return param;
    }

    protected static <T> List<T> asList(T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }
}
