package org.motechproject.mds;

import org.junit.runner.RunWith;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.domain.LookupMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testMdsContext.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
public abstract class BaseIT {

    private PersistenceManagerFactory persistenceManagerFactory;

    public PersistenceManagerFactory getPersistenceManagerFactory() {
        return persistenceManagerFactory;
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

    protected boolean containsEntity(String className) {
        for (EntityMapping mapping : getEntityMappings()) {
            if (className.equalsIgnoreCase(mapping.getClassName())) {
                return true;
            }
        }

        return false;
    }

    protected boolean containsLookup(String lookupName) {
        for (LookupMapping mapping : getLookupMappings()) {
            if (lookupName.equalsIgnoreCase(mapping.getLookupName())) {
                return true;
            }
        }

        return false;
    }

    protected List<EntityMapping> getEntityMappings() {
        PersistenceManager persistenceManager = getPersistenceManager();
        Query query = persistenceManager.newQuery(EntityMapping.class);

        return cast(EntityMapping.class, (Collection) query.execute());
    }

    protected List<LookupMapping> getLookupMappings() {
        PersistenceManager persistenceManager = getPersistenceManager();
        Query query = persistenceManager.newQuery(LookupMapping.class);

        return cast(LookupMapping.class, (Collection) query.execute());
    }

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
