package org.motechproject.tasks.service;

import org.motechproject.commons.api.MotechException;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.KeyInformation;
import org.motechproject.tasks.domain.ParameterType;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TaskHandlerException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import static org.motechproject.tasks.domain.ParameterType.LIST;
import static org.motechproject.tasks.events.constants.TaskFailureCause.ACTION;
import static org.motechproject.tasks.events.constants.TaskFailureCause.TRIGGER;

/**
 * Builds action parameters from  {@link TaskContext} and executes the action by invoking its service or raising its event.
 */
class TaskActionExecutor {
    private BundleContext bundleContext;
    private EventRelay eventRelay;

    private TaskService taskService;
    private TaskActivityService activityService;
    private KeyEvaluator keyEvaluator;

    TaskActionExecutor(TaskService taskService, TaskActivityService activityService,
                       EventRelay eventRelay) {
        this.eventRelay = eventRelay;
        this.taskService = taskService;
        this.activityService = activityService;
    }

    void execute(Task task, TaskActionInformation actionInformation, TaskContext taskContext) throws TaskHandlerException {
        this.keyEvaluator = new KeyEvaluator(taskContext);
        ActionEvent action = getActionEvent(actionInformation);
        Map<String, Object> parameters = createParameters(actionInformation, action);

        if (action.hasService() && bundleContext != null) {
            if (callActionServiceMethod(action, parameters)) {
                return;
            }
            activityService.addWarning(task, "task.warning.serviceUnavailable", action.getServiceInterface());
        }
        if (!action.hasSubject()) {
            throw new TaskHandlerException(ACTION, "task.error.cantExecuteAction");
        }
        eventRelay.sendEventMessage(new MotechEvent(action.getSubject(), parameters));
    }

    private ActionEvent getActionEvent(TaskActionInformation actionInformation)
            throws TaskHandlerException {
        ActionEvent action;

        try {
            action = taskService.getActionEventFor(actionInformation);
        } catch (ActionNotFoundException e) {
            throw new TaskHandlerException(TRIGGER, "task.error.actionNotFound", e);
        }

        return action;
    }

    Map<String, Object> createParameters(TaskActionInformation info,
                                         ActionEvent action) throws TaskHandlerException {
        SortedSet<ActionParameter> actionParameters = action.getActionParameters();
        Map<String, Object> parameters = new HashMap<>(actionParameters.size());

        for (ActionParameter actionParameter : actionParameters) {
            String key = actionParameter.getKey();

            if (info.getValues().containsKey(key)) {
                String template = info.getValues().get(key);

                if (template == null) {
                    throw new TaskHandlerException(
                        TRIGGER, "task.error.templateNull", key, action.getDisplayName()
                    );
                }

                switch (actionParameter.getType()) {
                    case LIST:
                        parameters.put(key, convertToList((List<String>) LIST.parse(template)));
                        break;
                    case MAP:
                        parameters.put(key, convertToMap(template));
                        break;
                    default:
                        try {
                            String userInput = keyEvaluator.evaluateTemplateString(template);
                            Object obj = actionParameter.getType().parse(userInput);
                            parameters.put(key, obj);
                        } catch (MotechException ex) {
                            throw new TaskHandlerException(TRIGGER, ex.getMessage(), ex, key);
                        }
                }
            } else {
                if (actionParameter.isRequired()) {
                    throw new TaskHandlerException(
                        TRIGGER, "task.error.taskActionNotContainsField",
                        action.getDisplayName(), key
                    );
                } else {
                    parameters.put(key, null);
                }
            }
        }

        return parameters;
    }

    private Map<Object, Object> convertToMap(String template) throws TaskHandlerException {
        String[] rows = template.split("(\\r)?\\n");
        Map<Object, Object> tempMap = new HashMap<>(rows.length);

        for (String row : rows) {
            String[] array = row.split(":", 2);
            Object mapKey;
            Object mapValue;

            switch (array.length) {
                case 2:
                    array[1] = array[1].trim();
                    mapKey = getValue(array[0]);
                    mapValue = getValue(array[1]);

                    tempMap.put(
                        ParameterType.getType(mapKey.getClass()).parse(keyEvaluator.evaluateTemplateString(array[0])),
                        ParameterType.getType(mapValue.getClass()).parse(keyEvaluator.evaluateTemplateString(array[1]))
                    );
                    break;
                case 1:
                    mapValue = getValue(array[0]);

                    tempMap.putAll((Map) mapValue);
                    break;
                default:
            }
        }
        return tempMap;
    }

    private List<Object> convertToList(List<String> templates) throws TaskHandlerException {
        List<Object> tempList = new ArrayList<>();

        for (String template : templates) {
            Object value = getValue(template.trim());

            if (value instanceof Collection) {
                tempList.addAll((Collection) value);
            } else {
                tempList.add(ParameterType.getType(value.getClass()).parse(keyEvaluator.evaluateTemplateString(template)));
            }
        }

        return tempList;
    }

    private Object getValue(String row) throws TaskHandlerException {
        List<KeyInformation> keys = KeyInformation.parseAll(row);

        Object result;
        if (keys.isEmpty()) {
            result = row;
        } else {
            KeyInformation rowKeyInfo = keys.get(0);
            result = keyEvaluator.getValue(rowKeyInfo);
        }

        return result;
    }

    private boolean callActionServiceMethod(ActionEvent action, Map<String, Object> parameters)
            throws TaskHandlerException {
        MethodHandler methodHandler = new MethodHandler(action, parameters);
        ServiceReference reference = bundleContext.getServiceReference(
                action.getServiceInterface()
        );
        boolean serviceAvailable = reference != null;

        if (serviceAvailable) {
            Object service = bundleContext.getService(reference);
            String serviceMethod = action.getServiceMethod();
            Class[] classes = methodHandler.isParametrized() ? methodHandler.getClasses() : null;
            Object[] objects = methodHandler.isParametrized() ? methodHandler.getObjects() : null;

            try {
                Method method = service.getClass().getMethod(serviceMethod, classes);

                try {
                    method.invoke(service, objects);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new TaskHandlerException(
                            ACTION, "task.error.serviceMethodInvokeError", e,
                            serviceMethod, action.getServiceInterface()
                    );
                }
            } catch (NoSuchMethodException e) {
                throw new TaskHandlerException(
                        ACTION, "task.error.notFoundMethodForService", e,
                        serviceMethod, action.getServiceInterface()
                );
            }
        }

        return serviceAvailable;
    }

    void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
