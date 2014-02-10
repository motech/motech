package org.motechproject.mds.repository;

import org.motechproject.mds.util.QueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
public abstract class MotechDataRepository<T> {
    private PersistenceManagerFactory persistenceManagerFactory;
    private Class<T> classType;

    protected MotechDataRepository(Class<T> classType) {
        this.classType = classType;
    }

    protected Class<T> getClassType() {
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

    public T create(T object) {
        return getPersistenceManager().makePersistent(object);
    }

    public List<T> retrieveAll() {
        return retrieveAll(new String[0], new Object[0]);
    }

    public List<T> retrieveAll(String property, Object value) {
        return retrieveAll(new String[]{property}, new Object[]{value});
    }

    public List<T> retrieveAll(String[] properties, Object[] values) {
        Query query = createQuery(properties, values);
        Collection collection = (Collection) query.executeWithArray(values);

        return cast(collection);
    }

    public T retrieve(String property, Object value) {
        return retrieve(new String[]{property}, new Object[]{value});
    }

    public T retrieve(String[] properties, Object[] values) {
        Query query = createQuery(properties, values);
        query.setUnique(true);

        return classType.cast(query.executeWithArray(values));
    }

    public boolean exists(String property, Object value) {
        return exists(new String[]{property}, new Object[]{value});
    }

    public boolean exists(String[] properties, Object[] values) {
        return retrieve(properties, values) != null;
    }

    public T update(T object) {
        throw new UnsupportedOperationException(
                "This method should be overridden by inherited class"
        );
    }

    public void delete(T object) {
        getPersistenceManager().deletePersistent(object);
    }

    public void delete(String property, Object value) {
        delete(new String[]{property}, new Object[]{value});
    }

    public void delete(String[] properties, Object[] values) {
        Query query = createQuery(properties, values);
        query.setUnique(true);

        Object object = query.executeWithArray(values);
        getPersistenceManager().deletePersistent(object);
    }

    public void deleteAll() {
        deleteAll(new String[0], new Object[0]);
    }

    public void deleteAll(String property, Object value) {
        deleteAll(new String[]{property}, new Object[]{value});
    }

    public void deleteAll(String[] properties, Object[] values) {
        Query query = createQuery(properties, values);
        Collection collection = (Collection) query.executeWithArray(values);

        getPersistenceManager().deletePersistentAll(collection);
    }

    /**
     * Converts the no generic collection into generic list with the given type. In the final list
     * there will be only elements that pass the <code>instanceOf</code> test, other elements
     * are ignored.
     *
     * @param collection an instance of {@link java.util.Collection} that contains objects.
     * @return a instance of {@link java.util.List} that contains object that are the given type.
     */
    protected List<T> cast(Collection collection) {
        List<T> list = new ArrayList<>(collection.size());

        for (Object obj : collection) {
            if (classType.isInstance(obj)) {
                list.add(classType.cast(obj));
            }
        }

        return list;
    }

    private Query createQuery(String[] properties, Object[] values) {
        if (properties.length != values.length) {
            throw new IllegalArgumentException("properties length must equal to values length");
        }

        Query query = getPersistenceManager().newQuery(classType);
        query.setFilter(QueryUtil.createFilter(properties));
        query.declareParameters(QueryUtil.createDeclareParameters(values));

        return query;
    }
}
