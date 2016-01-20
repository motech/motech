package org.motechproject.tasks.service;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.event.utils.MotechProxyUtils;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.events.constants.EventDataKeys;
import org.motechproject.tasks.events.constants.EventSubjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class TaskActionRetryHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskActionRetryHandler.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private EventListenerRegistryService registryService;

    @MotechListener(subjects = {EventSubjects.ACTION_RETRY_HANDLER})
    public void handleRetries(MotechEvent event) throws InterruptedException {
        Map<String, Object> params = event.getParameters();
        Long taskId = (Long) params.get(EventDataKeys.TASK_ID);
        Task task = taskService.getTask(taskId);
        int numberOfRetries = task.getNumberOfRetries();
        int retryIntervalTime = task.getRetryIntervalInMilliseconds();

        if (numberOfRetries == 0) {
            LOGGER.info("Failed action {} not retry, because number of retries for task {} is 0", event.getSubject(), task.getName());
            throw event.getExceptionFromListener();
        }

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Set<EventListener> listeners = registryService.getListeners(event.getSubject());
            EventListener listenerForAction = null;
            for (EventListener listener : listeners) {
                if (listener.getIdentifier().equals(event.getMessageDestination())) {
                    listenerForAction =  listener;
                    break;
                }
            }

            if (listenerForAction == null) {
                LOGGER.warn("Event listener with identifier {} not present to handle the event: {}", event.getMessageDestination(), event);
            } else {
                Object target = MotechProxyUtils.getTargetIfProxied(listenerForAction);
                Thread.currentThread().setContextClassLoader(target.getClass().getClassLoader());

                boolean success;
                for (int i = 1; i <= numberOfRetries; i++) {
                    success = true;
                    try {
                        listenerForAction.handle(event);
                    } catch (RuntimeException ex) {
                        success = false;
                        if (i == numberOfRetries) {
                            LOGGER.info("Max retry count = {} reached for action {}", numberOfRetries, event.getSubject());
                            throw ex;
                        } else if (retryIntervalTime != 0) {
                            Thread.sleep(retryIntervalTime);
                        }
                    }

                    if (success) {
                        break;
                    }
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setRegistryService(EventListenerRegistryService registryService) {
        this.registryService = registryService;
    }
}