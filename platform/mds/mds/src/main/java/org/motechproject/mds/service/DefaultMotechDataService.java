package org.motechproject.mds.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.SecurityException;
import org.motechproject.mds.filter.Filter;
import org.motechproject.mds.query.Property;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.mds.web.DraftData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.jdo.Query;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.motechproject.mds.util.SecurityUtil.getUserRoles;
import static org.motechproject.mds.util.SecurityUtil.getUsername;

/**
 * This is a basic implementation of {@link org.motechproject.mds.service.MotechDataService}. Mainly
 * it is used as super class to create a service related with the given entity schema in
 * {@link org.motechproject.mds.builder.EntityInfrastructureBuilder} but it can be also used by
 * other services inside this package.
 *
 * @param <T> the type of entity schema.
 */
@Service
public abstract class DefaultMotechDataService<T> implements MotechDataService<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String ID = "id";

    private MotechDataRepository<T> repository;
    private HistoryService historyService;
    private TrashService trashService;
    private EntityService entityService;
    private AllEntities allEntities;
    private SecurityMode securityMode;
    private Set<String> securityMembers;
    private Long schemaVersion;
    private Long entityId;
    private List<org.motechproject.mds.domain.Field> comboboxStringFields;

    @PostConstruct
    public void initializeSecurityState() {
        Class clazz = repository.getClassType();
        String name = clazz.getName();
        Entity entity = allEntities.retrieveByClassName(name);

        if (entity == null) {
            throw new EntityNotFoundException();
        }

        securityMode = entity.getSecurityMode();
        securityMembers = entity.getSecurityMembers();
        schemaVersion = entity.getEntityVersion();
        entityId = entity.getId();
        comboboxStringFields = entity.getStringComboboxFields();
    }

    @Override
    @Transactional
    public T create(T object) {
        validateCredentials();
        setOwnerCreator(object);

        T created = repository.create(object);

        if (null != entityService) {
            updateComboList(object);
        }
        historyService.record(created);

        return created;
    }

    @Override
    @Transactional
    public T retrieve(String primaryKeyName, Object value) {
        T instance = repository.retrieve(primaryKeyName, value);
        validateCredentials(instance);
        return instance;
    }

    @Override
    @Transactional
    public List<T> retrieveAll() {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.retrieveAll(securityRestriction);
    }

    @Override
    @Transactional
    public List<T> retrieveAll(QueryParams queryParams) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.retrieveAll(queryParams, securityRestriction);
    }

    @Override
    @Transactional
    public T update(T object) {
        validateCredentials(object);

        setModificationFields(object, getUsername(), DateUtil.now());

        T updated = repository.update(object);

        if (null != entityService) {
            updateComboList(object);
        }
        historyService.record(updated);

        return updated;
    }

    @Override
    @Transactional
    public void delete(T object) {
        validateCredentials(object);

        boolean trashMode = trashService.isTrashMode();
        if (trashMode) {
            // move object to trash if trash mode is active
            trashService.moveToTrash(object, schemaVersion);
        } else {
            // otherwise remove all historical data
            historyService.remove(object);
        }

        // independent of trash mode remove object. If trash mode is active then the same object
        // exists in the trash so this one is unnecessary.
        repository.delete(object);
    }

    @Override
    @Transactional
    public void delete(String primaryKeyName, Object value) {
        delete(retrieve(primaryKeyName, value));
    }

    @Override
    @Transactional
    public T findTrashInstanceById(Object instanceId, Object entityId) {
        return (T) trashService.findTrashById(instanceId, entityId);
    }

    @Override
    @Transactional
    public void revertFromTrash(Object newInstance, Object trash) {
        validateCredentials();
        trashService.moveFromTrash(repository.create((T) newInstance), trash);
    }

    @Override
    @Transactional
    public long count() {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.count(securityRestriction);
    }

    @Override
    @Transactional
    public Object getDetachedField(T instance, String fieldName) {
        return repository.getDetachedField(instance, fieldName);
    }

    @Override
    @Transactional
    public List<T> filter(Filter filter) {
        return filter(filter, null);
    }

    @Override
    @Transactional
    public List<T> filter(Filter filter, QueryParams queryParams) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.filter(filter, queryParams, securityRestriction);
    }

    @Override
    @Transactional
    public long countForFilter(Filter filter) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.countForFilter(filter, securityRestriction);
    }

    @Override
    @Transactional
    public void deleteAll() {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        repository.deleteAll(new String[0], new Object[0], securityRestriction);
    }

    @Override
    @Transactional
    public Object executeQuery(QueryExecution queryExecution) {
        Query query = repository.getPersistenceManager().newQuery(repository.getClassType());
        return queryExecution.execute(query);
    }

    protected List<T> retrieveAll(List<Property> properties) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.retrieveAll(properties, securityRestriction);
    }

    protected List<T> retrieveAll(List<Property> properties, QueryParams queryParams) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.retrieveAll(properties, queryParams, securityRestriction);
    }

    protected long count(List<Property> properties) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.count(properties, securityRestriction);
    }

    protected InstanceSecurityRestriction validateCredentials() {
        return checkNonInstanceAccess();
    }

    protected InstanceSecurityRestriction validateCredentials(T instance) {
        InstanceSecurityRestriction restriction = checkNonInstanceAccess();
        if (!restriction.isEmpty()) {
            restriction = checkInstanceAccess(instance, restriction);
        }
        return restriction;
    }

    private InstanceSecurityRestriction checkNonInstanceAccess() {
        boolean authorized = false;
        String username = getUsername();

        if (securityMode == SecurityMode.EVERYONE) {
            authorized = true;
        } else if (securityMode == SecurityMode.USERS) {
            if (securityMembers.contains(username)) {
                authorized = true;
            }
        } else if (securityMode == SecurityMode.ROLES) {
            for (String role : getUserRoles()) {
                if (securityMembers.contains(role)) {
                    authorized = true;
                }
            }
        }

        if (!authorized && !securityMode.isIntanceRestriction()) {
            throw new SecurityException();
        }

        InstanceSecurityRestriction restriction = new InstanceSecurityRestriction();
        restriction.setByOwner(securityMode == SecurityMode.OWNER);
        restriction.setByCreator(securityMode == SecurityMode.CREATOR);

        return restriction;
    }

    private InstanceSecurityRestriction checkInstanceAccess(T instance, InstanceSecurityRestriction restriction) {
        T fromDb = repository.retrieve(getId(instance));

        String creator = (String) PropertyUtil.safeGetProperty(fromDb, "creator");
        String owner = (String) PropertyUtil.safeGetProperty(fromDb, "owner");

        String username = getUsername();

        boolean authorized = false;

        if (restriction.isByOwner()) {
            authorized = StringUtils.equals(username, owner);
        } else if (restriction.isByCreator()) {
            authorized = StringUtils.equals(username, creator);
        }

        if (!authorized) {
            throw new SecurityException();
        }

        return restriction;
    }

    protected void setOwnerCreator(T instance) {
        String username = getUsername();
        DateTime now = DateUtil.now();

        PropertyUtil.safeSetProperty(instance, Constants.Util.CREATOR_FIELD_NAME, username);
        if (null == PropertyUtil.safeGetProperty(instance, Constants.Util.OWNER_FIELD_NAME)) {
            PropertyUtil.safeSetProperty(instance, Constants.Util.OWNER_FIELD_NAME, username);
        }
        PropertyUtil.safeSetProperty(instance, Constants.Util.CREATION_DATE_FIELD_NAME, now);

        setModificationFields(instance, username, now);
    }

    private void updateComboList(T instance) {
        try {
            String username = UUID.randomUUID().toString();
            EntityDraft draft = entityService.getEntityDraft(entityId, username);
            DraftData draftData = new DraftData();
            draftData.setEdit(true);

            for (org.motechproject.mds.domain.Field listField : comboboxStringFields) {
                Field field = FieldUtils.getField(instance.getClass(), listField.getName(), true);
                Object value = field.get(instance);
                org.motechproject.mds.domain.Field draftField = draft.getField(listField.getName());

                if (value != null && draftField != null) {
                    List<String> values = new ArrayList<>();
                    values.addAll(Arrays.asList(draftField.getSettings().get(0).getValue().split("\\n")));

                    Map<String, Object> draftValues = new HashMap<>();
                    draftValues.put("path", "settings.0.value");
                    draftValues.put("fieldId", draftField.getId());

                    List<List<String>> settingsArray = new ArrayList<>();

                    if ((value instanceof Collection<?>)) {
                        for (Object objectValue : ((Collection) value).toArray()) {
                            if (!values.contains(objectValue.toString())) {
                               values.add(objectValue.toString());
                            }
                        }
                        settingsArray.add(values);
                        draftValues.put("value", settingsArray);
                        draftData.setValues(draftValues);

                        entityService.saveDraftEntityChanges(entityId, draftData, username);
                    } else if (!values.contains(value.toString())) {
                        values.add(value.toString());

                        settingsArray.add(values);
                        draftValues.put("value", settingsArray);
                        draftData.setValues(draftValues);

                        entityService.saveDraftEntityChanges(entityId, draftData, username);
                    }
                }
            }

            if (draftData.getValues() != null ) {
                entityService.commitChanges(entityId, username);
            }
        } catch (IllegalAccessException e) {
            logger.error("Unable to retrieve field value", e);
        }
    }

    private void setModificationFields(T instance, String username, DateTime modificationTime) {
        PropertyUtil.safeSetProperty(instance, Constants.Util.MODIFIED_BY_FIELD_NAME, username);
        PropertyUtil.safeSetProperty(instance, Constants.Util.MODIFICATION_DATE_FIELD_NAME, modificationTime);
    }

    protected Object getId(T instance) {
        Field field = FieldUtils.getField(instance.getClass(), ID, true);
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            logger.error("Unable to retrieve object id", e);
            return null;
        }
    }

    @Autowired
    public void setRepository(MotechDataRepository<T> repository) {
        this.repository = repository;
    }

    protected MotechDataRepository<T> getRepository() {
        return repository;
    }

    @Autowired
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }

    @Autowired
    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Autowired
    public void setTrashService(TrashService trashService) {
        this.trashService = trashService;
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

}
