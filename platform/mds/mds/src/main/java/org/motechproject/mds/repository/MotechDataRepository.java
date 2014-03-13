package org.motechproject.mds.repository;

import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.mds.util.QueryParams;
import org.motechproject.mds.util.QueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is a basic repository class with standard CRUD operations. It should be used by other
 * repositories inside this package.
 * <p/>
 * This class is also used as super class to create a repository related with the given entity
 * schema in {@link org.motechproject.mds.builder.EntityInfrastructureBuilder}.
 *
 * @param <T> the type of class
 */
@Repository
public abstract class MotechDataRepository<T> {
    private PersistenceManagerFactory persistenceManagerFactory;
    private Class<T> classType;

    protected MotechDataRepository(Class<T> classType) {
        this.classType = classType;
    }

    public Class<T> getClassType() {
        return classType;
    }

    @Autowired
    @Qualifier("persistenceManagerFactory")
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    public PersistenceManager getPersistenceManager() {
        return null != persistenceManagerFactory
                ? persistenceManagerFactory.getPersistenceManager()
                : null;
    }

    public T retrieve(Object key) {
        return getPersistenceManager().getObjectById(classType, key);
    }

    public T create(T object) {
        return getPersistenceManager().makePersistent(object);
    }

    public List<T> retrieveAll() {
        return retrieveAll(new String[0], new Object[0], null);
    }

    public List<T> retrieveAll(InstanceSecurityRestriction restriction) {
        return retrieveAll(new String[0], new Object[0], restriction);
    }

    public List<T> retrieveAll(String property, Object value) {
        return retrieveAll(new String[]{property}, new Object[]{value}, null);
    }

    public List<T> retrieveAll(String property, Object value, InstanceSecurityRestriction restriction) {
        return retrieveAll(new String[]{property}, new Object[]{value}, restriction);
    }

    public List<T> retrieveAll(String[] properties, Object[] values, InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, values, restriction);
        Collection collection = (Collection) QueryUtil.executeWithArray(query, values, restriction);

        return cast(collection);
    }

    public List<T> retrieveAll(String[] properties, Object[] values, QueryParams queryParams,
                               InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, values, restriction);
        QueryUtil.setQueryParams(query, queryParams);

        Collection collection = (Collection) QueryUtil.executeWithArray(query, values, restriction);

        return cast(collection);
    }

    public List<T> retrieveAll(QueryParams queryParams, InstanceSecurityRestriction restriction) {
        Query query = createQuery(new String[0], new Object[0], restriction);
        QueryUtil.setQueryParams(query, queryParams);
        Collection collection = (Collection) QueryUtil.execute(query, restriction);

        return cast(collection);
    }

    public T retrieve(String property, Object value) {
        return retrieve(new String[]{property}, new Object[]{value}, null);
    }

    public T retrieve(String property, Object value, InstanceSecurityRestriction restriction) {
        return retrieve(new String[]{property}, new Object[]{value}, restriction);
    }

    public T retrieve(String[] properties, Object[] values) {
        return retrieve(properties, values, null);
    }

    public T retrieve(String[] properties, Object[] values, InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, values, restriction);
        query.setUnique(true);

        return (T) QueryUtil.executeWithArray(query, values, restriction);
    }

    public boolean exists(String property, Object value) {
        return exists(new String[]{property}, new Object[]{value});
    }

    public boolean exists(String[] properties, Object[] values) {
        return retrieve(properties, values) != null;
    }

    public T update(T object) {
        return getPersistenceManager().makePersistent(object);
    }

    public void delete(T object) {
        getPersistenceManager().deletePersistent(object);
    }

    public void delete(String property, Object value, InstanceSecurityRestriction restriction) {
        delete(new String[]{property}, new Object[]{value}, restriction);
    }

    public void delete(String[] properties, Object[] values, InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, values, restriction);
        query.setUnique(true);

        Object object = QueryUtil.executeWithArray(query, values, restriction);
        getPersistenceManager().deletePersistent(object);
    }

    public void deleteAll(String property, Object value) {
        deleteAll(new String[]{property}, new Object[]{value}, null);
    }

    public void deleteAll(String property, Object value, InstanceSecurityRestriction restriction) {
        deleteAll(new String[]{property}, new Object[]{value}, restriction);
    }

    public void deleteAll(String[] properties, Object[] values, InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, values, restriction);
        Collection collection = (Collection) QueryUtil.executeWithArray(query, values, restriction);

        getPersistenceManager().deletePersistentAll(collection);
    }

    public long count(InstanceSecurityRestriction restriction) {
        Query query = createQuery(new String[0], new Object[0], restriction);
        query.setResult("count(this)");
        return (long) QueryUtil.execute(query, restriction);
    }

    public long count(String[] properties, Object[] values, InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, values, restriction);
        query.setResult("count(this)");
        return (long) QueryUtil.executeWithArray(query, values, restriction);
    }

    private Query createQuery(String[] properties, Object[] values, InstanceSecurityRestriction restriction) {
        if (properties.length != values.length) {
            throw new IllegalArgumentException("properties length must equal to values length");
        }

        Query query = getPersistenceManager().newQuery(classType);
        query.setFilter(QueryUtil.createFilter(properties, restriction));
        query.declareParameters(QueryUtil.createDeclareParameters(values, restriction));

        return query;
    }

    /**
     * Converts the no generic collection into generic list with the given type.
     *
     * @param collection an instance of {@link java.util.Collection} that contains objects.
     * @return a instance of {@link java.util.List} that contains object that are the given type.
     */
    protected List<T> cast(Collection collection) {
        return new ArrayList<T>(collection);
    }
}
