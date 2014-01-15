package org.motechproject.mds.repository;

import org.springframework.transaction.annotation.Transactional;

import javax.jdo.Query;
import java.util.Collection;
import java.util.List;

/**
 * This is a basic repository class with CRUD operations. Mainly it is used as super class to create
 * a repository related with the given entity schema in
 * {@link org.motechproject.mds.builder.EntityInfrastructureBuilder} but it can be also used by
 * other repositories inside this package.
 *
 * @param <T> the type of entity schema.
 */
public abstract class MotechDataRepository<T> extends BaseMdsRepository {
    private Class<T> type;

    public MotechDataRepository(Class<T> type) {
        this.type = type;
    }

    @Transactional
    public T create(T object) {
        return getPersistenceManager().makePersistent(object);
    }

    @Transactional
    public T retrieve(String primaryKeyName, Object value) {
        Query query = getPersistenceManager().newQuery(type);
        query.setFilter(String.format("%s == param", primaryKeyName));
        query.declareParameters(String.format("%s param", value.getClass().getName()));
        query.setUnique(true);

        return type.cast(query.execute(value));
    }

    @Transactional
    public List<T> retrieveAll() {
        Query query = getPersistenceManager().newQuery(type);
        Collection collection = (Collection) query.execute();

        return cast(type, collection);
    }

    @Transactional
    public T update(T object) {
        return getPersistenceManager().makePersistent(object);
    }

    @Transactional
    public void delete(T object) {
        getPersistenceManager().deletePersistent(object);
    }

    @Transactional
    public void delete(String primaryKeyName, Object value) {
        delete(retrieve(primaryKeyName, value));
    }

}
