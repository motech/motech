package org.motechproject.commons.couchdb.dao;

import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.List;

/**
 * @deprecated As of release 0.24, MDS replaces CouchDB for persistence
 */
@Deprecated
public interface BaseDao<T extends MotechBaseDataObject> {

    void add(T entity);

    void update(T entity);

    void remove(T entity);

    T get(String id);

    boolean contains(String id);

    List<T> getAll();

    void safeRemove(T entity);
}
