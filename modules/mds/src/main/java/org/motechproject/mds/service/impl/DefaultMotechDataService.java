package org.motechproject.mds.service.impl;

import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

/**
 * This is a basic implementation of {@link org.motechproject.mds.service.MotechDataService}. Mainly
 * it is used as super class to create a service related with the given entity schema in
 * {@link org.motechproject.mds.builder.EntityInfrastructureBuilder} but it can be also used by
 * other services inside this package.
 *
 * @param <T> the type of entity schema.
 */
public abstract class DefaultMotechDataService<T> implements MotechDataService<T> {
    private MotechDataRepository<T> repository;

    @Override
    public T create(T object) {
        return repository.create(object);
    }

    @Override
    public T retrieve(String primaryKeyName, Object value) {
        return repository.retrieve(primaryKeyName, value);
    }

    @Override
    public List<T> retrieveAll() {
        return repository.retrieveAll();
    }

    @Override
    public T update(T object) {
        return repository.update(object);
    }

    @Override
    public void delete(T object) {
        repository.delete(object);
    }

    @Override
    public void delete(String primaryKeyName, Object value) {
        repository.delete(primaryKeyName, value);
    }

    public void setRepository(MotechDataRepository<T> repository) {
        this.repository = repository;
    }
}
