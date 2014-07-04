package org.motechproject.tasks.service.impl;

import com.google.gson.reflect.TypeToken;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.motechproject.tasks.events.constants.EventDataKeys.DATA_PROVIDER_NAME;
import static org.motechproject.tasks.events.constants.EventSubjects.DATA_PROVIDER_UPDATE_SUBJECT;

@Service("taskDataProviderService")
public class TaskDataProviderServiceImpl implements TaskDataProviderService {
    private DataProviderDataService dataProviderDataService;
    private MotechJsonReader motechJsonReader;
    private EventRelay eventRelay;

    @Autowired
    public TaskDataProviderServiceImpl(DataProviderDataService allTaskDataProviders, EventRelay eventRelay) {
        this(allTaskDataProviders, eventRelay, new MotechJsonReader());
    }

    public TaskDataProviderServiceImpl(DataProviderDataService dataProviderDataService, EventRelay eventRelay, MotechJsonReader motechJsonReader) {
        this.dataProviderDataService = dataProviderDataService;
        this.eventRelay = eventRelay;
        this.motechJsonReader = motechJsonReader;

    }

    @Override
    public TaskDataProvider registerProvider(final String body) {
        byte[] bytes = body.getBytes(Charset.forName("UTF-8"));
        InputStream stream = new ByteArrayInputStream(bytes);

        return registerProvider(stream);
    }

    @Override
    public TaskDataProvider registerProvider(final InputStream stream) {
        final Type type = new TypeToken<TaskDataProvider>() {} .getType();
        final TaskDataProvider provider = (TaskDataProvider) motechJsonReader.readFromStream(stream, type);

        Set<TaskError> errors = TaskDataProviderValidator.validate(provider);

        if (!isEmpty(errors)) {
            throw new ValidationException(TaskDataProviderValidator.TASK_DATA_PROVIDER, errors);
        }

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

        return getProvider(provider.getName());
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

}
