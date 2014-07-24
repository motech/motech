package org.motechproject.tasks.service.impl;

import com.google.gson.reflect.TypeToken;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.domain.TaskError;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.service.DataProviderDataService;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.tasks.validation.TaskDataProviderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

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
import static org.motechproject.tasks.events.constants.EventDataKeys.DATA_PROVIDER_NAME;
import static org.motechproject.tasks.events.constants.EventSubjects.DATA_PROVIDER_UPDATE_SUBJECT;

@Service("taskDataProviderService")
public class TaskDataProviderServiceImpl implements TaskDataProviderService, OsgiServiceLifecycleListener {

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
    public void registerProvider(final String body) {
        byte[] bytes = body.getBytes(Charset.forName("UTF-8"));
        InputStream stream = new ByteArrayInputStream(bytes);

        registerProvider(stream);
    }

    @Override
    public void registerProvider(final InputStream stream) {
        final Type type = new TypeToken<TaskDataProvider>() {} .getType();
        final TaskDataProvider provider = (TaskDataProvider) motechJsonReader.readFromStream(stream, type);

        Set<TaskError> errors = TaskDataProviderValidator.validate(provider);

        if (!isEmpty(errors)) {
            throw new ValidationException(TaskDataProviderValidator.TASK_DATA_PROVIDER, errors);
        }

        addProvider(provider);
    }

    @Override
    public TaskDataProvider getProvider(String name) {
        return dataProviderDataService.findByName(name);
    }

    @Override
    public TaskDataProvider getProviderById(Long providerId) {
        return dataProviderDataService.findById(providerId);
    }

    @Override
    public List<TaskDataProvider> getProviders() {
        return dataProviderDataService.retrieveAll();
    }

    @Override
    public void bind(Object service, Map properties) {
        dataProviderDataService = (DataProviderDataService) service;

        // add providers from queue
        synchronized (additionLock) {
            for (TaskDataProvider provider : providersToAdd) {
                addProviderImpl(provider);
            }
            providersToAdd.clear();
        }
    }

    @Override
    public void unbind(Object service, Map properties) {
        dataProviderDataService = null;
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

            if (existing != null) {
                dataProviderDataService.doInTransaction(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        existing.setObjects(provider.getObjects());
                        dataProviderDataService.update(existing);
                    }
                });

                Map<String, Object> parameters = new HashMap<>();
                parameters.put(DATA_PROVIDER_NAME, provider.getName());

                eventRelay.sendEventMessage(new MotechEvent(DATA_PROVIDER_UPDATE_SUBJECT, parameters));
            } else {
                dataProviderDataService.create(provider);
            }
        } else {
            // store for later addition
            providersToAdd.add(provider);
        }
    }
}
