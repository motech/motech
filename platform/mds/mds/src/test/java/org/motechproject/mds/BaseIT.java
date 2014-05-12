package org.motechproject.mds;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
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

    @Before
    public void setUp() throws Exception {
        clearDB();
    }

    @After
    public void tearDown() throws Exception {
        clearDB();
    }

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
        for (Entity entity : getEntities()) {
            if (className.equalsIgnoreCase(entity.getClassName())) {
                return true;
            }
        }

        return false;
    }

    protected boolean containsLookup(String lookupName) {
        for (Lookup lookup : getLookups()) {
            if (lookupName.equalsIgnoreCase(lookup.getLookupName())) {
                return true;
            }
        }

        return false;
    }

    protected List<Entity> getEntities() {
        return getAll(Entity.class);
    }

    protected List<EntityDraft> getEntityDrafts() {
        return getAll(EntityDraft.class);
    }

    protected List<Field> getFields() {
        return getAll(Field.class);
    }

    protected List<Lookup> getLookups() {
        return getAll(Lookup.class);
    }

    protected void clearDB() {
        getPersistenceManager().deletePersistentAll(getFields());
        getPersistenceManager().deletePersistentAll(getLookups());
        getPersistenceManager().deletePersistentAll(getEntityDrafts());
        getPersistenceManager().deletePersistentAll(getEntities());
    }

    protected <T> List<T> cast(Class<T> clazz, Collection collection) {
        List<T> list = new ArrayList<>(collection.size());

        for (Object obj : collection) {
            if (clazz.isInstance(obj)) {
                list.add(clazz.cast(obj));
            }
        }

        return list;
    }

    protected void setProperty(Object obj, String property, Object value) throws NoSuchFieldException, IllegalAccessException {
        java.lang.reflect.Field field = obj.getClass().getDeclaredField(property);
        boolean accessible = field.isAccessible();

        try {
            field.setAccessible(true);
            field.set(obj, value);
        } finally {
            field.setAccessible(accessible);
        }
    }

    private <T> List<T> getAll(Class<T> clazz) {
        PersistenceManager persistenceManager = getPersistenceManager();
        Query query = persistenceManager.newQuery(clazz);

        return cast(clazz, (Collection) query.execute());
    }
}
