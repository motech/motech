package org.motechproject.mds.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.SecurityException;
import org.motechproject.mds.filter.Filter;
import org.motechproject.mds.query.Property;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.query.SqlQueryExecution;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.SecurityMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.jdo.Query;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.mds.event.CrudEventType.CREATE;
import static org.motechproject.mds.event.CrudEventType.DELETE;
import static org.motechproject.mds.event.CrudEventType.UPDATE;
import static org.motechproject.mds.util.Constants.Util.CREATOR_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFICATION_DATE_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFIED_BY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.OWNER_FIELD_NAME;
import static org.motechproject.mds.util.PropertyUtil.safeSetProperty;
import static org.motechproject.mds.util.SecurityUtil.getUserRoles;
import static org.motechproject.mds.util.SecurityUtil.getUsername;
import static org.motechproject.mds.event.CrudEventBuilder.buildEvent;

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
    private static final String ID = "id";

    private MotechDataRepository<T> repository;
    private TrashService trashService;
    private AllEntities allEntities;
    private EntityService entityService;
    private EventRelay eventRelay;
    private SecurityMode securityMode;
    private Set<String> securityMembers;
    private Long schemaVersion;
    private Long entityId;
    private List<Field> comboboxStringFields;
    private JdoTransactionManager transactionManager;
    private boolean recordHistory;
    private boolean allowCreateEvent;
    private boolean allowUpdateEvent;
    private boolean allowDeleteEvent;
    private String module;
    private String entityName;
    private String namespace;

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
        recordHistory = entity.isRecordHistory();
        allowCreateEvent = entity.isAllowCreateEvent();
        allowUpdateEvent = entity.isAllowUpdateEvent();
        allowDeleteEvent = entity.isAllowDeleteEvent();
        module = entity.getModule();
        entityName = entity.getName();
        namespace = entity.getNamespace();

        // we need the field types for handling lookups with null values
        Map<String, String> fieldTypeMap = new HashMap<>();
        for (Field field : entity.getFields()) {
            fieldTypeMap.put(field.getName(), field.getType().getTypeClassName());
        }

        repository.setFieldTypeMap(fieldTypeMap);
    }

    @Override
    public T create(final T object) {
        validateCredentials();

        T createdInstance = doInTransaction(new TransactionCallback<T>() {
            @Override
            public T doInTransaction(TransactionStatus status) {
                T created = repository.create(object);

                if (!getComboboxStringFields().isEmpty()) {
                    updateComboList(object);
                }

                return created;
            }
        });

        if (allowCreateEvent) {
            sendEvent((Long) getId(createdInstance), CREATE);
        }

        return createdInstance;
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
    public T update(final T object) {
        validateCredentials(object);

        T updatedInstance = doInTransaction(new TransactionCallback<T>() {
            @Override
            public T doInTransaction(TransactionStatus status) {
                updateModificationData(object);
                T updated = repository.update(object);

                if (!getComboboxStringFields().isEmpty()) {
                    updateComboList(object);
                }

                return updated;
            }
        });

        if (allowUpdateEvent) {
            sendEvent((Long) getId(updatedInstance), UPDATE);
        }

        return updatedInstance;
    }

    @Override
    public T updateFromTransient(T transientObject) {
        return updateFromTransient(transientObject, null);
    }

    @Override
    public T updateFromTransient(final T transientObject, final Set<String> fieldsToUpdate) {
        validateCredentials(transientObject);

        T fromDbInstance = doInTransaction(new TransactionCallback<T>() {
            @Override
            public T doInTransaction(TransactionStatus status) {
                T fromDb = findById((Long) getId(transientObject));
                if (fromDb == null) {
                    fromDb = create(transientObject);
                } else {
                    PropertyUtil.copyProperties(fromDb, transientObject, null, fieldsToUpdate);
                }

                updateModificationData(fromDb);

                if (!getComboboxStringFields().isEmpty()) {
                    updateComboList(fromDb);
                }

                return fromDb;
            }
        });

        if (allowUpdateEvent) {
            sendEvent((Long) getId(fromDbInstance), UPDATE);
        }

        return fromDbInstance;
    }

    private void updateModificationData(Object obj) {
        safeSetProperty(obj, MODIFICATION_DATE_FIELD_NAME, now());
        safeSetProperty(obj, MODIFIED_BY_FIELD_NAME, defaultIfBlank(getUsername(), ""));
    }

    @Override
    public void delete(final T object) {
        validateCredentials(object);

        Long deletedInstanceId = doInTransaction(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                // independent of trash mode remove object. If trash mode is active then the same object
                // exists in the trash so this one is unnecessary.
                // We retrieve the object using the current pm
                Long id = (Long) getId(object);
                T existing = findById(id);

                repository.delete(existing);
                return id;
            }
        });

        if (allowDeleteEvent) {
            sendEvent(deletedInstanceId, DELETE);
        }
    }

    @Override
    public void delete(String primaryKeyName, Object value) {
        delete(retrieve(primaryKeyName, value));
    }

    @Override
    @Transactional
    public T findTrashInstanceById(Object instanceId) {
        return (T) trashService.findTrashById(instanceId, getClassType().getName());
    }

    @Override
    @Transactional
    public void revertFromTrash(Object newInstance, Object trash) {
        validateCredentials();
        trashService.moveFromTrash(repository.create((T) newInstance), trash, recordHistory);
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
    public <R> R executeQuery(QueryExecution<R> queryExecution) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        Query query = repository.getPersistenceManager().newQuery(repository.getClassType());
        return queryExecution.execute(query, securityRestriction);
    }

    @Override
    public Class<T> getClassType() {
        return repository.getClassType();
    }

    @Override
    @Transactional
    public T findById(Long id) {
        if (id == null) {
            return null;
        }
        return retrieve(ID, id);
    }

    @Override
    public <R> R doInTransaction(TransactionCallback<R> transactionCallback) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return transactionTemplate.execute(transactionCallback);
    }

    @Transactional
    public <R> R executeSQLQuery(SqlQueryExecution<R> queryExecution) {
        Query query = repository.getPersistenceManager().
                newQuery(Constants.Util.SQL_QUERY, queryExecution.getSqlQuery());
        return queryExecution.execute(query);
    }

    @Override
    public Long getSchemaVersion() {
        return schemaVersion;
    }

    @Override
    public boolean recordHistory() {
        return recordHistory;
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

        String creator = (String) PropertyUtil.safeGetProperty(fromDb, CREATOR_FIELD_NAME);
        String owner = (String) PropertyUtil.safeGetProperty(fromDb, OWNER_FIELD_NAME);

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

    private void updateComboList(T instance) {
        Entity entity = allEntities.retrieveById(entityId);
        Map<String, Collection> fieldUpdateMap = new HashMap<>();

        for (Field listField : entity.getStringComboboxFields()) {
            Object value = PropertyUtil.safeGetProperty(instance, listField.getName());

            if (value != null) {
                Collection valAsColl = (value instanceof Collection) ? (Collection) value : Arrays.asList(value);
                fieldUpdateMap.put(listField.getName(), valAsColl);
            }
        }

        if (!fieldUpdateMap.isEmpty()) {
            entityService.updateComboboxValues(entityId, fieldUpdateMap);
        }
    }

    private List<Field> getComboboxStringFields() {
        if (comboboxStringFields == null) {
            comboboxStringFields = allEntities.retrieveById(entityId).getStringComboboxFields();
        }

        return comboboxStringFields;
    }

    private void sendEvent(Long id, CrudEventType action) {
        eventRelay.sendEventMessage(buildEvent(module, namespace, entityName, getClassType().getName(), action, id));
    }

    protected Object getId(T instance) {
        return PropertyUtil.safeGetProperty(instance, ID);
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
    public void setTrashService(TrashService trashService) {
        this.trashService = trashService;
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    @Autowired
    public void setEventRelay(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    @Autowired
    @Qualifier("transactionManager")
    public void setTransactionManager(JdoTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
