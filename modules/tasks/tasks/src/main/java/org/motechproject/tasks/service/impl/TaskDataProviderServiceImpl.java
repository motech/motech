package org.motechproject.tasks.service.impl;

import com.google.gson.reflect.TypeToken;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.domain.TaskError;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.repository.AllTaskDataProviders;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.tasks.validation.TaskDataProviderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private AllTaskDataProviders allTaskDataProviders;
    private MotechJsonReader motechJsonReader;
    private EventRelay eventRelay;

    @Autowired
    public TaskDataProviderServiceImpl(AllTaskDataProviders allTaskDataProviders, EventRelay eventRelay) {
        this(allTaskDataProviders, eventRelay, new MotechJsonReader());
    }

    public TaskDataProviderServiceImpl(AllTaskDataProviders allTaskDataProviders, EventRelay eventRelay, MotechJsonReader motechJsonReader) {
        this.allTaskDataProviders = allTaskDataProviders;
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
        Type type = new TypeToken<TaskDataProvider>() {
        }.getType();
        TaskDataProvider provider = (TaskDataProvider) motechJsonReader.readFromStream(stream, type);

        Set<TaskError> errors = TaskDataProviderValidator.validate(provider);

        if (!isEmpty(errors)) {
            throw new ValidationException(TaskDataProviderValidator.TASK_DATA_PROVIDER, errors);
        }

        boolean update = allTaskDataProviders.addOrUpdate(provider);

        if (update) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(DATA_PROVIDER_NAME, provider.getName());

            eventRelay.sendEventMessage(new MotechEvent(DATA_PROVIDER_UPDATE_SUBJECT, parameters));
        }

        return getProvider(provider.getName());
    }

    @Override
    public TaskDataProvider getProvider(String name) {
        return allTaskDataProviders.byName(name);
    }

    @Override
    public TaskDataProvider getProviderById(String providerId) {
        return allTaskDataProviders.get(providerId);
    }

    @Override
    public List<TaskDataProvider> getProviders() {
        return allTaskDataProviders.getAll();
    }

}
