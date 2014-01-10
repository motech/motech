package org.motechproject.mds.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The <code>BaseMdsRepository</code> class is a base class for all repositories in mds module.
 */
public class BaseMdsRepository {
    private PersistenceManagerFactory persistenceManagerFactory;

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

    /**
     * Converts the no generic collection into generic list with the given type. In the final list
     * there will be only elements that pass the <code>instanceOf</code> test, other elements
     * are ignored.
     *
     * @param clazz      a class type used as generic list type.
     * @param collection an instance of {@link java.util.Collection} that contains objects.
     * @param <T>        a generic list type.
     * @return a instance of {@link java.util.List} that contains object that are the given type.
     */
    protected <T> List<T> cast(Class<T> clazz, Collection collection) {
        List<T> list = new ArrayList<T>(collection.size());

        for (Object obj : collection) {
            if (clazz.isInstance(obj)) {
                list.add(clazz.cast(obj));
            }
        }

        return list;
    }

}
