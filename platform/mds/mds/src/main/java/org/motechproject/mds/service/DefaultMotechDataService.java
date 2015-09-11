package org.motechproject.mds.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.ex.entity.EntityNotFoundException;
import org.motechproject.mds.ex.object.SecurityException;
import org.motechproject.mds.filter.Filters;
import org.motechproject.mds.util.StateManagerUtil;
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
import org.motechproject.server.osgi.event.OsgiEventProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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
import static org.motechproject.mds.event.CrudEventBuilder.buildEventParams;
import static org.motechproject.mds.event.CrudEventBuilder.createSubject;
import static org.motechproject.mds.event.CrudEventType.CREATE;
import static org.motechproject.mds.event.CrudEventType.DELETE;
import static org.motechproject.mds.event.CrudEventType.UPDATE;
import static org.motechproject.mds.util.Constants.Util.CREATOR_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFICATION_DATE_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFIED_BY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.OWNER_FIELD_NAME;
import static org.motechproject.mds.util.PropertyUtil.safeSetProperty;
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

    private MotechDataRepository<T> repository;
    private HistoryService historyService;
    private TrashService trashService;
    private AllEntities allEntities;
    private EntityService entityService;
    private OsgiEventProxy osgiEventProxy;
    private SecurityMode securityMode;
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
    private Field versionField;

    @PostConstruct
    public void initializeSecurityState() {
        Class clazz = repository.getClassType();
        String name = clazz.getName();
        Entity entity = allEntities.retrieveByClassName(name);

        if (entity == null) {
            throw new EntityNotFoundException(name);
        }

        securityMode = entity.getSecurityMode();
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
            if (field.isVersionField()) {
                versionField = field;
            }
        }

        repository.setFieldTypeMap(fieldTypeMap);
    }

    @Override
    @Transactional
    public T create(final T object) {
        validateCredentials();

        final T createdInstance = repository.create(object);

        if (!getComboboxStringFields().isEmpty()) {
            updateComboList(object);
        }

        if (recordHistory) {
            historyService.record(createdInstance);
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                if (allowCreateEvent) {
                    sendEvent((Long) getId(createdInstance), CREATE);
                }
            }
        });

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
        return repository.retrieveAll(QueryParams.ORDER_ID_ASC, securityRestriction);
    }

    @Override
    @Transactional
    public List<T> retrieveAll(QueryParams queryParams) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.retrieveAll(queryParams, securityRestriction);
    }

    @Override
    @Transactional
    public T update(final T object) {
        validateCredentials(object);

        updateModificationData(object);
        final T updatedInstance = repository.update(object);

        if (!getComboboxStringFields().isEmpty()) {
            updateComboList(object);
        }

        if (recordHistory) {
            historyService.record(updatedInstance);
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                if (allowUpdateEvent) {
                    sendEvent((Long) getId(updatedInstance), UPDATE);
                }
            }
        });

        return updatedInstance;
    }

    @Override
    @Transactional
    public T createOrUpdate (final T object) {
        final T result;

        if (getId(object) == null) {
            result = create(object);
        } else {
            result = update(object);
        }
        return result;
    }

    @Override
    @Transactional
    public T updateFromTransient(T transientObject) {
        return updateFromTransient(transientObject, null);
    }

    @Override
    @Transactional
    public T updateFromTransient(final T transientObject, final Set<String> fieldsToUpdate) {
        validateCredentials(transientObject);

        T fromDbInstance = findById((Long) getId(transientObject));
        if (fromDbInstance == null) {
            fromDbInstance = create(transientObject);
        } else {
            PropertyUtil.copyProperties(fromDbInstance, transientObject, fieldsToUpdate);
            if (versionField != null) {
                StateManagerUtil.setTransactionVersion(fromDbInstance, versionField.getName());
            }
        }

        updateModificationData(fromDbInstance);

        if (!getComboboxStringFields().isEmpty()) {
            updateComboList(fromDbInstance);
        }

        if (recordHistory) {
            historyService.record(fromDbInstance);
        }

        final T finalFromDbInstance = fromDbInstance;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                if (allowUpdateEvent) {
                    sendEvent((Long) getId(finalFromDbInstance), UPDATE);
                }
            }
        });

        return fromDbInstance;
    }

    private void updateModificationData(Object obj) {
        safeSetProperty(obj, MODIFICATION_DATE_FIELD_NAME, now());
        safeSetProperty(obj, MODIFIED_BY_FIELD_NAME, defaultIfBlank(getUsername(), ""));
    }

    @Override
    public void delete(final T object) {
        if (object == null) {
            throw new IllegalArgumentException("Unable to delete null object");
        }

        validateCredentials(object);

        Long deletedInstanceId = doInTransaction(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                boolean trashMode = trashService.isTrashMode();
                if (trashMode) {
                    // move object to trash if trash mode is active
                    trashService.moveToTrash(object, schemaVersion, recordHistory);
                } else if (recordHistory) {
                    // remove all historical data if history recording is active
                    historyService.remove(object);
                }

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
    public void deleteById(long id) {
        delete(Constants.Util.ID_FIELD_NAME, id);
    }

    @Override
    public void delete(String primaryKeyName, Object value) {
        T instance = retrieve(primaryKeyName, value);
        if (instance != null) {
            delete(instance);
        } else {
            logger.warn("Attempted to delete non-existing object with {}={}", primaryKeyName, value);
        }
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
    public List<T> filter(Filters filters, QueryParams queryParams) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.filter(filters, queryParams, securityRestriction);
    }

    @Override
    @Transactional
    public long countForFilters(Filters filters) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.countForFilters(filters, securityRestriction);
    }

    @Override
    @Transactional
    public void deleteAll() {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        repository.delete(new String[0], new Object[0], securityRestriction);
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
    public String getVersionFieldName(){
        if (versionField != null) {
            return versionField.getName();
        }
        return null;
    }

    @Override
    @Transactional
    public T findById(Long id) {
        if (id == null) {
            return null;
        }
        return retrieve(Constants.Util.ID_FIELD_NAME, id);
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
        return validateCredentials(null);
    }

    protected InstanceSecurityRestriction validateCredentials(T instance) {
        InstanceSecurityRestriction restriction = new InstanceSecurityRestriction();
        restriction.setByOwner(securityMode == SecurityMode.OWNER);
        restriction.setByCreator(securityMode == SecurityMode.CREATOR);

        if (!restriction.isEmpty() && instance != null) {
            restriction = checkInstanceAccess(instance, restriction);
        }

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
        String subject = createSubject(module, namespace, entityName, action);
        Map<String, Object> params = buildEventParams(module, namespace, entityName, getClassType().getName(), id);
        osgiEventProxy.sendEvent(subject, params);
    }

    protected Object getId(T instance) {
        return PropertyUtil.safeGetProperty(instance, Constants.Util.ID_FIELD_NAME);
    }

    protected Logger getLogger() {
        return logger;
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

    @Autowired
    public void setOsgiEventProxy(OsgiEventProxy osgiEventProxy) {
        this.osgiEventProxy = osgiEventProxy;
    }

    @Autowired
    @Qualifier("transactionManager")
    public void setTransactionManager(JdoTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
