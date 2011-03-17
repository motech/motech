package org.motechproject.dao;

import org.motechproject.model.MotechAuditableDataObject;

import java.util.List;

/**
 *
 */
public interface BaseDao<T extends MotechAuditableDataObject> {

    public void add(T entity);

    public void update(T entity);

    public void remove(T entity);

    public T get(String id);

    public List<T> getAll();


}
