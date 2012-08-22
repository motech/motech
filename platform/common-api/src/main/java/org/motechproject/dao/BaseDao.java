package org.motechproject.dao;

import org.motechproject.model.MotechBaseDataObject;

import java.util.List;

/**
 *
 */
public interface BaseDao<T extends MotechBaseDataObject> {

    void add(T entity);

    void update(T entity);

    void remove(T entity);

    T get(String id);

    boolean contains(String id);

    List<T> getAll();

    void safeRemove(T entity);
}
