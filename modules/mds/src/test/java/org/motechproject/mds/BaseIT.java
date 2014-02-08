package org.motechproject.mds;

import org.junit.runner.RunWith;
import org.motechproject.mds.domain.AvailableFieldType;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.domain.SettingOptions;
import org.motechproject.mds.domain.TypeSettings;
import org.motechproject.mds.domain.TypeValidation;
import org.motechproject.mds.domain.ValidationCriterion;
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
        for (Entity mapping : getEntityMappings()) {
            if (className.equalsIgnoreCase(mapping.getClassName())) {
                return true;
            }
        }

        return false;
    }

    protected boolean containsLookup(String lookupName) {
        for (Lookup mapping : getLookupMappings()) {
            if (lookupName.equalsIgnoreCase(mapping.getLookupName())) {
                return true;
            }
        }

        return false;
    }

    protected List<Entity> getEntityMappings() {
        return getAll(Entity.class);
    }

    protected List<EntityDraft> getEntityDrafts() {
        return getAll(EntityDraft.class);
    }

    protected List<Field> getFieldMappings() {
        return getAll(Field.class);
    }

    protected List<Lookup> getLookupMappings() {
        return getAll(Lookup.class);
    }

    protected List<TypeSettings> getTypeSettingsMappings() {
        return getAll(TypeSettings.class);
    }

    protected List<TypeValidation> getTypeValidationMappings() {
        return getAll(TypeValidation.class);
    }

    protected List<ValidationCriterion> getValidationCriterionMappings() {
        return getAll(ValidationCriterion.class);
    }

    protected List<AvailableFieldType> getAvailableFieldTypeMappings() {
        return getAll(AvailableFieldType.class);
    }

    protected List<SettingOptions> getSettingsOptionMappings() {
        return getAll(SettingOptions.class);
    }


    protected List<RestOptions> getEntityRestOptionsMappings() {
        return getAll(RestOptions.class);
    }

    protected void clearDB() {
        getPersistenceManager().deletePersistentAll(getValidationCriterionMappings());
        getPersistenceManager().deletePersistentAll(getFieldMappings());
        getPersistenceManager().deletePersistentAll(getTypeValidationMappings());
        getPersistenceManager().deletePersistentAll(getSettingsOptionMappings());
        getPersistenceManager().deletePersistentAll(getTypeSettingsMappings());
        getPersistenceManager().deletePersistentAll(getAvailableFieldTypeMappings());
        getPersistenceManager().deletePersistentAll(getLookupMappings());
        getPersistenceManager().deletePersistentAll(getEntityDrafts());
        getPersistenceManager().deletePersistentAll(getEntityMappings());
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

    private <T> List<T> getAll(Class<T> clazz) {
        PersistenceManager persistenceManager = getPersistenceManager();
        Query query = persistenceManager.newQuery(clazz);

        return cast(clazz, (Collection) query.execute());
    }
}
