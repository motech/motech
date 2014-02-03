package org.motechproject.mds;

import org.junit.runner.RunWith;
import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.domain.RestOptionsMapping;
import org.motechproject.mds.domain.LookupMapping;
import org.motechproject.mds.domain.SettingOptionsMapping;
import org.motechproject.mds.domain.FieldMapping;
import org.motechproject.mds.domain.TypeSettingsMapping;
import org.motechproject.mds.domain.TypeValidationMapping;
import org.motechproject.mds.domain.ValidationCriterionMapping;
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
        return getAll(EntityMapping.class);
    }

    protected List<EntityDraft> getEntityDrafts() {
        return getAll(EntityDraft.class);
    }

    protected List<FieldMapping> getFieldMappings() {
        return getAll(FieldMapping.class);
    }

    protected List<LookupMapping> getLookupMappings() {
        return getAll(LookupMapping.class);
    }

    protected List<TypeSettingsMapping> getTypeSettingsMappings() {
        return getAll(TypeSettingsMapping.class);
    }

    protected List<TypeValidationMapping> getTypeValidationMappings() {
        return getAll(TypeValidationMapping.class);
    }

    protected List<ValidationCriterionMapping> getValidationCriterionMappings() {
        return getAll(ValidationCriterionMapping.class);
    }

    protected List<AvailableFieldTypeMapping> getAvailableFieldTypeMappings() {
        return getAll(AvailableFieldTypeMapping.class);
    }

    protected List<SettingOptionsMapping> getSettingsOptionMappings() {
        return getAll(SettingOptionsMapping.class);
    }


    protected List<RestOptionsMapping> getEntityRestOptionsMappings() {
        return getAll(RestOptionsMapping.class);
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
