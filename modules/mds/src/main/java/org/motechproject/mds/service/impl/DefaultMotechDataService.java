package org.motechproject.mds.service.impl;

import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This is a basic implementation of {@link org.motechproject.mds.service.MotechDataService}. Mainly
 * it is used as super class to create a service related with the given entity schema in
 * {@link org.motechproject.mds.builder.EntityInfrastructureBuilder} but it can be also used by
 * other services inside this package.
 *
 * @param <T> the type of entity schema.
 */
@Service
public abstract class DefaultMotechDataService<T> implements MotechDataService<T> {
    private MotechDataRepository<T> repository;

    @Override
    @Transactional
    public T create(T object) {
        return repository.create(object);
    }

    @Override
    @Transactional
    public T retrieve(String primaryKeyName, Object value) {
        return repository.retrieve(primaryKeyName, value);
    }

    @Override
    @Transactional
    public List<T> retrieveAll() {
        return repository.retrieveAll();
    }

    @Transactional
    protected List<T> retrieveAll(String[] parameters, Object[] values) {
        return repository.retrieveAll(parameters, values);
    }

    @Transactional
    protected List<T> retrieveAll(String[] parameters, Object[] values, QueryParams queryParams) {
        return repository.retrieveAll(parameters, values, queryParams);
    }

    @Transactional
    protected long count(String[] parameters, Object[] values) {
        return repository.count(parameters, values);
    }

    @Override
    @Transactional
    public List<T> retrieveAll(QueryParams queryParams) {
        return repository.retrieveAll(queryParams);
    }

    @Override
    @Transactional
    public T update(T object) {
        return repository.update(object);
    }

    @Override
    @Transactional
    public void delete(T object) {
        repository.delete(object);
    }

    @Override
    @Transactional
    public void delete(String primaryKeyName, Object value) {
        repository.delete(primaryKeyName, value);
    }

    @Override
    @Transactional
    public long count() {
        return repository.count();
    }

    @Autowired
    public void setRepository(MotechDataRepository<T> repository) {
        this.repository = repository;
    }
}
