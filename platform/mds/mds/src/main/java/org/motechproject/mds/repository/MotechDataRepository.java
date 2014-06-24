package org.motechproject.mds.repository;

import org.motechproject.mds.filter.Filter;
import org.motechproject.mds.query.Property;
import org.motechproject.mds.query.QueryExecutor;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.query.QueryUtil;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.mds.util.PropertyUtil;
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
        Collection collection = (Collection) QueryExecutor.executeWithArray(query, values, restriction);

        return new ArrayList<T>(collection);
    }

    public List<T> retrieveAll(String[] properties, Object[] values, QueryParams queryParams,
                               InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, values, restriction);
        QueryUtil.setQueryParams(query, queryParams);

        Collection collection = (Collection) QueryExecutor.executeWithArray(query, values, restriction);

        return new ArrayList<T>(collection);
    }

    public List<T> retrieveAll(QueryParams queryParams, InstanceSecurityRestriction restriction) {
        Query query = createQuery(new String[0], new Object[0], restriction);
        QueryUtil.setQueryParams(query, queryParams);
        Collection collection = (Collection) QueryExecutor.execute(query, restriction);

        return new ArrayList<T>(collection);
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

        return (T) QueryExecutor.executeWithArray(query, values, restriction);
    }

    public boolean exists(String property, Object value) {
        return exists(new String[]{property}, new Object[]{value});
    }

    public boolean exists(String[] properties, Object[] values) {
        return !retrieveAll(properties, values, null).isEmpty();
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

        Object object = QueryExecutor.executeWithArray(query, values, restriction);
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
        Collection collection = (Collection) QueryExecutor.executeWithArray(query, values, restriction);

        getPersistenceManager().deletePersistentAll(collection);
    }

    public Object getDetachedField(T instance, String field) {
        T attached = getPersistenceManager().makePersistent(instance);
        return PropertyUtil.safeGetProperty(attached, field);
    }

    public long count(InstanceSecurityRestriction restriction) {
        return count(new String[0], new Object[0], restriction);
    }

    public long count(String[] properties, Object[] values, InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, values, restriction);
        QueryUtil.setCountResult(query);
        return (long) QueryExecutor.executeWithArray(query, values, restriction);
    }

    public List<T> filter(Filter filter, QueryParams queryParams) {
        return filter(filter, queryParams, null);
    }

    public List<T> filter(Filter filter, QueryParams queryParams, InstanceSecurityRestriction restriction) {
        Query query = queryForFilter(filter, queryParams, restriction);

        Collection collection = (Collection) QueryExecutor.executeWithFilter(query, filter, restriction);

        return new ArrayList<T>(collection);
    }

    public long countForFilter(Filter filter) {
        return countForFilter(filter, null);
    }

    public long countForFilter(Filter filter, InstanceSecurityRestriction restriction) {
        Query query = queryForFilter(filter, null, restriction);
        query.setResult("count(this)");

        return (long) QueryExecutor.executeWithFilter(query, filter, restriction);
    }

    private Query createQuery(String[] properties, Object[] values, InstanceSecurityRestriction restriction) {
        Query query = getPersistenceManager().newQuery(classType);
        QueryUtil.useFilter(query, properties, values, restriction);

        return query;
    }

    private Query createQuery(List<Property> properties, InstanceSecurityRestriction restriction) {
        Query query = getPersistenceManager().newQuery(classType);
        QueryUtil.useFilter(query, properties, restriction);

        return query;
    }

    private Query queryForFilter(Filter filter, QueryParams queryParams, InstanceSecurityRestriction restriction) {
        Query query = createQuery(new String[0], new Object[0], restriction);
        QueryUtil.setQueryParams(query, queryParams);
        QueryUtil.useFilter(query, filter);

        return query;
    }

    public List<T> retrieveAll(List<Property> properties, InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, restriction);

        Collection collection = (Collection) QueryExecutor.executeWithArray(query, properties);

        return new ArrayList<T>(collection);
    }

    public List<T> retrieveAll(List<Property> properties, QueryParams queryParams, InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, restriction);
        QueryUtil.setQueryParams(query, queryParams);

        Collection collection = (Collection) QueryExecutor.executeWithArray(query, properties);

        return new ArrayList<T>(collection);
    }

    public long count(List<Property> properties, InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, restriction);
        QueryUtil.setCountResult(query);

        return (long) QueryExecutor.executeWithArray(query, properties);
    }
}
