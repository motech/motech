package org.motechproject.commons.couchdb.dao;

import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

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
