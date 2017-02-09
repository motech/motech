package org.motechproject.mds.repository;

import org.motechproject.mds.filter.Filters;
import org.motechproject.mds.query.Property;
import org.motechproject.mds.query.QueryExecutor;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.query.QueryUtil;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.mds.util.PropertyUtil;
import org.springframework.stereotype.Repository;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
public abstract class MotechDataRepository<T> extends AbstractRepository {
    private Class<T> classType;
    private Integer fetchDepth;
    private Map<String, String> fieldTypeMap;

    protected MotechDataRepository(Class<T> classType) {
        this.classType = classType;
    }

    protected MotechDataRepository(Class<T> classType, int fetchDepth) {
        this.classType = classType;
        this.fetchDepth = fetchDepth;
    }

    public Class<T> getClassType() {
        return classType;
    }

    public void setFieldTypeMap(Map<String, String> fieldTypeMap) {
        this.fieldTypeMap = fieldTypeMap;
    }

    @Override
    public PersistenceManager getPersistenceManager() {
        PersistenceManager pm = super.getPersistenceManager();

        if (fetchDepth != null && pm != null) {
            pm.getFetchPlan().setMaxFetchDepth(fetchDepth);
        }

        return pm;
    }

    public void evictAll() {
        if (getPersistenceManagerFactory() != null) {
            getPersistenceManagerFactory().getDataStoreCache().evictAll();
        }
    }

    public void evictEntity(boolean withSubclasses) {
        if (getPersistenceManagerFactory() != null) {
            getPersistenceManagerFactory().getDataStoreCache().evictAll(withSubclasses, classType);
        }
    }

    public void evictOne(T object) {
        if (getPersistenceManagerFactory() != null) {
            getPersistenceManagerFactory().getDataStoreCache().evict(object);
        }
    }

    public T retrieve(Object key) {
        return getPersistenceManager().getObjectById(classType, key);
    }

    public List<T> retrieveAll(Collection<Long> keys) {
        Query query = getPersistenceManager().newQuery(classType);
        query.setFilter(":keys.contains(this.id)");
        Collection result = (Collection) query.execute(keys);
        return new ArrayList<>(result);
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

        return new ArrayList<>(collection);
    }

    public List<T> retrieveAll(String[] properties, Object[] values, QueryParams queryParams,
                               InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, values, restriction);
        QueryUtil.setQueryParams(query, queryParams);

        Collection collection = (Collection) QueryExecutor.executeWithArray(query, values, restriction);

        return new ArrayList<>(collection);
    }

    public List<T> retrieveAll(QueryParams queryParams, InstanceSecurityRestriction restriction) {
        Query query = createQuery(new String[0], new Object[0], restriction);
        QueryUtil.setQueryParams(query, queryParams);
        Collection collection = (Collection) QueryExecutor.execute(query, restriction);

        return new ArrayList<>(collection);
    }

    public List<T> retrieveAll(List<Property> properties, InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, restriction);

        Collection collection = (Collection) QueryExecutor.executeWithArray(query, properties);

        return new ArrayList<>(collection);
    }

    public List<T> retrieveAll(List<Property> properties, QueryParams queryParams, InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, restriction);
        QueryUtil.setQueryParams(query, queryParams);

        Collection collection = (Collection) QueryExecutor.executeWithArray(query, properties);

        return new ArrayList<>(collection);
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

    public T retrieveUnique(List<Property> properties, InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, restriction);
        query.setUnique(true);

        return (T) QueryExecutor.executeWithArray(query, properties);
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

    public long delete(String property, Object value) {
        return delete(new String[]{property}, new Object[]{value}, null);
    }

    public long delete(String property, Object value, InstanceSecurityRestriction restriction) {
        return delete(new String[]{property}, new Object[]{value}, restriction);
    }

    public long delete(String[] properties, Object[] values, InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, values, restriction);
        return QueryExecutor.executeDelete(query, values, restriction);
    }

    public T detachedCopy(T object) {
        return getPersistenceManager().detachCopy(object);
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

    public List<T> filter(Filters filters, QueryParams queryParams, InstanceSecurityRestriction restriction) {
        Query query = queryForFilters(filters, queryParams, restriction);

        Collection collection = (Collection) QueryExecutor.executeWithFilters(query, filters, restriction);

        return new ArrayList<>(collection);
    }

    public long countForFilters(Filters filters, InstanceSecurityRestriction restriction) {
        Query query = queryForFilters(filters, null, restriction);
        QueryUtil.setCountResult(query);

        return (long) QueryExecutor.executeWithFilters(query, filters, restriction);
    }

    public long count(List<Property> properties, InstanceSecurityRestriction restriction) {
        Query query = createQuery(properties, restriction);
        QueryUtil.setCountResult(query);

        return (long) QueryExecutor.executeWithArray(query, properties);
    }

    private Query createQuery(String[] properties, Object[] values, InstanceSecurityRestriction restriction) {
        Query query = getPersistenceManager().newQuery(classType);
        QueryUtil.useFilter(query, properties, values, fieldTypeMap, restriction);

        return query;
    }

    private Query createQuery(List<Property> properties, InstanceSecurityRestriction restriction) {
        Query query = getPersistenceManager().newQuery(classType);
        QueryUtil.useFilter(query, properties, restriction);

        return query;
    }

    private Query queryForFilters(Filters filters, QueryParams queryParams, InstanceSecurityRestriction restriction) {
        Query query = createQuery(new String[0], new Object[0], restriction);
        QueryUtil.setQueryParams(query, queryParams);
        QueryUtil.useFilters(query, filters);

        return query;
    }
}
