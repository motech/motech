package org.motechproject.tasks.service.impl;

import com.google.gson.reflect.TypeToken;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.repository.AllTaskDataProviders;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.tasks.validation.TaskDataProviderValidator;
import org.motechproject.tasks.validation.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

@Service("taskDataProviderService")
public class TaskDataProviderServiceImpl implements TaskDataProviderService {
    private AllTaskDataProviders allTaskDataProviders;
    private MotechJsonReader motechJsonReader;

    @Autowired
    public TaskDataProviderServiceImpl(AllTaskDataProviders allTaskDataProviders) {
        this(allTaskDataProviders, new MotechJsonReader());
    }

    public TaskDataProviderServiceImpl(AllTaskDataProviders allTaskDataProviders, MotechJsonReader motechJsonReader) {
        this.allTaskDataProviders = allTaskDataProviders;
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
        Type type = new TypeToken<TaskDataProvider>() {}.getType();
        TaskDataProvider provider = (TaskDataProvider) motechJsonReader.readFromStream(stream, type);

        ValidationResult result = TaskDataProviderValidator.validate(provider);

        if (!result.isValid()) {
            throw new ValidationException(result.getTaskErrors());
        }

        allTaskDataProviders.addOrUpdate(provider);

        return getProvider(provider.getName());
    }

    @Override
    public TaskDataProvider getProvider(String name) {
        return allTaskDataProviders.byName(name);
    }

    @Override
    public List<TaskDataProvider> getProviders() {
        return allTaskDataProviders.getAll();
    }

}
