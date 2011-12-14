package org.motechproject.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.motechproject.model.MotechBaseDataObject;

import java.util.List;

public abstract class MotechBaseRepository<T extends MotechBaseDataObject> extends CouchDbRepositorySupport<T>{
    protected MotechBaseRepository(Class<T> type, CouchDbConnector db) {
        super(type, db);
    }

    public void addOrReplace(T entity, String businessFieldName, String businessId) {
        List<T> entities = queryView(String.format("by_%s", businessFieldName), businessId);
        if (entities.size() == 0) add(entity);
        else if (entities.size() == 1) update(entity);
        else throw new BusinessIdNotUniqueException(businessFieldName, businessId);
    }

    public void safeRemove(T entity) {
        if (contains(entity.getId()))
            remove(entity);
    }
}
