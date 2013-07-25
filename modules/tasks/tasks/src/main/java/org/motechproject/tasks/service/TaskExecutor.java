package org.motechproject.tasks.service;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TaskHandlerException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.motechproject.tasks.events.constants.TaskFailureCause.ACTION;
import static org.motechproject.tasks.events.constants.TaskFailureCause.TRIGGER;

class TaskExecutor {
    private BundleContext bundleContext;
    private EventRelay eventRelay;

    private TaskService taskService;
    private TaskActivityService activityService;

    TaskExecutor(TaskService taskService, TaskActivityService activityService,
                 EventRelay eventRelay) {
        this.eventRelay = eventRelay;
        this.taskService = taskService;
        this.activityService = activityService;
    }

    void execute(TaskInitializer initializer,
                 TaskActionInformation actionInformation) throws TaskHandlerException {
        ActionEvent action = getActionEvent(actionInformation);
        Map<String, Object> parameters = initializer.createParameters(actionInformation, action);
        boolean invokeMethod = action.hasService() && bundleContext != null;
        boolean serviceAvailable = false;

        if (invokeMethod) {
            serviceAvailable = callActionServiceMethod(action, parameters);

            if (!serviceAvailable) {
                activityService.addWarning(
                        initializer.getTask(),
                        "task.warning.serviceUnavailable",
                        action.getServiceInterface()
                );
            }
        }

        boolean sendEvent = (!invokeMethod || !serviceAvailable) && action.hasSubject();

        if (sendEvent) {
            eventRelay.sendEventMessage(new MotechEvent(action.getSubject(), parameters));
        }

        if ((!invokeMethod || !serviceAvailable) && !sendEvent) {
            throw new TaskHandlerException(ACTION, "task.error.cantExecuteAction");
        }
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
