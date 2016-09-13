package org.motechproject.tasks.service.impl;

import com.google.gson.reflect.TypeToken;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.tasks.domain.mds.task.TaskDataProvider;
import org.motechproject.tasks.domain.mds.task.TaskError;
import org.motechproject.tasks.exception.ValidationException;
import org.motechproject.tasks.repository.DataProviderDataService;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.tasks.validation.TaskDataProviderValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.motechproject.tasks.constants.EventDataKeys.DATA_PROVIDER_NAME;
import static org.motechproject.tasks.constants.EventSubjects.DATA_PROVIDER_UPDATE_SUBJECT;

@Service("taskDataProviderService")
public class TaskDataProviderServiceImpl implements TaskDataProviderService, OsgiServiceLifecycleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskDataProviderServiceImpl.class);

    private DataProviderDataService dataProviderDataService;
    private Queue<TaskDataProvider> providersToAdd = new ArrayDeque<>();

    // we synchronize adding providers and emptying the queue of providers awaiting addition
    private final Object additionLock = new Object();

    private MotechJsonReader motechJsonReader;
    private EventRelay eventRelay;

    @Autowired
    public TaskDataProviderServiceImpl(EventRelay eventRelay) {
        this(eventRelay, new MotechJsonReader());
    }

    public TaskDataProviderServiceImpl(EventRelay eventRelay, MotechJsonReader motechJsonReader) {
        this.eventRelay = eventRelay;
        this.motechJsonReader = motechJsonReader;

    }

    @Override
    @Transactional
    public void registerProvider(final String body) {
        byte[] bytes = body.getBytes(Charset.forName("UTF-8"));
        InputStream stream = new ByteArrayInputStream(bytes);

        registerProvider(stream);
    }

    @Override
    @Transactional
    public void registerProvider(final InputStream stream) {
        final Type type = new TypeToken<TaskDataProvider>() { } .getType();
        final TaskDataProvider provider = (TaskDataProvider) motechJsonReader.readFromStream(stream, type);

        if(TaskDataProviderValidator.validateIsNotEmpty(provider)) {
            LOGGER.info("Registering a task data provider with name: {}", provider.getName());

            Set<TaskError> errors = TaskDataProviderValidator.validate(provider);

            if (!isEmpty(errors)) {
                throw new ValidationException(TaskDataProviderValidator.TASK_DATA_PROVIDER, TaskError.toDtos(errors));
            }

            addProvider(provider);
        } else {
            LOGGER.info("Registering an empty task data provider with name: {} is not possible", provider.getName());
        }
    }

    @Override
    @Transactional
    public TaskDataProvider getProvider(String name) {
        return dataProviderDataService.findByName(name);
    }

    @Override
    @Transactional
    public TaskDataProvider getProviderById(Long providerId) {
        return dataProviderDataService.findById(providerId);
    }

    @Override
    @Transactional
    public List<TaskDataProvider> getProviders() {
        return dataProviderDataService.retrieveAll();
    }

    @Override
    @Transactional
    public void bind(Object service, Map properties) {
        LOGGER.info("Data Service for task data providers registered, starting to register queued providers");

        dataProviderDataService = (DataProviderDataService) service;

        // add providers from queue
        LOGGER.debug("Adding the following task data providers: {}", providersToAdd);
        synchronized (additionLock) {
            for (TaskDataProvider provider : providersToAdd) {
                LOGGER.info("Registering a task data provider with name: {}", provider.getName());
                addProviderImpl(provider);
            }
            providersToAdd.clear();
        }
    }

    @Override
    public void unbind(Object service, Map properties) {
        dataProviderDataService = null;
    }

    @Override
    @Transactional
    public void unregister(String providerName) {
        TaskDataProvider provider = dataProviderDataService.findByName(providerName);
        if (provider != null) {
            dataProviderDataService.delete(provider);
        } else {
            LOGGER.info("A request to unregister the task data provider with name {} has been received, " +
                    "but the provider with such name does not exist.", providerName);
        }
    }

    private void addProvider(final TaskDataProvider provider) {
        synchronized (additionLock) {
            if (dataProviderDataService != null) {
                addProviderImpl(provider);
            }
        }
    }

    private void addProviderImpl(final TaskDataProvider provider) {
        // we add the provider only if the data provider service is available
        // we can't block while waiting for the service, since this is called in the bind method
        // if the service is not available, we add the provider to a queue, it will be added when the service
        // becomes available
        if (dataProviderDataService != null) {
            final TaskDataProvider existing = dataProviderDataService.findByName(provider.getName());

            // Only update data provider when there's actual change
            if (existing != null && !existing.equals(provider)) {
                LOGGER.debug("Updating a task data provider with name: {}", provider.getName());

                existing.setObjects(provider.getObjects());
                dataProviderDataService.update(existing);

                Map<String, Object> parameters = new HashMap<>();
                parameters.put(DATA_PROVIDER_NAME, provider.getName());

                eventRelay.sendEventMessage(new MotechEvent(DATA_PROVIDER_UPDATE_SUBJECT, parameters));
            } else if (existing == null) {
                LOGGER.debug("Creating a task data provider with name: {}", provider.getName());
                dataProviderDataService.create(provider);
            }
        } else {
            LOGGER.debug("DataProviderDataService is not available, storing a task data provider with name: {} for later addition", provider.getName());
            // store for later addition
            providersToAdd.add(provider);
        }
    }
}
